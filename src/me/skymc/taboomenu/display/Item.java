package me.skymc.taboomenu.display;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.handler.DataHandler;
import me.skymc.taboomenu.support.TabooLibHook;
import me.skymc.taboomenu.util.AttributeUtils;
import me.skymc.taboomenu.util.StringUtils;
import me.skymc.taboomenu.util.TranslateUtils;
import me.skymc.taboomenu.util.VersionUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * @Author sky
 * @Since 2018-08-01 19:47
 */
public class Item {

    private Icon icon;

    public Item(Icon icon) {
        this.icon = icon;
    }

    public ItemStack createItemStack(Player player) {
        if (icon.getMaterial() == Material.AIR && (icon.getItemSource() == null || icon.getItemSource().getType() == Material.AIR)) {
            return new ItemStack(Material.AIR);
        }

        ItemStack itemStack;
        if (icon.getItemSource() != null) {
            itemStack = icon.getItemSource().clone();
            if (icon.getMaterial() != Material.AIR) {
                itemStack.setType(icon.getMaterial());
            }
            if (itemStack.getType() == Material.AIR) {
                return new ItemStack(Material.AIR);
            }
        } else {
            itemStack = new ItemStack(icon.getMaterial());
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!StringUtils.isBlank(icon.getSkullTexture()) && TabooLibHook.isTabooLibEnabled() && itemMeta instanceof SkullMeta) {
            ItemStack finalItemStack = itemStack;
            itemStack = DataHandler.getTextureSkulls().computeIfAbsent(icon.getSkullId(), x -> TabooLibHook.setSkullTexture(finalItemStack, icon.getSkullId(), icon.getSkullTexture()));
            itemMeta = itemStack.getItemMeta();
        }

        if (!StringUtils.isBlank(icon.getName())) {
            itemMeta.setDisplayName(TranslateUtils.format(player, icon.getName()));
        }

        if (!StringUtils.isBlank(icon.getSkullOwner()) && itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwner(TranslateUtils.format(player, icon.getSkullOwner()));
        }

        if (!StringUtils.isBlank(icon.getEggType()) && VersionUtils.getVersionNumber() >= 11100 && itemMeta instanceof SpawnEggMeta) {
            formatSpawnEgg((SpawnEggMeta) itemMeta);
        }

        if (icon.getLore() != null) {
            itemMeta.setLore(TranslateUtils.format(player, icon.getLore()));
        }

        if (icon.getColor() != null && itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(icon.getColor());
        }

        if (icon.getPotionType() != null && VersionUtils.getVersionNumber() >= 10900 && itemMeta instanceof PotionMeta) {
            formatPotion((PotionMeta) itemMeta);
        }

        if (icon.getBannerPatterns() != null && VersionUtils.getVersionNumber() >= 10900 && itemMeta instanceof BannerMeta) {
            formatBanner((BannerMeta) itemMeta);
        }

        itemStack.setItemMeta(itemMeta);
        itemStack.setDurability(icon.getData());
        itemStack.setAmount(icon.getAmount());

        if (icon.isShiny()) {
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        }

        if (icon.isHideAttribute()) {
            AttributeUtils.hideAttributes(itemStack);
        }
        return itemStack;
    }

    // *********************************
    //
    //         Private Methods
    //
    // *********************************

    private void formatSpawnEgg(SpawnEggMeta itemMeta) {
        try {
            itemMeta.setSpawnedType(EntityType.valueOf(icon.getEggType()));
        } catch (Exception ignored) {
            TabooMenu.getTLogger().error(icon.getEggType() + " is an invalid entity type.");
        }
    }

    private void formatBanner(BannerMeta itemMeta) {
        for (String patternStr : icon.getBannerPatterns()) {
            String[] type = patternStr.split(" ");
            if (type.length == 1) {
                try {
                    itemMeta.setBaseColor(DyeColor.valueOf(type[0].toUpperCase()));
                } catch (Exception ignored) {
                    itemMeta.setBaseColor(DyeColor.BLACK);
                    TabooMenu.getTLogger().error(type[0] + " is an invalid color type.");
                }
            } else if (type.length == 2) {
                try {
                    itemMeta.addPattern(new Pattern(DyeColor.valueOf(type[0].toUpperCase()), PatternType.valueOf(type[1].toUpperCase())));
                } catch (Exception e) {
                    itemMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BASE));
                    TabooMenu.getTLogger().error(icon.getEggType() + " is an invalid banner type: " + e.toString());
                }
            }
        }
    }

    private void formatPotion(PotionMeta itemMeta) {
        try {
            if (icon.getPotionType().length < 2) {
                itemMeta.setBasePotionData(new PotionData(PotionType.valueOf(icon.getPotionType()[0])));
            } else if (icon.getPotionType()[1].equals("1")) {
                itemMeta.setBasePotionData(new PotionData(PotionType.valueOf(icon.getPotionType()[0]), true, false));
            } else if (icon.getPotionType()[1].equals("2")) {
                itemMeta.setBasePotionData(new PotionData(PotionType.valueOf(icon.getPotionType()[0]), false, true));
            } else {
                TabooMenu.getTLogger().error(icon.getPotionType()[1] + " is an invalid value.");
            }
        } catch (Exception e) {
            switch (e.getMessage()) {
                case "Potion Type is not upgradable":
                    TabooMenu.getTLogger().error(icon.getPotionType()[0] + " is not a upgradable potion.");
                    break;
                case "Potion Type is not extendable":
                    TabooMenu.getTLogger().error(icon.getPotionType()[0] + " is not a extendable potion.");
                    break;
                default:
                    TabooMenu.getTLogger().error(icon.getPotionType()[0] + " is an invalid potion type.");
                    break;
            }
        }
    }
}
