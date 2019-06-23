package me.skymc.taboomenu.handler;

import me.skymc.taboomenu.TabooMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * @Author sky
 * @Since 2018-06-17 12:06
 */
public class DataHandler {

    private static HashMap<String, Boolean> ignoredPreviousPlayers = new HashMap<>();
    private static HashMap<String, ItemStack> textureSkulls = new HashMap<>();
    private static HashMap<String, String> latestChatMessage = new HashMap<>();

    public static void ignoredPrevious(Player player) {
        ignoredPreviousPlayers.put(player.getName(), true);
        Bukkit.getScheduler().runTaskLater(TabooMenu.getInst(), () -> ignoredPreviousPlayers.remove(player.getName()), 1);
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static HashMap<String, Boolean> getIgnoredPreviousPlayers() {
        return ignoredPreviousPlayers;
    }

    public static HashMap<String, ItemStack> getTextureSkulls() {
        return textureSkulls;
    }

    public static HashMap<String, String> getLatestChatMessage() {
        return latestChatMessage;
    }

}
