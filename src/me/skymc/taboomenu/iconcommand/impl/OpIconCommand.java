package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @Author sky
 * @Since 2018-06-05 20:20
 */
public class OpIconCommand extends AbstractIconCommand {

    public OpIconCommand(String command) {
        super(command);
    }

    @Override
    public void execute(Player player) {
        boolean isOp = player.isOp();
        player.setOp(true);
        player.chat("/" + TranslateUtils.format(player, command));
        player.setOp(isOp);
    }

    @Override
    public String toString() {
        return MessageFormat.format("OpIconCommand'{'command=''{0}'''}'", command);
    }
}
