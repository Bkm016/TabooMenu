package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.support.EconomyBridge;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

/**
 * @Author sky
 * @Since 2018-06-08 18:39
 */
public class IconCommandGiveMoney extends AbstractIconCommand {

    private double amount;

    public IconCommandGiveMoney(String command) {
        super(command);
        this.amount = NumberConversions.toDouble(command);
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public void execute(Player player) {
        if (EconomyBridge.hasValidEconomy()) {
            EconomyBridge.giveMoney(player, amount);
        }
    }
}
