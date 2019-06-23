package me.skymc.taboomenu.listener;

import me.skymc.taboomenu.handler.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @Author sky
 * @Since 2018-06-17 12:09
 */
public class ListenerChat implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (!e.isCancelled()) {
            if (e.getPlayer().hasMetadata("Prompting")) {
                e.setCancelled(true);
            }
            DataHandler.getLatestChatMessage().put(e.getPlayer().getName(), e.getMessage());
        }
    }

}
