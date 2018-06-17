package me.skymc.taboomenu.event;

import me.skymc.taboomenu.display.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @Author sky
 * @Since 2018-06-07 14:34
 */
public class MenuOpenEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private Menu menu;

    public MenuOpenEvent(Player who, Menu menu) {
        super(who);
        this.menu = menu;
    }

    public MenuOpenEvent call() {
        Bukkit.getPluginManager().callEvent(this);
        return this;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
