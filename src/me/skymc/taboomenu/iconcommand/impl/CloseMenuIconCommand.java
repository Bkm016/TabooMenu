package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import org.bukkit.entity.Player;

/**
 * @Author sky
 * @Since 2018-06-05 20:20
 */
public class CloseMenuIconCommand extends AbstractIconCommand {

    public CloseMenuIconCommand(String command) {
        super(command);
    }

    @Override
    public void execute(Player player) {
        player.closeInventory();
    }
}
