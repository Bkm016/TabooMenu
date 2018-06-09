package me.skymc.taboomenu.util;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

/**
 * @Author sky
 * @Since 2018-06-06 12:47
 */
public class VersionUtils {

    private static boolean setup;
    private static boolean useReflection;
    private static Method oldGetOnlinePlayersMethod;

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static int getVersionNumber() {
        String version = getVersion();
        if (version.startsWith("v1_7")) {
            return 10700;
        } else if (version.startsWith("v1_8")) {
            return 10800;
        } else if (version.startsWith("v1_9")) {
            return 10900;
        } else if (version.startsWith("v1_10")) {
            return 11000;
        } else if (version.startsWith("v1_11")) {
            return 11100;
        } else if (version.startsWith("v1_12")) {
            return 11200;
        } else if (version.startsWith("v1_13")) {
            return 11300;
        }
        return 0;
    }

    public static String getModifiedSound(String str) {
        if (getVersionNumber() < 10900) {
            str = str.replace("BLOCK_FIRE_EXTINGUISH", "FIZZ");
            str = str.replace("BLOCK_NOTE_HAT", "NOTE_STICKS");
            str = str.replace("ENTITY_SHEEP_DEATH", "SHEEP_IDLE");
            str = str.replace("ENTITY_LLAMA_ANGRY", "HORSE_HIT");
            str = str.replace("BLOCK_BREWING_STAND_BREW", "CREEPER_HISS");
            str = str.replace("ENTITY_SHULKER_TELEPORT", "ENDERMAN_TELEPORT");
            str = str.replace("ENTITY_ZOMBIE_ATTACK_IRON_DOOR", "ZOMBIE_METAL");
            str = str.replace("BLOCK_GRAVEL_BREAK", "DIG_GRAVEL");
            str = str.replace("BLOCK_SNOW_BREAK", "DIG_SNOW");
            str = str.replace("BLOCK_GRAVEL_BREAK", "DIG_GRAVEL");
            str = str.replace("ENTITY_PLAYER_LEVELUP", "LEVEL_UP");
            str = str.replace("ENTITY_SNOWBALL_THROW", "SHOOT_ARROW");
            str = str.replace("PLAYER_ATTACK_CRIT", "ITEM_BREAK");
            str = str.replace("ENDERMEN", "ENDERMAN");
            str = str.replace("ARROW_SHOOT", "SHOOT_ARROW");
            str = str.replace("ENDERMAN_HURT", "ENDERMAN_HIT");
            str = str.replace("BLAZE_HURT", "BLAZE_HIT");
            str = str.replace("_FLAP", "_WINGS");
            str = str.replaceAll("ENTITY_|GENERIC_|BLOCK_|_AMBIENT|_BREAK|UI_BUTTON_|EXPERIENCE_", "");
        }
        return str;
    }

    public static Collection<? extends Player> getOnlinePlayers() {
        try {
            if (!setup) {
                oldGetOnlinePlayersMethod = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
                if (oldGetOnlinePlayersMethod.getReturnType() == Player[].class) {
                    useReflection = true;
                }
                setup = true;
            }
            if (!useReflection) {
                return Bukkit.getOnlinePlayers();
            } else {
                Player[] playersArray = (Player[]) oldGetOnlinePlayersMethod.invoke(null);
                return ImmutableList.copyOf(playersArray);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
