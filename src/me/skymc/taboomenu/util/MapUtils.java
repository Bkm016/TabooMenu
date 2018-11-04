package me.skymc.taboomenu.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author sky
 * @Since 2018-06-04 23:02
 */
public class MapUtils {

    public static boolean containsSimilar(Map<?, ?> map, String key) {
        return map.entrySet().stream().anyMatch(entry -> String.valueOf(entry.getKey()).matches("^(?i)" + key));
    }

    public static Object getSimilar(Map<?, ?> map, String key) {
        return map.entrySet().stream().filter(entry -> String.valueOf(entry.getKey()).matches("^(?i)" + key)).findFirst().map(Map.Entry::getValue).orElse(null);
    }

    public static <T> T getSimilarOrDefault(Map<?, ?> map, String key, T def) {
        return map.entrySet().stream().filter(entry -> String.valueOf(entry.getKey()).matches("^(?i)" + key)).findFirst().map(entry -> (T) entry.getValue()).orElse(def);
    }

    public static <T> T getOrDefault(Map<?, ?> map, Object key, T def) {
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
