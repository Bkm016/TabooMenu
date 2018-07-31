package me.skymc.taboomenu.condition.impl;

import me.skymc.taboomenu.condition.IconCondition;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.support.EconomyBridge;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @Author sky
 * @Since 2018-07-30 21:50
 */
public class ConditionPrice extends IconCondition {

    @Override
    public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (icon.getPrice() > 0) {
            if (!EconomyBridge.hasValidEconomy()) {
                player.sendMessage(ChatColor.RED + "This command has a price, but Vault with a compatible economy plugin was not found. For security, the command has been blocked. Please inform the staff.");
                return false;
            }
            if (!EconomyBridge.hasMoney(player, icon.getPrice())) {
                player.sendMessage(TranslateUtils.getMessage("no-money").replace("{money}", EconomyBridge.formatMoney(icon.getPrice())));
                return false;
            }
        }
        return true;
    }

    @Override
    public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (icon.getPrice() > 0 && !EconomyBridge.takeMoney(player, icon.getPrice())) {
            player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
        }
    }
}
