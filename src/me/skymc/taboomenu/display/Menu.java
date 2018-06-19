package me.skymc.taboomenu.display;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.event.IconViewEvent;
import me.skymc.taboomenu.event.MenuOpenEvent;
import me.skymc.taboomenu.handler.PlayerDataHandler;
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
    private String previous;
    private List<String> openCommand = new ArrayList<>();
    private List<AbstractIconCommand> openAction = new ArrayList<>();

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
            Inventory inventory = Bukkit.createInventory(new MenuHolder(this), rows * 9, TranslateUtils.colored(name));
            setIcon(player, inventory);
            PlayerDataHandler.ignoredPrevious(player);
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
            if (entry.getValue() == null || !entry.getValue().canViewIcon(player)) {
                inventory.setItem(entry.getKey(), new ItemStack(Material.AIR));
            } else {
                IconViewEvent event = new IconViewEvent(player, this, entry.getValue());
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    Icon icon = entry.getValue().getEffectiveIcon(player, ClickType.VIEW);
                    ItemStack itemStack = icon.createItemStack(player);
                    icon.executeViewAction(player, itemStack, icon);
                    inventory.setItem(entry.getKey(), itemStack);
                }
            }
        }
        time = System.currentTimeMillis() - time;
        if (player.isOp() && player.getItemInHand().getType().equals(Material.COMMAND)) {
            player.sendMessage("§7[TabooMenu] §8Performance Mirror: §fThe calculation time of this menu items: " + time + "ms");
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
                Objects.equals(getFile(), menu.getFile()) &&
                Objects.equals(getName(), menu.getName()) &&
                Objects.equals(getOpenCommand(), menu.getOpenCommand()) &&
                Objects.equals(getOpenAction(), menu.getOpenAction()) &&
                Objects.equals(getIcons(), menu.getIcons());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFile(), getName(), getRows(), getAutoRefresh(), isPermissionBypass(), getOpenCommand(), getOpenAction(), getIcons());
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
}
