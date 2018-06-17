package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.handler.PlayerDataHandler;
import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import org.bukkit.entity.Player;

/**
 * @Author sky
 * @Since 2018-06-05 20:20
 */
public class IconCommandClose extends AbstractIconCommand {

    private CloseType closeType;

    public IconCommandClose(CloseType closeType) {
        super(null);
        this.closeType = closeType;
    }

    public CloseType getCloseType() {
        return closeType;
    }

    @Override
    public void execute(Player player) {
        if (closeType == CloseType.CLOSE) {
            PlayerDataHandler.ignoredPrevious(player);
        }
        player.closeInventory();
    }

    public enum CloseType {
        CLOSE, PREVIOUS
    }
}
