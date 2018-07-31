package me.skymc.taboomenu.condition.impl;

import me.skymc.taboomenu.condition.IconCondition;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.util.StringUtils;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @Author sky
 * @Since 2018-07-30 21:50
 */
public class ConditionPermission extends IconCondition {

    @Override
    public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (!(StringUtils.isBlank(icon.getPermission()) || (icon.getPermission().startsWith("-") ? !player.hasPermission(icon.getPermission().substring(1)) : player.hasPermission(icon.getPermission())))) {
            player.sendMessage(icon.getPermissionMessage() != null ? icon.getPermissionMessage() : TranslateUtils.getMessage("no-permission"));
            return false;
        }
        return true;
    }

    @Override
    public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
    }
}
