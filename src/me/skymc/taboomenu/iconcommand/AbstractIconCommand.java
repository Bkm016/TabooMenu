package me.skymc.taboomenu.iconcommand;

import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @Author sky
 * @Since 2018-06-05 20:07
 */
public abstract class AbstractIconCommand {

    protected String command;

    public AbstractIconCommand(String command) {
        this.command = command;
    }

    protected String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return MessageFormat.format("AbstractIconCommand'{'command=''{0}'''}'", command);
    }

    public abstract void execute(Player player);

}
