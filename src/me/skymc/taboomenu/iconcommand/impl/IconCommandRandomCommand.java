package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @Author arasple
 */
public class IconCommandRandomCommand extends AbstractIconCommand {

    public IconCommandRandomCommand(String command) {
        super(command);
    }

    @Override
    public void execute(Player player) {
        String rcmd = command.substring(1, command.length() - 1).replace('_', ' ');
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), TranslateUtils.format(player, getRCmd(rcmd)));
    }

    private String getRCmd(String rcmd) {
        return rcmd.split("#")[(int) (Math.random() * rcmd.split("#").length)];
    }

}
