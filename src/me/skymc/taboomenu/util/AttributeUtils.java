package me.skymc.taboomenu.util;

import me.skymc.taboomenu.TabooMenu;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

public class AttributeUtils {

    private static boolean useItemFlags;
    private static boolean useReflection;

    private static Class<?> nbtTagCompoundClass;
    private static Class<?> nbtTagListClass;
    private static Method asNmsCopyMethod;
    private static Method asCraftMirrorMethod;
    private static Method hasTagMethod;
    private static Method getTagMethod;
    private static Method setTagMethod;
    private static Method nbtSetMethod;

    public static void setup() {
        if (isItemFlagExists()) {
            // We can use the new Bukkit API (1.8.3+)
            useItemFlags = true;
        } else {
            try {
                // Try to get the NMS methods and classes
                nbtTagCompoundClass = getNmsClass("NBTTagCompound");
                nbtTagListClass = getNmsClass("NBTTagList");
                Class<?> nmsItemStackClass = getNmsClass("ItemStack");
                asNmsCopyMethod = getObcClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
                asCraftMirrorMethod = getObcClass("inventory.CraftItemStack").getMethod("asCraftMirror", nmsItemStackClass);
                hasTagMethod = nmsItemStackClass.getMethod("hasTag");
                getTagMethod = nmsItemStackClass.getMethod("getTag");
                setTagMethod = nmsItemStackClass.getMethod("setTag", nbtTagCompoundClass);
                nbtSetMethod = nbtTagCompoundClass.getMethod("set", String.class, getNmsClass("NBTBase"));
                useReflection = true;
            } catch (Exception e) {
                TabooMenu.getTLogger().error("Could not enable the attribute remover for this version (" + e + "). Attributes will show up on items.");
            }
        }
    }

    private static Class<?> getNmsClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VersionUtils.getVersion() + "." + name);
    }

    private static Class<?> getObcClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + VersionUtils.getVersion() + "." + name);
    }

    public static void hideAttributes(ItemStack item) {
        if (item == null) {
            return;
        }
        if (useItemFlags) {
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
        } else if (useReflection) {
            try {
                Object nmsItemStack = asNmsCopyMethod.invoke(null, item);
                if (nmsItemStack == null) {
                    return;
                }

                Object nbtCompound;
                if ((Boolean) hasTagMethod.invoke(nmsItemStack)) {
                    nbtCompound = getTagMethod.invoke(nmsItemStack);
                } else {
                    nbtCompound = nbtTagCompoundClass.newInstance();
                    setTagMethod.invoke(nmsItemStack, nbtCompound);
                }

                if (nbtCompound == null) {
                    return;
                }

                Object nbtList = nbtTagListClass.newInstance();
                nbtSetMethod.invoke(nbtCompound, "AttributeModifiers", nbtList);
                asCraftMirrorMethod.invoke(null, nmsItemStack);
            } catch (Exception ignored) {
            }
        }
    }

    private static boolean isItemFlagExists() {
        try {
            Class.forName("org.bukkit.inventory.ItemFlag");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
