package me.skymc.taboomenu.serialize;

import com.google.common.io.Files;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.setting.IconSettings;
import me.skymc.taboomenu.setting.MenuSettings;
import me.skymc.taboomenu.util.MapUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @Author sky
 * @Since 2018-06-05 22:11
 */
public class MenuSerializer {

    private static final String MENU_SETTINGS = "(menu-)?setting(s)?";

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

        try {
            menu.getIcons().entrySet().stream().filter(x -> x.getValue().isFull()).findFirst().ifPresent(fullItem -> IntStream.range(0, menu.getRows() * 9).filter(i -> !menu.getIcons().containsKey(i)).forEach(i -> menu.getIcons().put(i, fullItem.getValue())));
        } catch (Exception e) {
            errors.add("The " + file.getName() + " has an invalid Full-Icon: " + e.toString());
        }
        return menu;
    }

    private static void loadMenuSettings(Menu menu, YamlConfiguration configuration) {
        Object settingsObject = MapUtils.getSimilarOrDefault(configuration.getValues(false), MENU_SETTINGS, new Object());
        Map settingsMap = getSettingsMap(settingsObject);
        menu.setName(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.NAME.getText(), ""));
        menu.setRows(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.ROWS.getText(), 1));
        menu.setPrevious(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.PREVIOUS.getText(), ""));
        menu.setAutoRefresh(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.AUTO_REFRESH.getText(), 0));
        menu.setPermissionBypass(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.PERMISSION_BYPASS.getText(), false));
        menu.setIgnoreCancelled(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.PERMISSION_BYPASS.getText(), false));
        Arrays.stream(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.COMMAND.getText(), "").split(";")).forEach(command -> menu.getOpenCommand().add(command.trim()));
        IconCommandSerializer.readCommands(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.OPEN_ACTION.getText(), "")).forEach(action -> menu.getOpenAction().add(action));
        IconCommandSerializer.readCommands(MapUtils.getSimilarOrDefault(settingsMap, MenuSettings.CLOSE_ACTION.getText(), "")).forEach(action -> menu.getCloseAction().add(action));
    }

    private static Map getSettingsMap(Object settingsObject) {
        Map settingsMap;
        if (settingsObject instanceof ConfigurationSection) {
            settingsMap = ((ConfigurationSection) settingsObject).getValues(false);
        } else if (settingsObject instanceof Map) {
            settingsMap = (Map) settingsObject;
        } else {
            settingsMap = new HashMap();
        }
        return settingsMap;
    }

    private static void loadMenuIcons(File file, List<String> errors, Menu menu, YamlConfiguration configuration) {
        for (String iconNode : configuration.getKeys(false)) {
            if (iconNode.matches(MENU_SETTINGS)) {
                continue;
            }

            Map<String, Object> iconMap = configuration.getConfigurationSection(iconNode).getValues(false);
            Icon icon = IconSerializer.loadIconFromMap(iconMap, iconNode, file.getName(), 0, errors);

            Integer x = null;
            Integer y = null;

            if (MapUtils.containsSimilar(iconMap, IconSettings.DEPRECATED_POSITION_X.getText())) {
                x = MapUtils.getSimilarOrDefault(iconMap, IconSettings.DEPRECATED_POSITION_X.getText(), 0);
            }
            if (MapUtils.containsSimilar(iconMap, IconSettings.DEPRECATED_POSITION_Y.getText())) {
                y = MapUtils.getSimilarOrDefault(iconMap, IconSettings.DEPRECATED_POSITION_Y.getText(), 0);
            }

            if (isNumber(iconNode)) {
                int slot = Integer.valueOf(iconNode);
                x = (slot % 9) + 1;
                y = (slot / 9) + 1;
            }

            if (icon.getSlotCopy().toArray().length > 0) {
                int slot = (int) icon.getSlotCopy().toArray()[0];
                icon.getSlotCopy().remove(slot);
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
