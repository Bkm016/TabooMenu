package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.TabooMenuAPI;
import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import org.bukkit.entity.Player;

/**
 * @Author sky
 * @Since 2018-06-06 22:34
 */
public class IconCommandOpenForce extends AbstractIconCommand {

    public IconCommandOpenForce(String command) {
        super(command);
    }

    @Override
    public void execute(Player player) {
        TabooMenuAPI.openMenu(player, command, true);
    }

}
