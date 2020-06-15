package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @Author sky
 * @Since 2018-06-05 20:20
 */
public class IconCommandOp extends AbstractIconCommand {

    public IconCommandOp(String command) {
        super(command);
    }

    @Override
    public void execute(Player player) {
        if (command.startsWith("/")) command = command.substring(1);
        boolean isOp = player.isOp();
        player.setOp(true);
        player.chat("/" + TranslateUtils.format(player, command));
        player.setOp(isOp);
    }

    @Override
    public String toString() {
        return MessageFormat.format("IconCommandOp'{'command=''{0}'''}'", command);
    }
}
