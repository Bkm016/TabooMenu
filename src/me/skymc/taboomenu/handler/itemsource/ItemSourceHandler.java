package me.skymc.taboomenu.handler.itemsource;

import com.google.common.collect.Maps;
import me.skymc.taboomenu.TabooMenu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

/**
 * @Author 坏黑
 * @Since 2018-11-15 20:21
 */
public class ItemSourceHandler {

    private static Map<String, ItemSource> itemSources = Maps.newHashMap();

    public static void inst() {
        if (Bukkit.getPluginManager().getPlugin("TabooScript") != null) {
            TabooMenu.getTLogger().finest("Hooked TabooScript.");
            return;
        }
        File libs = new File(TabooMenu.getInst().getDataFolder(), "libs");
        if (!libs.exists()) {
            TabooMenu.getInst().saveResource("libs/org.codehaus.groovy-groovy-2.5.0.jar", true);
        }
        for (File file : libs.listFiles()) {
            addToPath(TabooMenu.getInst(), file);
        }
    }

    public static void refresh() {
        if (!TabooMenu.getInst().getConfig().contains("ItemSource")) {
            return;
        }
        for (String itemSourceName : TabooMenu.getInst().getConfig().getConfigurationSection("ItemSource").getKeys(false)) {
            String depend = TabooMenu.getInst().getConfig().getString("ItemSource." + itemSourceName + ".depend", "");
            String source = TabooMenu.getInst().getConfig().getString("ItemSource." + itemSourceName + ".source", "");
            itemSources.put(itemSourceName, new ItemSource(itemSourceName, depend, source));
        }
    }

    private static void addToPath(Plugin plugin, URL url) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(plugin.getClass().getClassLoader(), url);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void addToPath(Plugin plugin, File file) {
        try {
            addToPath(plugin, file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, ItemSource> getItemSources() {
        return itemSources;
    }
}
