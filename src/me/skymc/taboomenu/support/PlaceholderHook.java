package me.skymc.taboomenu.support;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.skymc.taboomenu.display.data.RequiredItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author sky
 * @Since 2018-06-13 18:20
 */
public class PlaceholderHook extends EZPlaceholderHook {

    private HashMap<String, RequiredItem> requiredItems = new HashMap<>();

    public PlaceholderHook(Plugin plugin, String identifier) {
        super(plugin, identifier);
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (s.equals("money")) {
            return EconomyBridge.formatMoney(EconomyBridge.getMoney(player));
        }
        if (s.startsWith("required-item:")) {
            return Arrays.stream(s.substring(s.indexOf(":") + 1).split(";")).map(item -> requiredItems.computeIfAbsent(item, x -> RequiredItem.valueOf(item))).allMatch(requiredItem -> requiredItem.hasItem(player)) ? "true" : "false";
        }
        if (s.startsWith("take-item:")) {
            Arrays.stream(s.substring(s.indexOf(":") + 1).split(";")).map(item -> requiredItems.computeIfAbsent(item, x -> RequiredItem.valueOf(item))).forEach(requiredItem -> requiredItem.takeItem(player));
            return "";
        }
        return "<ERROR>";
    }
}
