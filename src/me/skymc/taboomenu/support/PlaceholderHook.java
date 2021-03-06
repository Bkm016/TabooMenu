package me.skymc.taboomenu.support;

import com.ilummc.tlib.resources.TLocale;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.data.RequiredItem;
import me.skymc.taboomenu.handler.ScriptHandler;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author sky
 * @Since 2018-06-13 18:20
 */
public class PlaceholderHook extends PlaceholderExpansion {

    private HashMap<String, RequiredItem> requiredItems = new HashMap<>();

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if ("money".equalsIgnoreCase(s)) {
            return EconomyBridge.formatMoney(EconomyBridge.getMoney(player));
        }
        if (s.startsWith("required-item:")) {
            return Arrays.stream(s.substring(s.indexOf(":") + 1).split(";")).map(item -> requiredItems.computeIfAbsent(item, x -> RequiredItem.valueOf(item))).allMatch(requiredItem -> requiredItem.hasItem(player)) ? "true" : "false";
        }
        if (s.startsWith("take-item:")) {
            Arrays.stream(s.substring(s.indexOf(":") + 1).split(";")).map(item -> requiredItems.computeIfAbsent(item, x -> RequiredItem.valueOf(item))).forEach(requiredItem -> requiredItem.takeItem(player));
            return "";
        }
        if (s.startsWith("js:")) {
            try {
                String value = TLocale.Translate.setPlaceholders(player, s.substring("js:".length()).replaceAll("[{}]", "%"));
                return String.valueOf(ScriptHandler.compile(value).eval());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return "<JS-ERROR>";
        }
        return "<ERROR>";
    }

    @Override
    public String getIdentifier() {
        return "taboomenu";
    }

    @Override
    public String getAuthor() {
        return "坏黑";
    }

    @Override
    public String getVersion() {
        return TabooMenu.getInst().getDescription().getVersion();
    }
}
