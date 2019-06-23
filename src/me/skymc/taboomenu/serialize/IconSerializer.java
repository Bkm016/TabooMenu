package me.skymc.taboomenu.serialize;

import com.google.common.collect.ImmutableMap;
import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.IconAction;
import me.skymc.taboomenu.display.data.RequiredItem;
import me.skymc.taboomenu.display.data.Requirement;
import me.skymc.taboomenu.event.IconLoadEvent;
import me.skymc.taboomenu.handler.ScriptHandler;
import me.skymc.taboomenu.handler.itemsource.ItemSource;
import me.skymc.taboomenu.handler.itemsource.ItemSourceHandler;
import me.skymc.taboomenu.setting.IconSettings;
import me.skymc.taboomenu.util.MapUtils;
import me.skymc.taboomenu.util.StringUtils;
import me.skymc.taboomenu.util.taboolib.ItemBuilder;
import me.skymc.taboomenu.version.MaterialControl;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.NumberConversions;

import java.util.*;

/**
 * @Author sky
 * @Since 2018-06-05 21:01
 */
public class IconSerializer {

    public static boolean isAir(Material material) {
        return material == null || material == Material.AIR;
    }

    public static Material getMaterial(String origin) {
        Material material = null;
        if (!StringUtils.isInt(origin)) {
            try {
                material = MaterialControl.matchXMaterial(origin).parseMaterial();
            } catch (NullPointerException ignored) {
            }
        } else if (!MaterialControl.isNewVersion()) {
            material = MaterialControl.matchXMaterial(Integer.valueOf(origin), (byte) 0).parseMaterial();
        }
        return isAir(material) ? getMaterialSimilar(origin) : material;
    }


    public static Material getMaterialSimilar(String s) {
        String errorMaterial = s.replace(" ", "_");
        for (String alias : TabooMenu.getInst().getConfig().getConfigurationSection("Aliases").getKeys(false)) {
            if (alias.replace(" ", "_").equalsIgnoreCase(errorMaterial)) {
                return MaterialControl.matchXMaterial(TabooMenu.getInst().getConfig().getString("Aliases." + alias)).parseMaterial();
            }
        }
        return Arrays.stream(MaterialControl.VALUES)
                .filter(x -> StringUtils.similarDegree(x.name(), errorMaterial) >= TabooMenu.getInst().getConfig().getDouble("Settings.SimilarDegreeLimit"))
                .max(Comparator.comparingDouble(x -> StringUtils.similarDegree(x.name(), errorMaterial)))
                .map(MaterialControl::parseMaterial)
                .orElse(Material.BEDROCK);
    }

    public static Icon loadIconFromMap(Map<String, Object> map, String iconName, String fileName, int requirementIndex, List<String> errors) {
        String[] material = MapUtils.getSimilarOrDefault(map, IconSettings.ID.getText(), (Object) "air").toString().toUpperCase().replace(" ", "_").split(":");

        Icon icon = new Icon(getMaterial(material[0]), material.length > 1 ? NumberConversions.toShort(material[1]) : 0, MapUtils.getSimilarOrDefault(map, IconSettings.AMOUNT.getText(), 1));
        icon.setIconName(iconName);
        icon.setMenuName(fileName);
        icon.setRequirementIndex(requirementIndex);

        if (MapUtils.containsSimilar(map, IconSettings.NAME.getText())) {
            loreItemName(map, icon);
        }

        if (MapUtils.containsSimilar(map, IconSettings.LORE.getText())) {
            loadItemLore(map, icon);
        }

        if (MapUtils.containsSimilar(map, IconSettings.BANNER_PATTERN.getText())) {
            icon.setBannerPatterns(MapUtils.getSimilarOrDefault(map, IconSettings.BANNER_PATTERN.getText(), Collections.emptyList()));
        }

        if (MapUtils.containsSimilar(map, IconSettings.POTION_TYPE.getText())) {
            icon.setPotionType(MapUtils.getSimilarOrDefault(map, IconSettings.POTION_TYPE.getText(), "").toUpperCase().split("-"));
        }

        double price = MapUtils.getSimilarOrDefault(map, IconSettings.PRICE.getText(), 0).doubleValue();
        if (price > 0.0) {
            icon.setPrice(price);
        } else if (price < 0.0) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative PRICE: " + price);
        }

