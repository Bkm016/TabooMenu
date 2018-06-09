package me.skymc.taboomenu.serialize;

import com.google.common.collect.ImmutableMap;
import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.Requirement;
import me.skymc.taboomenu.setting.IconSettings;
import me.skymc.taboomenu.util.MapUtils;
import me.skymc.taboomenu.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2018-06-05 21:01
 */
public class IconSerializer {

    public static Icon loadIconFromMap(Map<String, Object> map, String iconName, String fileName, List<String> errors) {
        String[] material = MapUtils.getOrDefaultIgnoreCase(map, IconSettings.ID.getText(), (Object) "air").toString().toUpperCase().split(":");

        Icon icon;
        try {
            icon = new Icon(Material.getMaterial(Integer.valueOf(material[0])), material.length > 1 ? Short.valueOf(material[1]) : 0, MapUtils.getOrDefault(map, IconSettings.AMOUNT.getText(), 1));
        } catch (Exception ignored) {
            try {
                icon = new Icon(Material.getMaterial(material[0].replace(" ", "_")), material.length > 1 ? Short.valueOf(material[1]) : 0, MapUtils.getOrDefault(map, IconSettings.AMOUNT.getText(), 1));
            } catch (Exception e) {
                icon = new Icon(Material.BEDROCK, (short) 0, 1);
                errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid ID: " + e.toString());
            }
        }

        if (icon.getMaterial() == null) {
            getSimilarMaterial(material[0], icon);
        }

        if (icon.getMaterial().equals(Material.AIR)) {
            icon.setMaterial(Material.BEDROCK);
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid ID: cannot used AIR");
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.NAME.getText())) {
            icon.setName(TabooMenu.getInst().getConfig().getString("Settings.DefaultColor.Name", "&f") + MapUtils.getOrDefaultIgnoreCase(map, IconSettings.NAME.getText(), ""));
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.LORE.getText())) {
            icon.setLore(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.LORE.getText(), Collections.emptyList()));
            List<String> collect = icon.getLore().stream().map(x -> TabooMenu.getInst().getConfig().getString("Settings.DefaultColor.Lore", "&7") + x).collect(Collectors.toList());
            icon.getLore().clear();
            icon.getLore().addAll(collect);
        }

        double price = MapUtils.getOrDefaultIgnoreCase(map, IconSettings.PRICE.getText(), 0).doubleValue();
        if (price > 0.0) {
            icon.setPrice(price);
        } else if (price < 0.0) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative PRICE: " + price);
        }

        int points = MapUtils.getOrDefaultIgnoreCase(map, IconSettings.POINTS.getText(), 0);
        if (points > 0.0) {
            icon.setPoints(points);
        } else if (points < 0.0) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative POINTS: " + points);
        }

        int levels = MapUtils.getOrDefaultIgnoreCase(map, IconSettings.LEVELS.getText(), 0);
        if (levels > 0.0) {
            icon.setLevels(levels);
        } else if (levels < 0.0) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative LEVELS: " + levels);
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.COMMAND.getText())) {
            icon.getIconCommands().addAll(IconCommandSerializer.formatCommands(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.COMMAND.getText(), new Object())));
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.SLOT_COPY.getText())) {
            loadSlotCopy(map, iconName, fileName, errors, icon);
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.REQUIREMENT.getText())) {
            loadRequirements(map, iconName, fileName, errors, icon);
        }

        if (!icon.getRequirements().isEmpty()) {
            icon.getRequirements().sort(Comparator.comparingInt(Requirement::getPriority));
        }

        icon.setShiny(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.SHINY.getText(), false));
        icon.setHideAttribute(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.HIDE_ATTRIBUTE.getText(), true));
        icon.setColor(parseColor(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.COLOR.getText(), "0,0,0"), errors));
        icon.setSkullOwner(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.SKULL_OWNER.getText(), ""));
        icon.setPermission(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.PERMISSION.getText(), ""));
        icon.setPermissionView(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.PERMISSION_VIEW.getText(), ""));
        icon.setPermissionMessage(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.PERMISSION_MESSAGE.getText(), ""));

        loadDeprecatedSettings(map, icon);
        return icon;
    }

    // *********************************
    //
    //         Private Methods
    //
    // *********************************

    private static void loadDeprecatedSettings(Map<String, Object> map, Icon icon) {
        if (MapUtils.containsIgnoreCase(map, IconSettings.DEPRECATED_DATA_VALUE.getText())) {
            icon.setData(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.DEPRECATED_DATA_VALUE.getText(), 0).shortValue());
        }
        if (MapUtils.containsIgnoreCase(map, IconSettings.DEPRECATED_ENCHANTMENT.getText())) {
            icon.setShiny(true);
        }
        icon.setPermissionView(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.DEPRECATED_DATA_VALUE.getText(), ""));
    }

    private static void loadSlotCopy(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        Object slotObject = map.entrySet().stream().filter(entry -> IconSettings.SLOT_COPY.getText().equalsIgnoreCase(String.valueOf(entry.getKey()))).findFirst().get().getValue();
        for (String slotString : slotObject instanceof List ? (List<String>) slotObject : Collections.singletonList(slotObject.toString())) {
            try {
                icon.getSlotCopy().add(Integer.valueOf(slotString));
            } catch (Exception e) {
                errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid SLOT-COPY: " + e.toString());
            }
        }
    }

    private static Color parseColor(String input, List<String> errors) {
        String[] split = StringUtils.stripChars(input, " ").split(",");

        if (split.length != 3) {
            errors.add(Arrays.asList(split) + " must be in the format \"red, green, blue\".");
        }

        int red = 0, green = 0, blue = 0;
        try {
            red = Integer.parseInt(split[0]);
            green = Integer.parseInt(split[1]);
            blue = Integer.parseInt(split[2]);
        } catch (NumberFormatException ex) {
            errors.add(Arrays.asList(split) + " contains invalid numbers.");
        }

        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            errors.add(Arrays.asList(split) + " should only contain numbers between 0 and 255.");
        }
        return Color.fromRGB(red, green, blue);
    }

    private static void getSimilarMaterial(String s, Icon icon) {
        String errorMaterial = s.replace(" ", "_");
        double degree = -1;
        double degreeLimit = TabooMenu.getInst().getConfig().getDouble("Settings.SimilarDegreeLimit");

        for (String alias : TabooMenu.getInst().getConfig().getConfigurationSection("Aliases").getKeys(false)) {
            if (alias.replace(" ", "_").equalsIgnoreCase(errorMaterial)) {
                degree = 1;
                try {
                    icon.setMaterial(Material.getMaterial(TabooMenu.getInst().getConfig().getString("Aliases." + alias)));
                } catch (Exception ignored) {
                }
                break;
            }
        }

        if (degree < degreeLimit) {
            for (Material materialOther : Material.values()) {
                double degreeNew = StringUtils.similarDegree(materialOther.name(), errorMaterial);
                if (degreeNew > degree) {
                    degree = degreeNew;
                    icon.setMaterial(materialOther);
                }
                if (degree >= degreeLimit) {
                    break;
                }
            }
        }
    }

    private static void loadRequirements(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        Object requirementObject = map.entrySet().stream().filter(entry -> IconSettings.REQUIREMENT.getText().equalsIgnoreCase(String.valueOf(entry.getKey()))).findFirst().get().getValue();

        int i = 0;
        for (Object requirementOrigin : requirementObject instanceof List ? (List) requirementObject : Collections.singletonList(requirementObject)) {
            if (requirementOrigin instanceof Map) {
                String requirementIcon = iconName + "$" + i++;

                Map<String, Object> requirementMap = (Map<String, Object>) requirementOrigin;
                if (!MapUtils.containsIgnoreCase(requirementMap, IconSettings.REQUIREMENT_EXPRESSION.getText())) {
                    errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid REQUIREMENT: EXPRESSION cannot be null.");
                    continue;
                }

                boolean preCompile = MapUtils.getOrDefault(requirementMap, IconSettings.REQUIREMENT_PRECOMPILE.getText(), false);
                int requirementPriority = MapUtils.getOrDefaultIgnoreCase(requirementMap, IconSettings.REQUIREMENT_PRIORITY.getText(), 0);
                String requirementScript = MapUtils.getOrDefaultIgnoreCase(requirementMap, IconSettings.REQUIREMENT_EXPRESSION.getText(), "");

                Icon requirementItem;
                if (MapUtils.containsIgnoreCase(requirementMap, IconSettings.REQUIREMENT_ITEM.getText())) {
                    requirementItem = loadIconFromMap(MapUtils.getOrDefaultIgnoreCase(requirementMap, IconSettings.REQUIREMENT_ITEM.getText(), ImmutableMap.of("", new Object())), requirementIcon, fileName, errors);
                } else if (MapUtils.containsIgnoreCase(requirementMap, IconSettings.COMMAND.getText())) {
                    requirementItem = (Icon) icon.clone();
                    if (requirementItem == null) {
                        errors.add("The icon \"" + requirementIcon + "\" in the menu \"" + fileName + "\" has an invalid REQUIREMENT: ITEM clone failed.");
                        continue;
                    }
                    requirementItem.setIconCommands(IconCommandSerializer.formatCommands(MapUtils.getOrDefaultIgnoreCase(requirementMap, IconSettings.COMMAND.getText(), new Object())));
                } else {
                    errors.add("The icon \"" + requirementIcon + "\" in the menu \"" + fileName + "\" has an invalid REQUIREMENT: ITEM cannot be null.");
                    continue;
                }

                icon.getRequirements().add(new Requirement(requirementItem, requirementPriority, requirementScript, preCompile));
            } else {
                errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid REQUIREMENT: " + requirementOrigin);
            }
        }
    }
}
