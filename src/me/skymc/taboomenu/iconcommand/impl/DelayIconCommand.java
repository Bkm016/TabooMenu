package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import org.bukkit.entity.Player;

/**
 * @Author sky
 * @Since 2018-06-08 18:39
 */
public class DelayIconCommand extends AbstractIconCommand {

    private int delay;

    public DelayIconCommand(String command) {
        super(command);
        try {
            delay = Integer.valueOf(command);
        } catch (Exception ignored) {
            delay = 0;
        }
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public void execute(Player player) {

    }
}
