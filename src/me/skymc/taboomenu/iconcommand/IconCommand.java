package me.skymc.taboomenu.iconcommand;

import me.skymc.taboomenu.display.data.ClickType;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @Author sky
 * @Since 2018-06-06 13:02
 */
public class IconCommand {

    private final AbstractIconCommand command;
    private final List<ClickType> clickType;

    public IconCommand(AbstractIconCommand command, ClickType... clickType) {
        this.command = command;
        this.clickType = Arrays.asList(clickType);
    }

    public AbstractIconCommand getCommand() {
        return command;
    }

    public List<ClickType> getClickType() {
        return clickType;
    }

    @Override
    public String toString() {
        return MessageFormat.format("IconCommand'{'command={0}, clickType={1}'}'", command, clickType);
    }
}
