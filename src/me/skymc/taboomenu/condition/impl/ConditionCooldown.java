package me.skymc.taboomenu.condition.impl;

import com.google.common.collect.ImmutableMap;
import me.skymc.taboolib.database.PlayerDataManager;
import me.skymc.taboolib.timeutil.TimeFormatter;
import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.condition.IconCondition;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.event.IconClickEvent;
import me.skymc.taboomenu.event.IconLoadEvent;
import me.skymc.taboomenu.event.IconViewEvent;
import me.skymc.taboomenu.serialize.IconSerializer;
import me.skymc.taboomenu.setting.IconSettings;
import me.skymc.taboomenu.util.MapUtils;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.ChatColor;
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

    private static HashMap<String, CooldownData> iconCooldown = new HashMap<>();

    @Override
    public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (iconCooldown.containsKey(icon.getIndex())) {
            if (!TabooMenu.getInst().isTLibEnable()) {
                player.sendMessage(ChatColor.RED + "This command has a cooldown setting, but the plugin TabooLib was not found. For security, the command has been blocked. Please inform the staff.");
                return false;
            }
            CooldownData cooldownData = iconCooldown.get(icon.getIndex());
            long latestUsed = PlayerDataManager.getPlayerData(player).getLong("TabooMenu.Cooldown." + icon.getIndex(), 0L);
            if (latestUsed + cooldownData.getCooldown() > System.currentTimeMillis()) {
                TimeFormatter formatter = new TimeFormatter((latestUsed + cooldownData.getCooldown()) - System.currentTimeMillis());
                player.sendMessage(cooldownData.getMessage()
                        .replace("{day}", String.valueOf(formatter.getDays()))
                        .replace("{hour}", String.valueOf(formatter.getHours()))
                        .replace("{minute}", String.valueOf(formatter.getMinutes()))
                        .replace("{second}", String.valueOf(formatter.getSeconds()))
                        .replace("{milliseconds}", String.valueOf(formatter.getMilliseconds())));
                return false;
            }
        }
        return true;
    }

    @Override
    public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (iconCooldown.containsKey(icon.getIndex())) {
            PlayerDataManager.getPlayerData(player).set("TabooMenu.Cooldown." + icon.getIndex(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onView(IconViewEvent e) {
        CooldownData cooldownData = iconCooldown.get(e.getIcon().getIndex());
        if (cooldownData != null && cooldownData.getCooldownIcon() != null && isCooldown(e.getPlayer(), e.getIcon().getIndex(), cooldownData)) {
            e.setIcon(cooldownData.getCooldownIcon());
        }
    }

    @EventHandler
    public void onClick(IconClickEvent e) {
        CooldownData cooldownData = iconCooldown.get(e.getIcon().getIndex());
        if (cooldownData != null && cooldownData.getCooldownIcon() != null && isCooldown(e.getPlayer(), e.getIcon().getIndex(), cooldownData)) {
            e.setIcon(cooldownData.getCooldownIcon());
        }
    }

    @EventHandler
    public void onIconLoad(IconLoadEvent e) {
        if (MapUtils.containsSimilar(e.getSection(), IconSettings.COOLDOWN.getText())) {
            if (MapUtils.containsSimilar(e.getSection(), IconSettings.COOLDOWN_ITEM.getText())) {
                iconCooldown.put(e.getIcon().getIndex(), new CooldownData(MapUtils.getSimilarOrDefault(e.getSection(), IconSettings.COOLDOWN.getText(), 0), MapUtils.getSimilarOrDefault(e.getSection(), IconSettings.COOLDOWN_MESSAGE.getText(), TranslateUtils.getMessage("in-cooldown")), IconSerializer.loadIconFromMap(MapUtils.getSimilarOrDefault(e.getSection(), IconSettings.COOLDOWN_ITEM.getText(), ImmutableMap.of("", new Object())), e.getIconName() + "#CooldownIcon", e.getFileName(), e.getRequirementIndex(), e.getErrors())));
            } else {
                iconCooldown.put(e.getIcon().getIndex(), new CooldownData(MapUtils.getSimilarOrDefault(e.getSection(), IconSettings.COOLDOWN.getText(), 0), MapUtils.getSimilarOrDefault(e.getSection(), IconSettings.COOLDOWN_MESSAGE.getText(), TranslateUtils.getMessage("in-cooldown"))));
            }
        } else {
            iconCooldown.remove(e.getIcon().getIndex());
        }
    }

    public static boolean isCooldown(Player player, String index, CooldownData cooldownData) {
        return PlayerDataManager.getPlayerData(player).getLong("TabooMenu.Cooldown." + index, 0L) + cooldownData.getCooldown() > System.currentTimeMillis();
    }

    public static HashMap<String, CooldownData> getIconCooldown() {
        return iconCooldown;
    }

    public static class CooldownData {

        private int cooldown;
        private String message;
        private Icon cooldownIcon;

        public CooldownData(int cooldown, String message) {
            this(cooldown, message, null);
        }

        public CooldownData(int cooldown, String message, Icon cooldownIcon) {
            this.cooldown = cooldown;
            this.message = message;
            this.cooldownIcon = cooldownIcon;
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

        public Icon getCooldownIcon() {
            return cooldownIcon;
        }
    }
}
