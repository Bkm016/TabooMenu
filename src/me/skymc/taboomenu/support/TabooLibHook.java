package me.skymc.taboomenu.support;

import org.bukkit.Bukkit;

/**
 * @Author sky
 * @Since 2018-06-26 16:14
 */
public class TabooLibHook {

    public static boolean isTabooLibEnabled() {
        return Bukkit.getPluginManager().getPlugin("TabooLib") != null;
    }

}