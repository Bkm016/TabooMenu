package me.skymc.taboomenu.listener;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.inventory.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author sky
 * @Since 2018-06-06 23:00
 */
public class ListenerInventory implements Listener {

    private Map<String, Long> antiClickSpam = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getInventory().getHolder() instanceof MenuHolder) {
            e.setCancelled(true);

            MenuHolder menuHolder = (MenuHolder) e.getInventory().getHolder();
            Icon icon = menuHolder.getMenu().getIcons().get(e.getRawSlot());

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

                if (e.getClick() == org.bukkit.event.inventory.ClickType.SHIFT_RIGHT) {
                    icon.onClick((Player) e.getWhoClicked(), ClickType.SHIFT_RIGHT);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.SHIFT_LEFT) {
                    icon.onClick((Player) e.getWhoClicked(), ClickType.SHIFT_LEFT);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.CONTROL_DROP) {
                    icon.onClick((Player) e.getWhoClicked(), ClickType.CONTROL_DROP);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.RIGHT) {
                    icon.onClick((Player) e.getWhoClicked(), ClickType.RIGHT);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.LEFT) {
                    icon.onClick((Player) e.getWhoClicked(), ClickType.LEFT);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.DROP) {
                    icon.onClick((Player) e.getWhoClicked(), ClickType.DROP);
                } else if (e.getClick() == org.bukkit.event.inventory.ClickType.MIDDLE) {
                    icon.onClick((Player) e.getWhoClicked(), ClickType.MIDDLE);
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
