package me.skymc.taboomenu.serialize;

import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.display.data.IconCommand;
import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.iconcommand.impl.*;
import me.skymc.taboomenu.util.MapUtils;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.util.NumberConversions;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2018-06-05 20:13
 */
public class IconCommandSerializer {

    private final static HashMap<Pattern, Class<? extends AbstractIconCommand>> commandTypes = new HashMap<>();
    private final static Pattern chanceFlag = Pattern.compile("<(?i)(change|chance|rate):(.+)>");

    static {
        commandTypes.put(commandPattern("(tell|send|message):"), IconCommandMessage.class);
        commandTypes.put(commandPattern("broadcast:"), IconCommandBroadcast.class);
        commandTypes.put(commandPattern("console:"), IconCommandConsole.class);
        commandTypes.put(commandPattern("player:"), IconCommandPlayer.class);
        commandTypes.put(commandPattern("rcmd:"), IconCommandRandomCommand.class);
        commandTypes.put(commandPattern("(server|connect):"), IconCommandServer.class);
        commandTypes.put(commandPattern("sound:"), IconCommandSound.class);
        commandTypes.put(commandPattern("sound-(all|broadcast):"), IconCommnadSoundBroadcast.class);
        commandTypes.put(commandPattern("op:"), IconCommandOp.class);
        commandTypes.put(commandPattern("open:"), IconCommandOpen.class);
        commandTypes.put(commandPattern("open-force:"), IconCommandOpenForce.class);
        commandTypes.put(commandPattern("(delay|wait):"), IconCommandDelay.class);
        commandTypes.put(commandPattern("(take|remove)-(money|balance):"), IconCommandTakeMoney.class);
        commandTypes.put(commandPattern("(give|add)-(money|balance):"), IconCommandGiveMoney.class);
        commandTypes.put(commandPattern("(take|remove)-point(s)?:"), IconCommandTakePoints.class);
        commandTypes.put(commandPattern("(give|add)-point(s)?:"), IconCommandGivePoints.class);
        commandTypes.put(commandPattern("(give-)?item(s)?:"), IconCommandGiveItem.class);
        commandTypes.put(commandPattern("(take|remove)-item(s)?:"), IconCommandTakeItems.class);
    }

    public static List<AbstractIconCommand> readCommands(String input) {
        return Arrays.stream(input.split(";")).map(x -> x = x.trim()).filter(command -> command.length() > 0).map(IconCommandSerializer::matchCommand).collect(Collectors.toList());
    }

    public static IconCommand readCommandsFully(String input, ClickType[] clickType) {
        // change 是我打错了！是chance！
        double chance = 1;
        Matcher matcherChange = chanceFlag.matcher(input);
        if (matcherChange.find()) {
            input = matcherChange.replaceFirst("").trim();
            chance = NumberConversions.toDouble(matcherChange.group(2).trim());
        }
        return new IconCommand(readCommands(input), chance, clickType);
    }

    public static List<IconCommand> formatCommands(Object commandOrigin, ClickType... clickType) {
        List<IconCommand> iconCommands = new ArrayList<>();
        for (Object commandObject : TranslateUtils.formatList(commandOrigin)) {
            if (commandObject instanceof String) {
                iconCommands.add(readCommandsFully(commandObject.toString(), clickType.length == 0 ? new ClickType[]{ClickType.ALL} : clickType));
            } else if (commandObject instanceof Map) {
                Map commandMap = (Map) commandObject;
                if (MapUtils.containsSimilar(commandMap, "list|command(s)?")) {
                    ClickType[] clickTypes = Arrays.stream(MapUtils.getSimilarOrDefault(commandMap, "(click-)?type", "ALL").split("\\|")).map(ClickType::getByName).toArray(ClickType[]::new);
                    iconCommands.addAll(formatCommands(MapUtils.getSimilarOrDefault(commandMap, "list|command(s)?", new Object()), clickTypes));
                }
            }
        }
        return iconCommands;
    }

    public static AbstractIconCommand matchCommand(String input) {
        if (input.toLowerCase().matches("close(-menu)?")) {
            return new IconCommandClose(IconCommandClose.CloseType.CLOSE);
        } else if (input.toLowerCase().matches("previous(-menu)?")) {
            return new IconCommandClose(IconCommandClose.CloseType.PREVIOUS);
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
        return new IconCommandPlayer(input);
    }

    // *********************************
    //
    //         Private Methods
    //
    // *********************************

    private static Pattern commandPattern(String regex) {
        return Pattern.compile("^(?i)" + regex);
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static Pattern getChanceFlag() {
        return chanceFlag;
    }
}
