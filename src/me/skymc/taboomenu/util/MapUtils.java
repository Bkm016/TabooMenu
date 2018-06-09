package me.skymc.taboomenu.util;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author sky
 * @Since 2018-06-04 23:02
 */
public class MapUtils {

    public static boolean containsIgnoreCase(Map<?, ?> map, String key) {
        return map.entrySet().stream().anyMatch(entry -> key.equalsIgnoreCase(String.valueOf(entry.getKey())));
    }

    public static <T> T getOrDefaultIgnoreCase(Map<?, ?> map, String key, T def) {
        Preconditions.checkNotNull(def, "Default value cannot be null.");
        return map.entrySet().stream().filter(entry -> key.equalsIgnoreCase(String.valueOf(entry.getKey()))).findFirst().map(entry -> (T) entry.getValue()).orElse(def);
    }

    public static <T> T getOrDefault(Map<?, ?> map, Object key, T def) {
        Preconditions.checkNotNull(def, "Default value cannot be null.");
        if (map.containsKey(key)) {
            try {
                return (T) map.get(key);
            } catch (Exception ignored) {
            }
        }
        return def;
    }

    public static Map instanceMap(Map map) {
        try {
            return map.getClass().newInstance();
        } catch (Exception ignored) {
            return new HashMap<>();
        }
    }
}
