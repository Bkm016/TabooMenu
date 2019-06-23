package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.handler.DataHandler;
import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.util.StringUtils;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author Arasple
 * @Since 2019-06-23
 * <p>
 * Prompter-Console: kick {0} {1};command2
 */
public class IconCommandPrompterConsole extends AbstractIconCommand {

    private String[] commands;

    public IconCommandPrompterConsole(String command) {
        super(command);
        commands = command.split(";");
    }

    @Override
    public void execute(Player player) {
        player.setMetadata("Prompting", new FixedMetadataValue(TabooMenu.getInst(), "55555"));
        DataHandler.getLatestChatMessage().remove(player.getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (DataHandler.getLatestChatMessage().containsKey(player.getName())) {
                    String[] args = DataHandler.getLatestChatMessage().get(player.getName()).split(" ");
                    if ("cancel".equalsIgnoreCase(args[0]) || "exit".equalsIgnoreCase(args[0])) {
                        player.removeMetadata("Prompting", TabooMenu.getInst());
                        DataHandler.getLatestChatMessage().remove(player.getName());
                        cancel();
                        return;
                    }
                    for (String cmd : commands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), TranslateUtils.format(player, StringUtils.replaceWithOrder(cmd, args)));
                    }
                    player.removeMetadata("Prompting", TabooMenu.getInst());
                    DataHandler.getLatestChatMessage().remove(player.getName());
                    cancel();
                }
            }
        }.runTaskTimer(TabooMenu.getInst(), 10, 5);
    }

}
