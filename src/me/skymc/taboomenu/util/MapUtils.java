package me.skymc.taboomenu.util;

import com.google.common.collect.Maps;
import me.skymc.taboomenu.TabooMenu;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author sky
 * @Since 2018-06-04 23:02
 */
public class MapUtils {

    public static boolean containsSimilar(Map<?, ?> map, String key) {
        return map.entrySet().stream().anyMatch(entry -> String.valueOf(entry.getKey()).matches("^(?i)" + key));
    }

    public static Object getSimilar(Map<?, ?> map, String key) {
        Optional<? extends Map.Entry<?, ?>> find = map.entrySet().stream().filter(entry -> String.valueOf(entry.getKey()).matches("^(?i)" + key)).findFirst();
        return find.map(entry -> entry.getValue() instanceof ConfigurationSection ? sectionToMap(entry.getValue()) : entry.getValue()).orElse(null);
    }

    public static <T> T getSimilarOrDefault(Map<?, ?> map, String key, T def) {
        Optional<? extends Map.Entry<?, ?>> find = map.entrySet().stream().filter(entry -> String.valueOf(entry.getKey()).matches("^(?i)" + key)).findFirst();
        if (!find.isPresent()) {
            return def;
        }
        if (find.get().getValue() instanceof ConfigurationSection && def instanceof Map) {
            return (T) sectionToMap(find.get().getValue());
        }
        return (T) find.get().getValue();
    }

    public static Map<Object, Object> instanceMap(Map map) {
        try {
            return map.getClass().newInstance();
        } catch (Exception ignored) {
            return new HashMap<>();
        }
    }

    public static Map sectionToMap(Object obj) {
        if (obj instanceof Map) {
            return (Map) obj;
        } else if (obj instanceof ConfigurationSection) {
            return ((ConfigurationSection) obj).getValues(false);
        } else {
            TabooMenu.getTLogger().info("Invalid Section type: " + obj.getClass().getName());
            return Maps.newHashMap();
        }
    }
}
