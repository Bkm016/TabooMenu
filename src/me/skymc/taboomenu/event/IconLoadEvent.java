package me.skymc.taboomenu.event;

import me.skymc.taboomenu.display.Icon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.Map;

/**
 * @Author sky
 * @Since 2018-06-07 14:34
 */
public class IconLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Icon icon;
    private String iconName;
    private String fileName;
    private int requirementIndex;
    private Map<String, Object> section;
    private List<String> errors;

    public IconLoadEvent(Icon icon, String iconName, String fileName, int requirementIndex, Map<String, Object> section, List<String> errors) {
        this.icon = icon;
        this.iconName = iconName;
        this.fileName = fileName;
        this.section = section;
        this.requirementIndex = requirementIndex;
        this.errors = errors;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************


    public Icon getIcon() {
        return icon;
    }

    public String getIconName() {
        return iconName;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<String, Object> getSection() {
        return section;
    }

    public int getRequirementIndex() {
        return requirementIndex;
    }

    public List<String> getErrors() {
        return errors;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
