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
public class ConditionLevel extends IconCondition {

    @Override
    public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (icon.getLevels() > 0) {
            if (player.getLevel() < icon.getLevels()) {
                player.sendMessage(TranslateUtils.getMessage("no-exp").replace("{levels}", Integer.toString(icon.getLevels())));
                return false;
            }
        }
        return true;
    }

    @Override
    public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (icon.getLevels() > 0) {
            player.setLevel(player.getLevel() - icon.getLevels());
        }
    }
}
