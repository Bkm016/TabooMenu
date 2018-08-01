package me.skymc.taboomenu.event;

import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.display.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @Author sky
 * @Since 2018-06-07 14:34
 */
public class IconClickEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean canceled;
    private Icon icon;
    private Menu menu;

    public IconClickEvent(Player who, Menu menu, Icon icon) {
        super(who);
        this.menu = menu;
        this.icon = icon;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Menu getMenu() {
        return menu;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
