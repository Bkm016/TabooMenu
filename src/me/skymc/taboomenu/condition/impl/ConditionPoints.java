package me.skymc.taboomenu.condition.impl;

import me.skymc.taboomenu.condition.IconCondition;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.support.PlayerPointsBridge;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @Author sky
 * @Since 2018-07-30 21:50
 */
public class ConditionPoints extends IconCondition {

    @Override
    public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (icon.getPoints() > 0) {
            if (!PlayerPointsBridge.hasValidPlugin()) {
                player.sendMessage(ChatColor.RED + "This command has a price in points, but the plugin PlayerPoints was not found. For security, the command has been blocked. Please inform the staff.");
                return false;
            }
            if (!PlayerPointsBridge.hasPoints(player, icon.getPoints())) {
                player.sendMessage(TranslateUtils.getMessage("no-points").replace("{points}", Integer.toString(icon.getPoints())));
                return false;
            }
        }
        return true;
    }

    @Override
    public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (icon.getPoints() > 0 && !PlayerPointsBridge.takePoints(player, icon.getPoints())) {
            player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
        }
    }
}
