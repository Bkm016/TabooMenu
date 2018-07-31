package me.skymc.taboomenu.condition.impl;

import me.skymc.taboomenu.condition.IconCondition;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.event.IconLoadEvent;
import me.skymc.taboomenu.setting.IconSettings;
import me.skymc.taboomenu.util.MapUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

/**
 * @Author sky
 * @Since 2018-07-31 9:10
 */
public class ConditionCooldown extends IconCondition implements Listener {

    private HashMap<Icon, String> iconTag = new HashMap<>();
    private HashMap<String, CooldownData> iconCooldown = new HashMap<>();

    @Override
    public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        return false;
    }

    @Override
    public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {

    }

    @EventHandler
    public void onIconLoad(IconLoadEvent e) {
        if (MapUtils.containsIgnoreCase(e.getSection(), IconSettings.COOLDOWN.getText())) {
            String index = e.getFileName() + "," + e.getIconName() + "," + e.getRequirementIndex();
        }
    }

    public HashMap<Icon, String> getIconTag() {
        return iconTag;
    }

    public HashMap<String, CooldownData> getIconCooldown() {
        return iconCooldown;
    }

    public static class CooldownData {
        private String message;
        private int cooldown;

        public CooldownData(String message, int cooldown) {
            this.message = message;
            this.cooldown = cooldown;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCooldown() {
            return cooldown;
        }

        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }
    }
}
