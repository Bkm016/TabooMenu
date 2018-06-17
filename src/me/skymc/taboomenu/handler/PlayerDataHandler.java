package me.skymc.taboomenu.handler;

import me.skymc.taboomenu.TabooMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @Author sky
 * @Since 2018-06-17 12:06
 */
public class PlayerDataHandler {

    private static HashMap<String, Boolean> ignoredPreviousPlayers = new HashMap<>();

    public static void ignoredPrevious(Player player) {
        ignoredPreviousPlayers.put(player.getName(), true);
        Bukkit.getScheduler().runTaskLater(TabooMenu.getInst(), () -> ignoredPreviousPlayers.remove(player.getName()), 1);
    }

    public static HashMap<String, Boolean> getIgnoredPreviousPlayers() {
        return ignoredPreviousPlayers;
    }
}
