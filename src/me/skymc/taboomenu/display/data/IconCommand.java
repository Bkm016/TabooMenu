package me.skymc.taboomenu.display.data;

import me.skymc.taboomenu.iconcommand.AbstractIconCommand;

import java.util.Arrays;
import java.util.List;

/**
 * @Author sky
 * @Since 2018-06-06 13:02
 */
public class IconCommand {

    private List<AbstractIconCommand> commands;
    private List<ClickType> clickType;
    private double chance;

    public IconCommand(List<AbstractIconCommand> commands, double chance, ClickType... clickType) {
        this.commands = commands;
        this.clickType = Arrays.asList(clickType);
        this.chance = chance;
    }

    @Override
    public String toString() {
        return "IconCommand{" +
                "commands=" + commands +
                ", clickType=" + clickType +
                '}';
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public List<AbstractIconCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<AbstractIconCommand> commands) {
        this.commands = commands;
    }

    public List<ClickType> getClickType() {
        return clickType;
    }

    public void setClickType(List<ClickType> clickType) {
        this.clickType = clickType;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

}
