package me.skymc.taboomenu;

import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.inventory.MenuHolder;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author sky
 * @Since 2018-06-06 22:34
 */
public class TabooMenuAPI {

    /**
     * Open the menu for player
     *
     * @param player   Target
     * @param menuName FileName
     * @param force    Permission Bypass
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

    public static Menu getPlayerCurrentMenu(Player player) {
        return player.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder ? ((MenuHolder) player.getOpenInventory().getTopInventory().getHolder()).getMenu() : null;
    }

    public static List<String> getMenus() {
        List<String> fileName = new ArrayList<>();
        TabooMenu.getMenus().forEach(x -> fileName.add(x.getFile().getName()));
        return fileName;
    }

    public enum MenuState {
        OPENED, NO_PERMISSION, MENU_NOT_FOUND, UNKNOWN
    }
}
