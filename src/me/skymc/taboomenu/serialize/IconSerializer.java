package me.skymc.taboomenu.serialize;

import com.google.common.collect.ImmutableMap;
import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.IconAction;
import me.skymc.taboomenu.display.data.RequiredItem;
import me.skymc.taboomenu.display.data.Requirement;
import me.skymc.taboomenu.event.IconLoadEvent;
import me.skymc.taboomenu.handler.ScriptHandler;
import me.skymc.taboomenu.setting.IconSettings;
import me.skymc.taboomenu.support.TabooLibHook;
import me.skymc.taboomenu.util.MapUtils;
import me.skymc.taboomenu.util.StringUtils;
import me.skymc.taboomenu.version.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * @Author sky
 * @Since 2018-06-05 21:01
 */
public class IconSerializer {

    public static Icon loadIconFromMap(Map<String, Object> map, String iconName, String fileName, int requirementIndex, List<String> errors) {
        String material = MapUtils.getOrDefaultIgnoreCase(map, IconSettings.ID.getText(), (Object) "air").toString().toUpperCase();
        
        Icon icon;
        try {
            icon = new Icon(MaterialUtils.getMaterial(material), MapUtils.getOrDefaultIgnoreCase(map, IconSettings.AMOUNT.getText(), 1));
        } catch (Exception ignored) {
            try {
                icon = new Icon(MaterialUtils.getMaterial(material.replace(" ", "_")), MapUtils.getOrDefaultIgnoreCase(map, IconSettings.AMOUNT.getText(), 1));
            } catch (Exception e) {
                icon = new Icon(Material.BEDROCK, (short) 0, 1);
                errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid ID: " + e.toString());
            }
        }

        icon.setIconName(iconName);
        icon.setMenuName(fileName);
        icon.setRequirementIndex(requirementIndex);

        if (icon.getMaterial() == null) {
            setSimilarMaterial(material, icon);
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.NAME.getText())) {
            loreItemName(map, icon);
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.LORE.getText())) {
            loadItemLore(map, icon);
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.BANNER_PATTERN.getText())) {
            icon.setBannerPatterns(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.BANNER_PATTERN.getText(), Collections.emptyList()));
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.POTION_TYPE.getText())) {
            icon.setPotionType(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.POTION_TYPE.getText(), "").toUpperCase().split("-"));
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

        if (MapUtils.containsIgnoreCase(map, IconSettings.REQUIRED_ITEM.getText())) {
            loadRequiredItems(map, icon);
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.ACTION.getText())) {
            loadIconAction(map, iconName, fileName, errors, icon);
        }

        if (MapUtils.containsIgnoreCase(map, IconSettings.SKULL_TEXTURE.getText())) {
            loadSkullTexture(map, iconName, fileName, errors, icon);
        }

        icon.setFull(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.FULL.getText(), false));
        icon.setShiny(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.SHINY.getText(), false));
        icon.setColor(parseColor(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.COLOR.getText(), "0,0,0"), errors));
        icon.setEggType(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.EGG_TYPE.getText(), "").toUpperCase());
        icon.setSkullOwner(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.SKULL_OWNER.getText(), ""));
        icon.setPermission(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.PERMISSION.getText(), ""));
        icon.setPermissionView(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.PERMISSION_VIEW.getText(), ""));
        icon.setPermissionMessage(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.PERMISSION_MESSAGE.getText(), ""));
        icon.setHideAttribute(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.HIDE_ATTRIBUTE.getText(), true));

        if (MapUtils.containsIgnoreCase(map, IconSettings.DEPRECATED_ENCHANTMENT.getText())) {
            icon.setShiny(true);
        }
        if (MapUtils.containsIgnoreCase(map, IconSettings.DEPRECATED_DATA_VALUE.getText())) {
            icon.setData(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.DEPRECATED_DATA_VALUE.getText(), 0).shortValue());
        }
        if (MapUtils.containsIgnoreCase(map, IconSettings.DEPRECATED_PERMISSION_VIEW.getText())) {
            icon.setPermissionView(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.DEPRECATED_PERMISSION_VIEW.getText(), ""));
        }
        if (MapUtils.containsIgnoreCase(map, IconSettings.REQUIREMENT.getText())) {
            loadRequirements(map, iconName, fileName, errors, icon);
        }

        IconLoadEvent iconLoadEvent = new IconLoadEvent(icon, iconName, fileName, requirementIndex, map, errors);
        Bukkit.getPluginManager().callEvent(iconLoadEvent);
        return iconLoadEvent.getIcon();
    }

    // *********************************
    //
    //         Private Methods
    //
    // *********************************

    private static void loreItemName(Map<String, Object> map, Icon icon) {
        icon.setName(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.NAME.getText(), ""));
        if (!(icon.getName().startsWith("&") || icon.getName().startsWith("§"))) {
            icon.setName(TabooMenu.getInst().getConfig().getString("Settings.DefaultColor.Name", "&f") + icon.getName());
        }
    }

    private static void loadItemLore(Map<String, Object> map, Icon icon) {
        List<String> itemLore = new ArrayList<>(MapUtils.getOrDefaultIgnoreCase(map, IconSettings.LORE.getText(), Collections.emptyList()));
        for (int i = 0; i < itemLore.size(); i++) {
            String lore = itemLore.get(i);
            if (!(lore.startsWith("&") || lore.startsWith("§"))) {
                itemLore.set(i, TabooMenu.getInst().getConfig().getString("Settings.DefaultColor.Lore", "&7") + lore);
            }
        }
        icon.setLore(itemLore);
    }

    private static void loadRequiredItems(Map<String, Object> map, Icon icon) {
        Object requiredItemObject = map.entrySet().stream().filter(entry -> IconSettings.REQUIRED_ITEM.getText().equalsIgnoreCase(String.valueOf(entry.getKey()))).findFirst().get().getValue();
        for (Object requiredItemOrigin : requiredItemObject instanceof List ? (List) requiredItemObject : Collections.singletonList(requiredItemObject)) {
            for (String requiredItemSource : requiredItemOrigin.toString().split(";")) {
                icon.getRequiredItems().add(RequiredItem.valueOf(requiredItemSource));
            }
        }
    }

    private static void loadSlotCopy(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        Object slotObject = map.entrySet().stream().filter(entry -> IconSettings.SLOT_COPY.getText().equalsIgnoreCase(String.valueOf(entry.getKey()))).findFirst().get().getValue();
        for (Object slotString : slotObject instanceof List ? (List) slotObject : Collections.singletonList(slotObject.toString())) {
            try {
                icon.getSlotCopy().add(Integer.valueOf(slotString.toString()));
            } catch (Exception e) {
                errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid SLOT-COPY: " + e.toString());
            }
        }
    }

    private static void loadSkullTexture(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        if (!TabooLibHook.isTabooLibEnabled()) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative SKULL-TEXTURE: cannot found TabooLib");
            return;
        }
        Object textureObject = MapUtils.getOrDefaultIgnoreCase(map, IconSettings.SKULL_TEXTURE.getText(), new Object());
        Map textureMap;
        if (textureObject instanceof ConfigurationSection) {
            textureMap = ((ConfigurationSection) textureObject).getValues(false);
        } else if (textureObject instanceof Map) {
            textureMap = (Map) textureObject;
        } else {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative SKULL-TEXTURE: not a Map");
            return;
        }
        icon.setSkullId(MapUtils.getOrDefaultIgnoreCase(textureMap, "id", ""));
        icon.setSkullTexture(MapUtils.getOrDefaultIgnoreCase(textureMap, "texture", ""));
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

    private static void setSimilarMaterial(String s, Icon icon) {
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
                    requirementItem = loadIconFromMap(MapUtils.getOrDefaultIgnoreCase(requirementMap, IconSettings.REQUIREMENT_ITEM.getText(), ImmutableMap.of("", new Object())), iconName, fileName, i, errors);
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
        if (!icon.getRequirements().isEmpty()) {
            icon.getRequirements().sort(Comparator.comparingInt(Requirement::getPriority));
        }
    }

    private static void loadIconAction(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        Object actionObject = MapUtils.getOrDefaultIgnoreCase(map, IconSettings.ACTION.getText(), new Object());
        if (actionObject instanceof Map) {
            IconAction iconAction = new IconAction();
            try {
                Map<String, Object> actionMap = ((Map) actionObject);
                if (MapUtils.containsIgnoreCase(actionMap, IconSettings.ACTION_VIEW.getText())) {
                    String expression = MapUtils.getOrDefaultIgnoreCase(actionMap, IconSettings.ACTION_VIEW.getText(), "");
                    iconAction.setViewAction(expression);
                    iconAction.setViewPrecompile(MapUtils.getOrDefaultIgnoreCase(actionMap, IconSettings.ACTION_VIEW_PRECOMPILE.getText(), false));
                    if (iconAction.isViewPrecompile()) {
                        iconAction.setViewActionScript(ScriptHandler.compile(expression));
                    }
                }
                if (MapUtils.containsIgnoreCase(actionMap, IconSettings.ACTION_CLICK.getText())) {
                    String expression = MapUtils.getOrDefaultIgnoreCase(actionMap, IconSettings.ACTION_CLICK.getText(), "");
                    iconAction.setClickAction(expression);
                    iconAction.setClickPrecompile(MapUtils.getOrDefaultIgnoreCase(actionMap, IconSettings.ACTION_CLICK_PRECOMPILE.getText(), false));
                    if (iconAction.isClickPrecompile()) {
                        iconAction.setClickActionScript(ScriptHandler.compile(expression));
                    }
                }
            } catch (Exception e) {
                errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative ACTION: " + e.toString());
            }
            icon.setIconAction(iconAction);
        } else {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative ACTION: not a Map");
        }
    }
}
