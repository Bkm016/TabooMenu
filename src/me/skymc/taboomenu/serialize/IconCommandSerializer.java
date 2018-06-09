package me.skymc.taboomenu.serialize;

import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.iconcommand.IconCommand;
import me.skymc.taboomenu.iconcommand.impl.*;
import me.skymc.taboomenu.util.MapUtils;
import me.skymc.taboomenu.util.TranslateUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2018-06-05 20:13
 */
public class IconCommandSerializer {

    private static HashMap<Pattern, Class<? extends AbstractIconCommand>> commandTypes = new HashMap<>();

    static {
        commandTypes.put(commandPattern("(tell|send|message):"), TellIconCommand.class);
        commandTypes.put(commandPattern("broadcast:"), BroadcastIconCommand.class);
        commandTypes.put(commandPattern("console:"), ConsoleIconCommand.class);
        commandTypes.put(commandPattern("player:"), PlayerIconCommand.class);
        commandTypes.put(commandPattern("server:"), ServerIconCommand.class);
        commandTypes.put(commandPattern("sound:"), SoundIconCommand.class);
        commandTypes.put(commandPattern("op:"), OpIconCommand.class);
        commandTypes.put(commandPattern("open:"), OpenMenuIconCommand.class);
        commandTypes.put(commandPattern("open-force:"), OpenMenuForceIconCommand.class);
        commandTypes.put(commandPattern("delay:"), DelayIconCommand.class);
    }

    public static List<AbstractIconCommand> readCommands(String input) {
        return Arrays.stream(input.split(";")).map(x -> x = x.trim()).filter(command -> command.length() > 0).map(IconCommandSerializer::matchCommand).collect(Collectors.toList());
    }

    public static AbstractIconCommand matchCommand(String input) {
        if (input.equalsIgnoreCase("close")) {
            return new CloseMenuIconCommand(null);
        }
        for (Map.Entry<Pattern, Class<? extends AbstractIconCommand>> entry : commandTypes.entrySet()) {
            Matcher matcher = entry.getKey().matcher(input);
            if (matcher.find()) {
                try {
                    return entry.getValue().getDeclaredConstructor(String.class).newInstance(matcher.replaceFirst("").trim());
                } catch (Exception ignored) {
                }
            }
        }
        return new PlayerIconCommand(input);
    }

    public static List<IconCommand> formatCommands(Object commandOrigin) {
        List<IconCommand> iconCommands = new ArrayList<>();
        for (Object commandObject : TranslateUtils.formatList(commandOrigin)) {
            if (commandObject instanceof String) {
                readCommands(commandObject.toString()).forEach(x -> iconCommands.add(new IconCommand(x, ClickType.ALL)));
            } else if (commandObject instanceof Map) {
                Map commandMap = (Map) commandObject;
                if (MapUtils.containsIgnoreCase(commandMap, "list")) {
                    ClickType[] clickTypes = Arrays.stream(MapUtils.getOrDefaultIgnoreCase(commandMap, "type", "ALL").split("\\|")).map(ClickType::getByName).toArray(ClickType[]::new);
                    readCommands(MapUtils.getOrDefaultIgnoreCase(commandMap, "list", "")).forEach(x -> iconCommands.add(new IconCommand(x, clickTypes)));
                }
            }
        }
        return iconCommands;
    }

    // *********************************
    //
    //         Private Methods
    //
    // *********************************

    private static Pattern commandPattern(String regex) {
        return Pattern.compile("^(?i)" + regex);
    }
}
