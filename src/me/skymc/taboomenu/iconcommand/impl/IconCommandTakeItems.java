package me.skymc.taboomenu.iconcommand.impl;

import me.skymc.taboomenu.display.data.RequiredItem;
import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author sky
 * @Since 2018-06-08 18:39
 */
public class IconCommandTakeItems extends AbstractIconCommand {

    private HashMap<String, RequiredItem> requiredItems = new HashMap<>();

    public IconCommandTakeItems(String command) {
        super(command);
        Arrays.stream(command.split(";")).map(item -> requiredItems.put(item, RequiredItem.valueOf(item)));
    }

    @Override
    public void execute(Player player) {
        requiredItems.values().forEach(value -> value.takeItem(player));
    }
}
