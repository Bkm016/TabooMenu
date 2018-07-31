package me.skymc.taboomenu.condition.impl;

import me.skymc.taboomenu.condition.IconCondition;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @Author sky
 * @Since 2018-07-30 22:04
 */
public class ConditionPermissionView extends IconCondition {

    @Override
    public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        return StringUtils.isBlank(icon.getPermissionView()) || (icon.getPermissionView().startsWith("-") ? !player.hasPermission(icon.getPermissionView().substring(1)) : player.hasPermission(icon.getPermissionView()));
    }

    @Override
    public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
    }

    @Override
    public boolean inView() {
        return true;
    }
}
