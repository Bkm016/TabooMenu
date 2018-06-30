package me.skymc.taboomenu.util;

import com.google.common.io.Files;
import me.clip.placeholderapi.PlaceholderAPI;
import me.skymc.taboomenu.TabooMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
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

    public static String uncolored(String text) {
        return text.replace("§", "&");
    }

    public static List<String> uncolored(List<String> text) {
        return text.stream().map(x -> x = uncolored(x)).collect(Collectors.toList());
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

    public static YamlConfiguration loadConfiguration(File file, List<String> errors) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(Files.toString(file, Charset.forName("utf-8")));
        } catch (InvalidConfigurationException e) {
            errors.add("The config.yml was not a valid YAML, please look at the error above, Default values will be used.");
            errors.add(e.toString());
        } catch (IOException e) {
            errors.add("I/O error while using the configuration. Default values will be used.");
            errors.add(e.toString());
        } catch (Exception e) {
            errors.add("Unhandled error while reading the values for the configuration! Please inform the developer.");
            errors.add(e.toString());
        }
        return config;
    }

    public static void printErrors(List<String> errors) {
        TabooMenu.getTLogger().error("#------------------- TabooMenu Errors -------------------#");
        int count = 1;
        for (String error : errors) {
            TabooMenu.getTLogger().error("(" + (count++) + ") &f" + error);
        }
        TabooMenu.getTLogger().error("#--------------------------------------------------------#");
    }

    public static void printErrors(CommandSender sender, List<String> errors) {
        sender.sendMessage("§c#------------------- TabooMenu Errors -------------------#");
        int count = 1;
        for (String error : errors) {
            sender.sendMessage("§c(" + (count++) + ") §f" + error);
        }
        sender.sendMessage("§c#--------------------------------------------------------#");
    }
}
