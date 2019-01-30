package me.skymc.taboomenu.listener;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.inventory.MenuHolder;
import me.skymc.taboomenu.inventory.TemplateHolder;
import me.skymc.taboomenu.template.TemplateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author sky
 * @Since 2018-06-06 23:00
 */
public class ListenerInventory implements Listener {

    private Map<String, Long> antiClickSpam = new HashMap<>();

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof MenuHolder) {
            Menu menu = ((MenuHolder) e.getInventory().getHolder()).getMenu();
            if (!menu.getCloseAction().isEmpty()) {
                menu.getCloseAction().forEach(x -> x.execute((Player) e.getPlayer()));
            }
        } else if (e.getInventory().getHolder() instanceof TemplateHolder) {
            TemplateManager.saveTemplate((Player) e.getPlayer(), ((TemplateHolder) e.getInventory().getHolder()).getTemplate(), e.getInventory());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof MenuHolder) {
            MenuHolder menuHolder = (MenuHolder) e.getInventory().getHolder();
            if (e.isCancelled() && menuHolder.getMenu().isIgnoreCancelled()) {
                return;
            }
            Icon icon = menuHolder.getMenu().getIcons().get(e.getRawSlot());
            e.setCancelled(true);

            if (icon != null) {
                int minDelay = TabooMenu.getInst().getConfig().getInt("Settings.AntiClickApamDelay");
                if (minDelay > 0) {
                    Long cd = antiClickSpam.get(e.getWhoClicked().getName());
                    long now = System.currentTimeMillis();
                    if (cd != null && cd > now) {
                        return;
                    } else {
                        antiClickSpam.put(e.getWhoClicked().getName(), now + minDelay);
                    }
                }

                long time = System.currentTimeMillis();

                if (e.getClick() == org.bukkit.event.inventory.ClickType.SHIFT_RIGHT) {
                    icon.onClick((Player) e.getWhoClicked(), e, ClickType.SHIFT_RIGHT);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.SHIFT_LEFT) {
                    icon.onClick((Player) e.getWhoClicked(), e, ClickType.SHIFT_LEFT);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.CONTROL_DROP) {
                    icon.onClick((Player) e.getWhoClicked(), e, ClickType.CONTROL_DROP);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.RIGHT) {
                    icon.onClick((Player) e.getWhoClicked(), e, ClickType.RIGHT);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.LEFT) {
                    icon.onClick((Player) e.getWhoClicked(), e, ClickType.LEFT);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.DROP) {
                    icon.onClick((Player) e.getWhoClicked(), e, ClickType.DROP);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.MIDDLE) {
                    icon.onClick((Player) e.getWhoClicked(), e, ClickType.MIDDLE);
                }

                if (e.getWhoClicked().isOp() && TabooMenu.getInst().getConfig().getBoolean("Settings.Debug")) {
                    e.getWhoClicked().sendMessage("§7[TabooMenu §8Mirror§7]: §fThe calculation time of clicked items: " + (System.currentTimeMillis() - time) + "ms");
                }

                menuHolder.getMenu().refresh((Player) e.getWhoClicked(), e.getInventory());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Menu.getRefreshTasks().remove(e.getPlayer().getName());
        antiClickSpam.remove(e.getPlayer().getName());
    }
}
