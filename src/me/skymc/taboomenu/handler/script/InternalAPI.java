package me.skymc.taboomenu.handler.script;

import me.skymc.taboomenu.TabooMenuAPI;
import me.skymc.taboomenu.display.Icon;
import org.bukkit.entity.Player;

/**
 * @Author sky
 * @Since 2018-08-01 20:01
 */
public class InternalAPI {

    private Player player;
    private Icon icon;

    public InternalAPI(Player player, Icon icon) {
        this.player = player;
        this.icon = icon;
    }

    public boolean isCooldown() {
        return TabooMenuAPI.isCooldown(player, icon.getIndex());
    }

    public boolean isCooldown(String index) {
        return TabooMenuAPI.isCooldown(player, index);
    }
}
