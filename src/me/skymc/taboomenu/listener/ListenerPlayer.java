package me.skymc.taboomenu.listener;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.TabooMenuAPI;
import me.skymc.taboomenu.handler.PlayerDataHandler;
import me.skymc.taboomenu.inventory.MenuHolder;
import me.skymc.taboomenu.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author sky
 * @Since 2018-06-17 12:09
 */
public class ListenerPlayer implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayerDataHandler.getIgnoredPreviousPlayers().remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!((Player) e.getPlayer()).isOnline() || PlayerDataHandler.getIgnoredPreviousPlayers().containsKey(e.getPlayer().getName())) {
            return;
        }
        if (e.getInventory().getHolder() instanceof MenuHolder && !StringUtils.isEmpty(((MenuHolder) e.getInventory().getHolder()).getMenu().getPrevious())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerDataHandler.ignoredPrevious((Player) e.getPlayer());
                    TabooMenuAPI.openMenu((Player) e.getPlayer(), ((MenuHolder) e.getInventory().getHolder()).getMenu().getPrevious(), true);
                }
            }.runTaskLater(TabooMenu.getInst(), 1);
        }
    }

}
