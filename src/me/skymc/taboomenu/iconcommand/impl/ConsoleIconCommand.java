package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @Author sky
 * @Since 2018-06-05 20:20
 */
public class ConsoleIconCommand extends AbstractIconCommand {

    public ConsoleIconCommand(String command) {
        super(command);
    }

    @Override
    public void execute(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), TranslateUtils.format(player, command));
    }

    @Override
    public String toString() {
        return MessageFormat.format("ConsoleIconCommand'{'command=''{0}'''}'", command);
    }
}
