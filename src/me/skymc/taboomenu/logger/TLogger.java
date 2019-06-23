package me.skymc.taboomenu.logger;

import me.skymc.taboomenu.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * @author sky
 */
public class TLogger {

    public static final int VERBOSE = 0;
    public static final int FINEST = 1;
    public static final int FINE = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int FATAL = 6;

    private final String pattern;
    private Plugin plugin;
    private int level;

    public String getPattern() {
        return this.pattern;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public TLogger(String pattern, Plugin plugin, int level) {
        this.pattern = pattern;
        this.plugin = plugin;
        this.level = level;
    }

    public void verbose(String msg) {
        if (this.level <= 0) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.replaceWithOrder(this.pattern, this.plugin.getName(), "§f全部", ChatColor.translateAlternateColorCodes('&', msg)));
        }

    }

    public void finest(String msg) {
        if (this.level <= 1) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.replaceWithOrder(this.pattern, this.plugin.getName(), "§e良好", ChatColor.translateAlternateColorCodes('&', msg)));
        }

    }

    public void fine(String msg) {
        if (this.level <= 2) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.replaceWithOrder(this.pattern, this.plugin.getName(), "§a正常", ChatColor.translateAlternateColorCodes('&', msg)));
        }

    }

    public void info(String msg) {
        if (this.level <= 3) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.replaceWithOrder(this.pattern, this.plugin.getName(), "§b信息", ChatColor.translateAlternateColorCodes('&', msg)));
        }

    }

    public void warn(String msg) {
        if (this.level <= 4) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.replaceWithOrder(this.pattern, this.plugin.getName(), "§6警告", "§6" + ChatColor.translateAlternateColorCodes('&', msg)));
        }

    }

    public void error(String msg) {
        if (this.level <= 5) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.replaceWithOrder(this.pattern, this.plugin.getName(), "§c错误", "§c" + ChatColor.translateAlternateColorCodes('&', msg)));
        }

    }

    public void fatal(String msg) {
        if (this.level <= 6) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.replaceWithOrder(this.pattern, this.plugin.getName(), "§4致命错误", "§4" + ChatColor.translateAlternateColorCodes('&', msg)));
        }

    }

    public static TLogger getUnformatted(Plugin plugin) {
        return new TLogger("§8[§3§l{0}§8][§r{1}§8] §f{2}", plugin, 0);
    }

}