        int points = MapUtils.getSimilarOrDefault(map, IconSettings.POINTS.getText(), 0);
        if (points > 0.0) {
            icon.setPoints(points);
        } else if (points < 0.0) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative POINTS: " + points);
        }

        int levels = MapUtils.getSimilarOrDefault(map, IconSettings.LEVELS.getText(), 0);
        if (levels > 0.0) {
            icon.setLevels(levels);
        } else if (levels < 0.0) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative LEVELS: " + levels);
        }

        if (MapUtils.containsSimilar(map, IconSettings.COMMAND.getText())) {
            icon.getIconCommands().addAll(IconCommandSerializer.formatCommands(MapUtils.getSimilarOrDefault(map, IconSettings.COMMAND.getText(), new Object())));
        }

        if (MapUtils.containsSimilar(map, IconSettings.SLOT_COPY.getText())) {
            loadSlotCopy(map, iconName, fileName, errors, icon);
        }

        if (MapUtils.containsSimilar(map, IconSettings.REQUIRED_ITEM.getText())) {
            loadRequiredItems(map, icon);
        }

        if (MapUtils.containsSimilar(map, IconSettings.ACTION.getText())) {
            loadIconAction(map, iconName, fileName, errors, icon);
        }

        if (MapUtils.containsSimilar(map, IconSettings.SKULL_TEXTURE.getText())) {
            loadSkullTexture(map, iconName, fileName, errors, icon);
        }

        if (MapUtils.containsSimilar(map, IconSettings.ITEM_SOURCE.getText())) {
            loadItemSource(map, iconName, fileName, errors, icon);
        }

        icon.setFull(MapUtils.getSimilarOrDefault(map, IconSettings.FULL.getText(), false));
        icon.setShiny(MapUtils.getSimilarOrDefault(map, IconSettings.SHINY.getText(), false));
        icon.setColor(parseColor(MapUtils.getSimilarOrDefault(map, IconSettings.COLOR.getText(), "0,0,0"), errors));
        icon.setEggType(MapUtils.getSimilarOrDefault(map, IconSettings.EGG_TYPE.getText(), "").toUpperCase());
        icon.setSkullOwner(MapUtils.getSimilarOrDefault(map, IconSettings.SKULL_OWNER.getText(), ""));
        icon.setPermission(MapUtils.getSimilarOrDefault(map, IconSettings.PERMISSION.getText(), ""));
        icon.setPermissionView(MapUtils.getSimilarOrDefault(map, IconSettings.PERMISSION_VIEW.getText(), ""));
        icon.setPermissionMessage(MapUtils.getSimilarOrDefault(map, IconSettings.PERMISSION_MESSAGE.getText(), ""));
        icon.setHideAttribute(MapUtils.getSimilarOrDefault(map, IconSettings.HIDE_ATTRIBUTE.getText(), true));
        icon.setUnbreakable(MapUtils.getSimilarOrDefault(map, IconSettings.UNBREAKABLE.getText(), false));

        if (MapUtils.containsSimilar(map, IconSettings.DEPRECATED_DATA_VALUE.getText())) {
            icon.setData(MapUtils.getSimilarOrDefault(map, IconSettings.DEPRECATED_DATA_VALUE.getText(), 0).shortValue());
        }
        if (MapUtils.containsSimilar(map, IconSettings.REQUIREMENT.getText())) {
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
        icon.setName(MapUtils.getSimilarOrDefault(map, IconSettings.NAME.getText(), ""));
        if (!(icon.getName().startsWith("&") || icon.getName().startsWith("§"))) {
            icon.setName(TabooMenu.getInst().getConfig().getString("Settings.DefaultColor.Name", "&f") + icon.getName());
        }
    }

    private static void loadItemLore(Map<String, Object> map, Icon icon) {
        List<String> itemLore = new ArrayList<>(MapUtils.getSimilarOrDefault(map, IconSettings.LORE.getText(), Collections.emptyList()));
        for (int i = 0; i < itemLore.size(); i++) {
            String lore = itemLore.get(i);
            if (!(lore.startsWith("&") || lore.startsWith("§"))) {
                itemLore.set(i, TabooMenu.getInst().getConfig().getString("Settings.DefaultColor.Lore", "&7") + lore);
            }
        }
        icon.setLore(itemLore);
    }

    private static void loadRequiredItems(Map<String, Object> map, Icon icon) {
        Object requiredItemObject = MapUtils.getSimilar(map, IconSettings.REQUIRED_ITEM.getText());
        for (Object requiredItemOrigin : requiredItemObject instanceof List ? (List<Object>) requiredItemObject : Collections.singletonList(requiredItemObject)) {
            for (String requiredItemSource : requiredItemOrigin.toString().split(";")) {
                icon.getRequiredItems().add(RequiredItem.valueOf(requiredItemSource));
            }
        }
    }

    private static void loadSlotCopy(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        Object slotObject = MapUtils.getSimilar(map, IconSettings.SLOT_COPY.getText());
        for (Object slotString : slotObject instanceof List ? (List<String>) slotObject : Collections.singletonList(slotObject.toString())) {
            try {
                icon.getSlotCopy().add(Integer.valueOf(slotString.toString()));
            } catch (Exception e) {
                errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid SLOT-COPY: " + e.toString());
            }
        }
    }

    private static void loadItemSource(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        String[] source = MapUtils.getSimilarOrDefault(map, IconSettings.ITEM_SOURCE.getText(), "").split(":");
        if (source.length == 1) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a invalid ITEM-SOURCE: Correct format \"SOURCE:INPUT\"");
            icon.setItemSource(new ItemBuilder(Material.BEDROCK).name("&c* invalid ITEM-SOURCE *").build());
        } else if (!ItemSourceHandler.getItemSources().containsKey(source[0])) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a invalid ITEM-SOURCE: " + source[0] + " not found");
            icon.setItemSource(new ItemBuilder(Material.BEDROCK).name("&c* invalid ITEM-SOURCE *").build());
        } else {
            ItemSource itemSource = ItemSourceHandler.getItemSources().get(source[0]);
            itemSource.refreshItem(source[1], errors);
            icon.setItemSource(itemSource.getItem().containsKey(source[1]) ? itemSource.getItem().get(source[1]) : new ItemBuilder(Material.BEDROCK).name("&c* invalid ITEM-SOURCE *").build());
        }
    }

    private static void loadSkullTexture(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        Object textureObject = MapUtils.getSimilarOrDefault(map, IconSettings.SKULL_TEXTURE.getText(), new Object());
        Map textureMap = null;
        String texture;
        if (textureObject instanceof ConfigurationSection) {
            textureMap = ((ConfigurationSection) textureObject).getValues(false);
            texture = MapUtils.getSimilarOrDefault(textureMap, "texture", "");
        } else if (textureObject instanceof Map) {
            textureMap = (Map) textureObject;
            texture = MapUtils.getSimilarOrDefault(textureMap, "texture", "");
        } else {
            texture = String.valueOf(textureObject);
        }

        // icon.setSkullId(MapUtils.getSimilarOrDefault(textureMap, "id", ""));
        icon.setSkullTexture(texture);
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

    private static void loadRequirements(Map<String, Object> map, String iconName, String fileName, List<String> errors, Icon icon) {
        Object requirementObject = MapUtils.getSimilar(map, IconSettings.REQUIREMENT.getText());

        int i = 0;
        for (Object requirementOrigin : requirementObject instanceof List ? (List<Object>) requirementObject : Collections.singletonList(requirementObject)) {
            if (requirementOrigin instanceof Map) {
                String requirementIcon = iconName + "$" + i++;

                Map<String, Object> requirementMap = (Map<String, Object>) requirementOrigin;
                if (!MapUtils.containsSimilar(requirementMap, IconSettings.REQUIREMENT_EXPRESSION.getText())) {
                    errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has an invalid REQUIREMENT: EXPRESSION cannot be null.");
                    continue;
                }

                boolean preCompile = MapUtils.getSimilarOrDefault(requirementMap, IconSettings.REQUIREMENT_PRECOMPILE.getText(), false);
                int requirementPriority = MapUtils.getSimilarOrDefault(requirementMap, IconSettings.REQUIREMENT_PRIORITY.getText(), 0);
                String requirementScript = MapUtils.getSimilarOrDefault(requirementMap, IconSettings.REQUIREMENT_EXPRESSION.getText(), "");

                Icon requirementItem;
                if (MapUtils.containsSimilar(requirementMap, IconSettings.REQUIREMENT_ITEM.getText())) {
                    requirementItem = loadIconFromMap(MapUtils.getSimilarOrDefault(requirementMap, IconSettings.REQUIREMENT_ITEM.getText(), ImmutableMap.of("", new Object())), iconName, fileName, i, errors);
                } else if (MapUtils.containsSimilar(requirementMap, IconSettings.COMMAND.getText())) {
                    requirementItem = (Icon) icon.clone();
                    if (requirementItem == null) {
                        errors.add("The icon \"" + requirementIcon + "\" in the menu \"" + fileName + "\" has an invalid REQUIREMENT: ITEM clone failed.");
                        continue;
                    }
                    requirementItem.setIconCommands(IconCommandSerializer.formatCommands(MapUtils.getSimilarOrDefault(requirementMap, IconSettings.COMMAND.getText(), new Object())));
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
        Map value = MapUtils.sectionToMap(MapUtils.getSimilarOrDefault(map, IconSettings.ACTION.getText(), new Object()));
        IconAction iconAction = new IconAction();
        try {
            if (MapUtils.containsSimilar(value, IconSettings.ACTION_VIEW.getText())) {
                String expression = MapUtils.getSimilarOrDefault(value, IconSettings.ACTION_VIEW.getText(), "");
                iconAction.setViewAction(expression);
                iconAction.setViewPrecompile(MapUtils.getSimilarOrDefault(value, IconSettings.ACTION_VIEW_PRECOMPILE.getText(), false));
                if (iconAction.isViewPrecompile()) {
                    iconAction.setViewActionScript(ScriptHandler.compile(expression));
                }
            }
            if (MapUtils.containsSimilar(value, IconSettings.ACTION_CLICK.getText())) {
                String expression = MapUtils.getSimilarOrDefault(value, IconSettings.ACTION_CLICK.getText(), "");
                iconAction.setClickAction(expression);
                iconAction.setClickPrecompile(MapUtils.getSimilarOrDefault(value, IconSettings.ACTION_CLICK_PRECOMPILE.getText(), false));
                if (iconAction.isClickPrecompile()) {
                    iconAction.setClickActionScript(ScriptHandler.compile(expression));
                }
            }
        } catch (Exception e) {
            errors.add("The icon \"" + iconName + "\" in the menu \"" + fileName + "\" has a negative ACTION: " + e.toString());
        }
        icon.setIconAction(iconAction);
    }
}
