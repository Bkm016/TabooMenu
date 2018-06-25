package me.skymc.taboomenu.iconcommand;

import org.bukkit.entity.Player;

/**
 * @Author sky
 * @Since 2018-06-05 20:07
 */
public abstract class AbstractIconCommand {

    public String command;

    public AbstractIconCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "AbstractIconCommand{" +
                "command='" + command + '\'' +
                '}';
    }

    public abstract void execute(Player player);

}
