package me.skymc.taboomenu.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.skymc.taboomenu.version.MaterialControl;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * @author Arasple
 */
public class SkullUtils {

    /**
     * 取得一个自定义头颅物品
     */
    public static ItemStack getTextureSkull(String texture) {
        return setTexture(new ItemStack(MaterialControl.matchXMaterial("PLAYER_HEAD").parseMaterial()), texture);
    }

    /**
     * 设置一个头颅物品的财政
     *
     * @param skull   头颅物品
     * @param texture 目标材质
     * @return 设置后的头颅
     */
    public static ItemStack setTexture(ItemStack skull, String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        Field field;
        try {
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            profile.getProperties().put("textures", new Property("textures", texture, null));
            field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
            skull.setItemMeta(meta);
            return skull;
        } catch (ClassCastException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 取得一个头颅物品的财政
     *
     * @param skull 头颅物品
     * @return 材质
     */
    public static String getTexture(ItemStack skull) {
        GameProfile profile;
        ItemMeta meta = skull.getItemMeta();
        Field field;
        try {
            field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            profile = (GameProfile) field.get(meta);
            if (profile != null) {
                for (Property prop : profile.getProperties().values()) {
                    if ("textures".equals(prop.getName())) {
                        return prop.getValue();
                    }
                }
            }
            return null;
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
