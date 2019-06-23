package me.skymc.taboomenu.support;

import org.bukkit.Bukkit;

/**
 * @Author sky
 * @Since 2018-06-26 16:14
 */
public class TabooLibHook {

    private static boolean tabooLibEnabled;

    public static boolean setupTabooLib() {
        return tabooLibEnabled = Bukkit.getPluginManager().getPlugin("TabooLib") != null;
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static boolean isTabooLibEnabled() {
        return tabooLibEnabled;
    }

}