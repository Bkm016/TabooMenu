package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.util.BungeeUtils;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @Author sky
 * @Since 2018-06-05 20:20
 */
public class IconCommandServer extends AbstractIconCommand {

    public IconCommandServer(String command) {
        super(command);
    }

    @Override
    public void execute(Player player) {
        BungeeUtils.connect(player, command);
    }

    @Override
    public String toString() {
        return MessageFormat.format("IconCommandServer'{'command=''{0}'''}'", command);
    }
}
