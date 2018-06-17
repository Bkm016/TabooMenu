package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.support.PlayerPointsBridge;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

/**
 * @Author sky
 * @Since 2018-06-08 18:39
 */
public class IconCommandGivePoints extends AbstractIconCommand {

    private int amount;

    public IconCommandGivePoints(String command) {
        super(command);
        this.amount = NumberConversions.toInt(command);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void execute(Player player) {
        if (PlayerPointsBridge.hasValidPlugin()) {
            PlayerPointsBridge.givePoints(player, amount);
        }
    }
}
