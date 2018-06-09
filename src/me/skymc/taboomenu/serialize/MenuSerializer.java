package me.skymc.taboomenu.serialize;

import com.google.common.io.Files;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.setting.IconSettings;
import me.skymc.taboomenu.setting.MenuSettings;
import me.skymc.taboomenu.util.MapUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author sky
 * @Since 2018-06-05 22:11
 */
public class MenuSerializer {

    public static Menu loadMenu(File file, List<String> errors) {
        Menu menu = new Menu(file);
        YamlConfiguration configuration = new YamlConfiguration();

        try {
            configuration.loadFromString(Files.toString(file, Charset.forName("utf-8")));
        } catch (InvalidConfigurationException e) {
            errors.add("The " + file.getName() + " was not a valid YAML, please look at the error above, Default values will be used.");
            errors.add(e.toString());
            return null;
        } catch (IOException e) {
            errors.add("I/O error while using the configuration. Default values will be used.");
            errors.add(e.toString());
            return null;
        } catch (Exception e) {
            errors.add("Unhandled error while reading the values for the configuration! Please inform the developer.");
            errors.add(e.toString());
            return null;
        }

        loadMenuIcons(file, errors, menu, configuration);
        loadMenuSettings(menu, configuration);
        return menu;
    }

    private static void loadMenuSettings(Menu menu, YamlConfiguration configuration) {
        Map<String, Object> values = configuration.getValues(false);
        Object settingsSection = MapUtils.getOrDefaultIgnoreCase(values, "menu-settings", new Object());
        if (settingsSection instanceof MemorySection) {
            Map settingsMap = ((MemorySection) settingsSection).getValues(false);
            menu.setName(MapUtils.getOrDefaultIgnoreCase(settingsMap, MenuSettings.NAME.getText(), ""));
            menu.setRows(MapUtils.getOrDefaultIgnoreCase(settingsMap, MenuSettings.ROWS.getText(), 1));
            menu.setAutoRefresh(MapUtils.getOrDefaultIgnoreCase(settingsMap, MenuSettings.AUTO_REFRESH.getText(), 0));
            menu.setPermissionBypass(MapUtils.getOrDefaultIgnoreCase(settingsMap, MenuSettings.PERMISSION_BYPASS.getText(), false));
            Arrays.stream(MapUtils.getOrDefaultIgnoreCase(settingsMap, MenuSettings.COMMAND.getText(), "").split(";")).forEach(command -> menu.getOpenCommand().add(command.trim()));
            IconCommandSerializer.readCommands(MapUtils.getOrDefaultIgnoreCase(settingsMap, MenuSettings.OPEN_ACTION.getText(), "")).forEach(action -> menu.getOpenAction().add(action));
        }

    }

    private static void loadMenuIcons(File file, List<String> errors, Menu menu, YamlConfiguration configuration) {
        for (String iconNode : configuration.getKeys(false)) {
            if (iconNode.equalsIgnoreCase("menu-settings")) {
                continue;
            }

            Map<String, Object> iconMap = configuration.getConfigurationSection(iconNode).getValues(false);
            Icon icon = IconSerializer.loadIconFromMap(iconMap, iconNode, file.getName(), errors);

            Integer x = null;
            Integer y = null;

            if (MapUtils.containsIgnoreCase(iconMap, IconSettings.DEPRECATED_POSITION_X.getText())) {
                x = MapUtils.getOrDefaultIgnoreCase(iconMap, IconSettings.DEPRECATED_POSITION_X.getText(), 0);
            }
            if (MapUtils.containsIgnoreCase(iconMap, IconSettings.DEPRECATED_POSITION_Y.getText())) {
                y = MapUtils.getOrDefaultIgnoreCase(iconMap, IconSettings.DEPRECATED_POSITION_Y.getText(), 0);
            }

            if (isNumber(iconNode)) {
                int slot = Integer.valueOf(iconNode);
                x = (slot % 9) + 1;
                y = (slot / 9) + 1;
            }

            if (x == null || y == null) {
                errors.add("The icon \"" + iconNode + "\" in the menu \"" + file.getName() + " is missing POSITION-X and/or POSITION-Y.");
                continue;
            }

            int slot = makePositive(y - 1) * 9 + makePositive(x - 1);

            if (menu.getIcons().containsKey(slot)) {
                errors.add("The icon \"" + iconNode + "\" in the menu \"" + file.getName() + " is overriding another icon with the same position.");
                continue;
            }

            menu.getIcons().put(slot, icon);

            for (int slotCopy : icon.getSlotCopy()) {
                if (menu.getIcons().containsKey(slotCopy)) {
                    errors.add("The icon \"" + iconNode + "$" + slotCopy + "\" in the menu \"" + file.getName() + " is overriding another icon with the same position.");
                } else {
                    menu.getIcons().put(slotCopy, icon);
                }
            }
        }
    }

    // *********************************
    //
    //         Private Methods
    //
    // *********************************

    private static int makePositive(int i) {
        return i < 0 ? 0 : i;
    }

    private static boolean isNumber(String origin) {
        try {
            Integer.valueOf(origin);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}