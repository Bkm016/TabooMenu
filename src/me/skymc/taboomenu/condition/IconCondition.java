package me.skymc.taboomenu.condition;

import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.data.ClickType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;

/**
 * @Author sky
 * @Since 2018-07-30 21:42
 */
public abstract class IconCondition {

    private static HashMap<String, IconCondition> iconConditions = new HashMap<>();

    public static Collection<IconCondition> getIconConditions() {
        return iconConditions.values();
    }

    public static void registerCondition(String name, IconCondition condition, Plugin plugin) {
        if (iconConditions.containsKey(name)) {
            throw new IllegalPluginAccessException("IconCondition " + name + " is already registered.");
        } else {
            iconConditions.put(name, condition);
            if (condition instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) condition, plugin);
            }
        }
    }

    /**
     * 是否作用于物品被展示时
     *
     * @return boolean
     */
    public boolean inView() {
        return false;
    }

    /**
     * 检测条件是否达成
     *
     * @param player     玩家
     * @param clickEvent 点击事件
     * @param clickType  点击类型
     * @param icon       物品对象
     * @return boolean
     */
    abstract public boolean check(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon);

    /**
     * 所有条件达成后更改玩家数据
     *
     * @param player     玩家
     * @param clickEvent 点击事件
     * @param clickType  点击类型
     * @param icon       物品对象
     */
    abstract public void change(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon);

}
