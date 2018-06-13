package me.skymc.taboomenu.support;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @Author sky
 * @Since 2018-06-13 18:20
 */
public class PlaceholderHook extends EZPlaceholderHook {

    public PlaceholderHook(Plugin plugin, String identifier) {
        super(plugin, identifier);
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (s.equals("money")) {
            return EconomyBridge.formatMoney(EconomyBridge.getMoney(player));
        }
        return null;
    }
}
