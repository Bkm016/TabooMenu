package me.skymc.taboomenu.support;

import me.skymc.taboolib.itemnbtapi.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * @Author sky
 * @Since 2018-06-26 16:14
 */
public class TabooLibHook {

    private static boolean tabooLibEnabled;

    public static boolean setupTabooLib() {
        return tabooLibEnabled = Bukkit.getPluginManager().getPlugin("TabooLib") != null;
    }

    public static ItemStack setSkullTexture(ItemStack itemStack, String id, String texture) {
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound skullOwner = nbtItem.addCompound("SkullOwner");
        skullOwner.setString("Id", id);
        NBTCompound properties = skullOwner.addCompound("Properties");
        NBTList textures = properties.getList("textures", NBTType.NBTTagCompound);
        NBTListCompound nbtListCompound = textures.addCompound();
        nbtListCompound.setString("Value", texture);
        return nbtItem.getItem();
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static boolean isTabooLibEnabled() {
        return tabooLibEnabled;
    }
}
