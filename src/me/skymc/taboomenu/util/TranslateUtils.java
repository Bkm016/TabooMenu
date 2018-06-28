package me.skymc.taboomenu.util;

import com.google.common.io.Files;
import me.clip.placeholderapi.PlaceholderAPI;
import me.skymc.taboomenu.TabooMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2018-06-05 20:19
 */
public class TranslateUtils {

    public static String getMessage(String message) {
        return colored(TabooMenu.getInst().getConfig().getString("Messages." + message, ""));
    }

    public static String format(Player player, String text) {
        return player == null ? colored(text) : (isPlaceholderPluginEnabled() ? PlaceholderAPI.setPlaceholders(player, text) : colored(text)).replace("{player}", player.getName());
    }

    public static List<String> format(Player player, List<String> text) {
        return player == null ? colored(text) : (isPlaceholderPluginEnabled() ? PlaceholderAPI.setPlaceholders(player, text) : colored(text)).stream().map(x -> x.replace("{player}", player.getName())).collect(Collectors.toList());
    }

    public static String colored(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> colored(List<String> text) {
        return text.stream().map(x -> x = colored(x)).collect(Collectors.toList());
    }

    public static List formatList(Object origin) {
        return origin instanceof List ? (List) origin : Collections.singletonList(origin.toString());
    }

    public static boolean isPlaceholderPluginEnabled() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        return plugin != null && plugin.isEnabled();
    }

    public static YamlConfiguration loadConfiguration(File file) {
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.loadFromString(Files.toString(file, Charset.forName("utf-8")));
            return configuration;
        } catch (Exception e) {
            return configuration;
        }
    }
}
