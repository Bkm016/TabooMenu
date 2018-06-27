package me.skymc.taboomenu;

import com.google.common.io.Files;
import me.skymc.taboomenu.bstats.Metrics;
import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.handler.DataHandler;
import me.skymc.taboomenu.handler.JavaScriptHandler;
import me.skymc.taboomenu.inventory.MenuHolder;
import me.skymc.taboomenu.listener.ListenerCommand;
import me.skymc.taboomenu.listener.ListenerInventory;
import me.skymc.taboomenu.listener.ListenerPlayer;
import me.skymc.taboomenu.logger.TLogger;
import me.skymc.taboomenu.serialize.MenuSerializer;
import me.skymc.taboomenu.support.EconomyBridge;
import me.skymc.taboomenu.support.PlaceholderHook;
import me.skymc.taboomenu.support.PlayerPointsBridge;
import me.skymc.taboomenu.support.TabooLibHook;
import me.skymc.taboomenu.util.AttributeUtils;
import me.skymc.taboomenu.util.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author sky
 * @Since 2018-06-05 18:40
 */
public class TabooMenu extends JavaPlugin {

    private static TabooMenu inst;
    private static TLogger tLogger;
    private static List<Menu> menus = new ArrayList<>();
    private YamlConfiguration config;

    @Override
    public void onEnable() {
        inst = this;
        tLogger = TLogger.getUnformatted(this);

        if (TabooLibHook.setupTabooLib()) {
            tLogger.finest("Hooked TabooLib.");
        }
        if (EconomyBridge.setupEconomy()) {
            tLogger.finest("Hooked Economy.");
        }
        if (PlayerPointsBridge.setupPlayerPoints()) {
            tLogger.finest("Hooked PlayerPoints.");
        }

        AttributeUtils.setup();
        JavaScriptHandler.inst();

        Bukkit.getPluginCommand("taboomenu").setExecutor(new TabooMenuCommand());
        Bukkit.getPluginCommand("taboomenu").setTabCompleter(new TabooMenuCommand());

        Bukkit.getPluginManager().registerEvents(new ListenerPlayer(), this);
        Bukkit.getPluginManager().registerEvents(new ListenerCommand(), this);
        Bukkit.getPluginManager().registerEvents(new ListenerInventory(), this);

        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(this, "BungeeCord")) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }

        new Metrics(inst);
        new PlaceholderHook(inst, "taboomenu").hook();

        new BukkitRunnable() {

            @Override
            public void run() {
                long times = System.currentTimeMillis();

                List<String> errors = new ArrayList<>();
                load(errors);

                if (!errors.isEmpty()) {
                    tLogger.error("#------------------- TabooMenu Errors -------------------#");
                    int count = 1;
                    for (String error : errors) {
                        tLogger.error("(" + (count++) + ") &f" + error);
                    }
                    tLogger.error("#--------------------------------------------------------#");
                } else {
                    tLogger.info("Loaded " + menus.size() + " menus. (" + (System.currentTimeMillis() - times) + "ms)");
                }
            }
        }.runTask(this);
    }

    @Override
    public void onDisable() {
        closeMenu();
    }

    public void closeMenu() {
        VersionUtils.getOnlinePlayers().stream().filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder).forEach(HumanEntity::closeInventory);
    }

    public void load(List<String> errors) {
        DataHandler.getTextureSkulls().clear();

        menus.clear();
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveResource("config.yml", true);
        }

        config = new YamlConfiguration();
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

        File menusFolder = new File(getDataFolder(), "menu");
        if (!menusFolder.isDirectory()) {
            menusFolder.mkdirs();
            try {
                saveResource("menu/example.yml", false);
            } catch (Exception ignored) {
            }
        }

        List<File> menuFiles = loadMenuFiles(menusFolder);
        for (File menuFile : menuFiles) {
            Menu menu = MenuSerializer.loadMenu(menuFile, errors);
            if (menu == null) {
                continue;
            }
            if (menus.stream().filter(x -> x.getFile().getName().equalsIgnoreCase(menu.getFile().getName())).findFirst().orElse(null) != null) {
                errors.add("Two menus have the same file name \"" + menu.getFile().getName() + "\" with different cases. There will be problems opening one of these two menus.");
                continue;
            }
            if (!isSameOpenCommand(menu, errors)) {
                menus.add(menu);
            }
        }
    }

    private boolean isSameOpenCommand(Menu menu, List<String> errors) {
        for (Menu menuOther : menus) {
            for (String command : menuOther.getOpenCommand()) {
                if (menu.getOpenCommand().contains(command) && !command.isEmpty()) {
                    errors.add("The menus \"" + menu.getFile().getName() + "\" and \"" + menuOther.getFile().getName() + "\" have the same command \"" + command + "\". Only one will be opened.");
                    return true;
                }
            }
        }
        return false;
    }

    private List<File> loadMenuFiles(File file) {
        List<File> list = new ArrayList<>();
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                list.addAll(loadMenuFiles(subFile));
            }
        } else if (file.isFile()) {
            if (file.getName().endsWith(".yml")) {
                list.add(file);
            }
        }
        return list;
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static TabooMenu getInst() {
        return inst;
    }

    public static TLogger getTLogger() {
        return tLogger;
    }

    public static List<Menu> getMenus() {
        return menus;
    }

    @Override
    public YamlConfiguration getConfig() {
        return config;
    }
}
