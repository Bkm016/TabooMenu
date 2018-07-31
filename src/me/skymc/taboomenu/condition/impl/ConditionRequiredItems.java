package me.skymc.taboomenu.condition.impl;

import me.skymc.taboomenu.condition.IconCondition;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @Author sky
 * @Since 2018-07-30 21:50
 */
public class ConditionRequiredItems extends IconCondition {

    @Override
    public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (!icon.getRequiredItems().isEmpty()) {
            if (icon.getRequiredItems().stream().anyMatch(x -> !x.hasItem(player))) {
                player.sendMessage(TranslateUtils.getMessage("no-required-item"));
                return false;
            }
        }
        return true;
    }

    @Override
    public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (!icon.getRequiredItems().isEmpty()) {
            icon.getRequiredItems().forEach(x -> x.takeItem(player));
        }
    }
}
