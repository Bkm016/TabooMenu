package me.skymc.taboomenu;

import com.google.common.base.Preconditions;
import me.skymc.taboolib.database.PlayerDataManager;
import me.skymc.taboomenu.condition.impl.ConditionCooldown;
import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.inventory.MenuHolder;
import me.skymc.taboomenu.serialize.IconSerializer;
import me.skymc.taboomenu.support.TabooLibHook;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author sky
 * @Since 2018-06-06 22:34
 */
public class TabooMenuAPI {

    public enum MenuState {
        OPENED, NO_PERMISSION, MENU_NOT_FOUND, UNKNOWN
    }

    /**
     * 获取玩家当前打开的菜单
     *
     * @param player 玩家
     * @return {@link Menu}
     */
    public static Menu getPlayerCurrentMenu(Player player) {
        return player.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder ? ((MenuHolder) player.getOpenInventory().getTopInventory().getHolder()).getMenu() : null;
    }

    /**
     * 根据配置节点创建物品
     *
     * @param section 配置节点
     * @param player  玩家
     * @return {@link ItemStack}
     */
    public static ItemStack createItem(ConfigurationSection section, Player player) {
        Preconditions.checkNotNull(section, "section cannot be null.");
        return IconSerializer.loadIconFromMap(section.getValues(false), "<Unknown>", "<Unknown>", 0, Collections.emptyList()).getItem().createItemStack(player);
    }

    /**
     * 获取所有可用菜单
     *
     * @return {@link List}
     */
    public static List<String> getMenus() {
        List<String> fileName = new ArrayList<>();
        TabooMenu.getMenus().forEach(x -> fileName.add(x.getFile().getName()));
        return fileName;
    }

    /**
     * 判断物品是否在冷却中
     *
     * @param player 玩家
     * @param index  物品序号
     * @return boolean
     */
    public static Boolean isCooldown(Player player, String index) {
        if (!TabooLibHook.isTabooLibEnabled()) {
            player.sendMessage(ChatColor.RED + "This command has a cooldown setting, but the plugin TabooLib was not found. For security, the command has been blocked. Please inform the staff.");
            return false;
        }
        if (ConditionCooldown.getIconCooldown().containsKey(index)) {
            return PlayerDataManager.getPlayerData(player).getLong("TabooMenu.Cooldown." + index, 0L) + ConditionCooldown.getIconCooldown().get(index).getCooldown() > System.currentTimeMillis();
        } else {
            return false;
        }
    }

    /**
     * 打开菜单
     *
     * @param player   玩家
     * @param menuName 文件名
     * @param force    是否跳过权限判断
     * @return {@link MenuState}
     */
    public static MenuState openMenu(Player player, String menuName, boolean force) {
        String finalMenuName = menuName.endsWith(".yml") ? menuName : menuName + ".yml";
        Menu menu = TabooMenu.getMenus().stream().filter(x -> x.getFile().getName().equalsIgnoreCase(finalMenuName)).findFirst().orElse(null);
        if (menu != null) {
            if (force || menu.isPermissionBypass() || player.hasPermission(menu.getPermission())) {
                try {
                    menu.open(player);
                    return MenuState.OPENED;
                } catch (Exception ignored) {
                    return MenuState.UNKNOWN;
                }
            } else {
                player.sendMessage(TranslateUtils.getMessage("no-open-permission").replace("{permission}", menu.getPermission()));
                return MenuState.NO_PERMISSION;
            }
        } else {
            return MenuState.MENU_NOT_FOUND;
        }
    }
}
