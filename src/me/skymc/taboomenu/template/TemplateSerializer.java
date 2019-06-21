package me.skymc.taboomenu.template;

import com.google.common.collect.ImmutableMap;
import me.skymc.taboomenu.support.TabooLibHook;
import me.skymc.taboomenu.util.StringUtils;
import me.skymc.taboomenu.util.TranslateUtils;
import me.skymc.taboomenu.util.VersionUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2018-06-30 14:40
 */
public class TemplateSerializer {

    public static Map<String, Object> serialize(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", item.getType().name().toLowerCase().replace("_", " ") + (item.getDurability() > 0 ? ":" + item.getDurability() : ""));
        if (item.getAmount() > 1) {
            map.put("amount", item.getAmount());
        }
        if (itemMeta.hasDisplayName()) {
            map.put("name", TranslateUtils.uncolored(itemMeta.getDisplayName()));
        }
        if (itemMeta.hasLore()) {
            map.put("lore", TranslateUtils.uncolored(itemMeta.getLore()));
        }
        if (itemMeta.hasEnchants()) {
            map.put("shiny", true);
        }
        if (itemMeta instanceof LeatherArmorMeta) {
            map.put("color", ((LeatherArmorMeta) itemMeta).getColor().getRed() + "," + ((LeatherArmorMeta) itemMeta).getColor().getGreen() + "," + ((LeatherArmorMeta) itemMeta).getColor().getBlue());
        }
        if (itemMeta instanceof SkullMeta) {
            serializeSkullTexture(item, (SkullMeta) itemMeta, map);
        }
        if (VersionUtils.getVersionNumber() >= 11100 && itemMeta instanceof SpawnEggMeta) {
            map.put("egg-type", ((SpawnEggMeta) itemMeta).getSpawnedType().name().toLowerCase());
        }
        if (VersionUtils.getVersionNumber() >= 10900 && itemMeta instanceof PotionMeta) {
            serializePotionType(map, (PotionMeta) itemMeta);
        }
        if (VersionUtils.getVersionNumber() >= 10900 && itemMeta instanceof BannerMeta) {
            serializeBannerType((BannerMeta) itemMeta, map);
        }
        return map;
    }

    private static void serializeBannerType(BannerMeta itemMeta, Map<String, Object> map) {
        List<String> pattern = itemMeta.getPatterns().stream().map(p -> p.getColor().name().toLowerCase() + " " + p.getPattern().name().toLowerCase()).collect(Collectors.toList());
        if (itemMeta.getBaseColor() != null) {
            pattern.add(itemMeta.getBaseColor().name().toLowerCase());
        }
        map.put("banner-pattern", pattern);
    }

    private static void serializePotionType(Map<String, Object> map, PotionMeta itemMeta) {
        if (itemMeta.getBasePotionData().isExtended()) {
            map.put("potion-type", itemMeta.getBasePotionData().getType().name().toLowerCase() + "-1");
        } else if (itemMeta.getBasePotionData().isUpgraded()) {
            map.put("potion-type", itemMeta.getBasePotionData().getType().name().toLowerCase() + "-2");
        } else {
            map.put("potion-type", itemMeta.getBasePotionData().getType().name().toLowerCase());
        }
    }

    private static void serializeSkullTexture(ItemStack item, SkullMeta itemMeta, Map<String, Object> map) {
        if (!StringUtils.isBlank(itemMeta.getOwner())) {
            map.put("skull-owner", itemMeta.getOwner());
        } else if (TabooLibHook.isTabooLibEnabled()) {
            String[] skullTexture = TabooLibHook.getSkullTexture(item);
            if (skullTexture != null) {
                map.put("skull-texture", ImmutableMap.of("id", skullTexture[0], "texture", skullTexture[1]));
            }
        }
    }
}