package me.skymc.taboomenu.display;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.event.IconViewEvent;
import me.skymc.taboomenu.event.MenuOpenEvent;
import me.skymc.taboomenu.handler.DataHandler;
import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.inventory.MenuHolder;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

/**
 * @Author sky
 * @Since 2018-06-05 20:05
 */
public class Menu {

    private static HashMap<String, BukkitTask> refreshTasks = new HashMap<>();

    public static HashMap<String, BukkitTask> getRefreshTasks() {
        return refreshTasks;
    }

    private File file;
    private String name;
    private int rows;
    private int autoRefresh;
    private boolean permissionBypass;
    private boolean ignoreCancelled;
    private String previous;
    private List<String> openCommand = new ArrayList<>();
    private List<AbstractIconCommand> openAction = new ArrayList<>();
    private List<AbstractIconCommand> closeAction = new ArrayList<>();

    private HashMap<Integer, Icon> icons = new HashMap<>();

    public Menu(File file) {
        this.file = file;
    }

    public void open(Player player) {
        new MenuOpenEvent(player, this).call().getMenu().openSilent(player);
    }

    public void openSilent(Player player) {
        try {
            if (!openAction.isEmpty()) {
                openAction.forEach(openAction -> openAction.execute(player));
            }
            Inventory inventory = Bukkit.createInventory(new MenuHolder(this), rows * 9, TranslateUtils.format(player, name));
            setIcon(player, inventory);
            DataHandler.ignoredPrevious(player);
            player.openInventory(inventory);
        } catch (Exception e) {
            player.sendMessage("§cAn internal error occurred while opening the menu:");
            player.sendMessage("§c" + e.toString());
        }
    }

    public void setIcon(Player player, Inventory inventory) {
        refresh(player, inventory);
        if (autoRefresh > 0) {
            Optional.ofNullable(refreshTasks.put(player.getName(), new BukkitRunnable() {

                @Override
                public void run() {
                    if (player.getOpenInventory().getTopInventory().equals(inventory)) {
                        refresh(player, inventory);
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(TabooMenu.getInst(), autoRefresh * 20, autoRefresh * 20))).ifPresent(BukkitTask::cancel);
        }
    }

    public void refresh(Player player, Inventory inventory) {
        long time = System.currentTimeMillis();
        for (Map.Entry<Integer, Icon> entry : icons.entrySet()) {
            if (entry.getValue() == null || !entry.getValue().canIconView(player)) {
                inventory.setItem(entry.getKey(), new ItemStack(Material.AIR));
            } else if (entry.getKey() < inventory.getSize()) {
                Icon icon = entry.getValue().getEffectiveIcon(player, ClickType.VIEW);
                IconViewEvent event = new IconViewEvent(player, this, icon);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    ItemStack itemStack = event.getIcon().getItem().createItemStack(player);
                    event.getIcon().executeViewAction(player, itemStack, event.getIcon());
                    inventory.setItem(entry.getKey(), itemStack);
                }
            }
        }
        if (player.isOp() && TabooMenu.getInst().getConfig().getBoolean("Settings.Debug")) {
            player.sendMessage("§7[TabooMenu] §fThe calculation time of refresh items: " + (System.currentTimeMillis() - time) + "ms");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Menu)) {
            return false;
        }
        Menu menu = (Menu) o;
        return getRows() == menu.getRows() &&
                getAutoRefresh() == menu.getAutoRefresh() &&
                isPermissionBypass() == menu.isPermissionBypass() &&
                ignoreCancelled == menu.ignoreCancelled &&
                Objects.equals(getFile(), menu.getFile()) &&
                Objects.equals(getName(), menu.getName()) &&
                Objects.equals(getPrevious(), menu.getPrevious()) &&
                Objects.equals(getOpenCommand(), menu.getOpenCommand()) &&
                Objects.equals(getOpenAction(), menu.getOpenAction()) &&
                Objects.equals(getCloseAction(), menu.getCloseAction()) &&
                Objects.equals(getIcons(), menu.getIcons());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFile(), getName(), getRows(), getAutoRefresh(), isPermissionBypass(), ignoreCancelled, getPrevious(), getOpenCommand(), getOpenAction(), getCloseAction(), getIcons());
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public File getFile() {
        return file;
    }

    public HashMap<Integer, Icon> getIcons() {
        return icons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(int autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    public boolean isPermissionBypass() {
        return permissionBypass;
    }

    public void setPermissionBypass(boolean permissionBypass) {
        this.permissionBypass = permissionBypass;
    }

    public List<AbstractIconCommand> getOpenAction() {
        return openAction;
    }

    public List<AbstractIconCommand> getCloseAction() {
        return closeAction;
    }

    public List<String> getOpenCommand() {
        return openCommand;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getPermission() {
        return "taboomenu.open." + file.getName();
    }

    public boolean isIgnoreCancelled() {
        return ignoreCancelled;
    }

    public void setIgnoreCancelled(boolean ignoreCancelled) {
        this.ignoreCancelled = ignoreCancelled;
    }
}
