package me.skymc.taboomenu;

import me.skymc.taboolib.string.ArrayUtils;
import me.skymc.taboomenu.sound.SoundPack;
import me.skymc.taboomenu.template.TemplateManager;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author sky
 * @Since 2018-06-07 10:58
 */
public class TabooMenuCommand implements CommandExecutor, TabCompleter {

    private List<String> commands = Arrays.asList("open", "list", "reload", "template");
    private List<String> commandsTemplate = Arrays.asList("create", "name", "rows", "open");
    private SoundPack soundPack = new SoundPack("UI_BUTTON_CLICK-1-1");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            soundPack.play((Player) sender);
        }
        if (args.length == 0) {
            return commands;
        } else if (args.length == 1) {
            return commands.stream().filter(str -> str.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        } else if (args[0].equalsIgnoreCase("template")) {
            if (args.length == 2) {
                return commandsTemplate.stream().filter(str -> str.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
            } else if (args[1].equalsIgnoreCase("name") || args[1].equalsIgnoreCase("rows") || args[1].equalsIgnoreCase("open")) {
                if (args.length == 2) {
                    return new ArrayList<>(TemplateManager.getTemplates());
                } else if (args.length == 3) {
                    return TemplateManager.getTemplates().stream().filter(str -> str.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                }
            }
        } else if (args[0].equalsIgnoreCase("open")) {
            if (args.length == 2) {
                return TabooMenuAPI.getMenus().stream().filter(str -> str.toLowerCase().startsWith(args[1].toLowerCase())).map(x -> x = x.replace(" ", "__")).collect(Collectors.toList());
            } else if (args.length == 1) {
                return TabooMenuAPI.getMenus().stream().map(x -> x = x.replace(" ", "__")).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            helpCommand(sender, s);
        } else if (args[0].equalsIgnoreCase("open")) {
            openCommand(sender, s, args);
        } else if (args[0].equalsIgnoreCase("list")) {
            listCommand(sender);
        } else if (args[0].equalsIgnoreCase("reload")) {
            reloadCommand(sender);
        } else if (args[0].equalsIgnoreCase("template")) {
            templateCommand(sender, args);
        } else {
            sender.sendMessage("§7[TabooMenu] §4Invalid command: §c" + args[0]);
        }
        return true;
    }

    void helpCommand(CommandSender sender, String s) {
        if (sender.hasPermission("taboomenu.command.help")) {
            sender.sendMessage("");
            sender.sendMessage("§7[TabooMenu] §fCommands:");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " open §7<§8-op§7> §7[§8MENU§7] §7[§8PLAYER§7] §7- §8Opens a menu for a player.");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " list §7- §8Lists the loaded menus.");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " reload §7- §8Reloads the plugin.");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " §7template§f create §7[§8MENU§7] §7<§8ROWS§7> §7[§8NAME§7] §7- §8Create a template.");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " §7template§f name §7[§8MENU§7] §7<§8NAME§7> §7- §8Change the name of template.");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " §7template§f rows §7[§8MENU§7] §7<§8ROWS§7> §7- §8Change the rows of template.");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " §7template§f open §7[§8MENU§7] §7- §8Open a template.");
            sender.sendMessage("");
        } else {
            sender.sendMessage(TranslateUtils.getMessage("no-help-permission"));
        }
    }

    void listCommand(CommandSender sender) {
        if (!sender.hasPermission("taboomenu.command.list")) {
            sender.sendMessage(TranslateUtils.getMessage("no-list-permission"));
        } else {
            sender.sendMessage("");
            sender.sendMessage("§7[TabooMenu] §fLoaded menus:");
            TabooMenu.getMenus().stream().map(menu -> "§7[TabooMenu] §7- §f" + menu.getFile().getName()).forEach(sender::sendMessage);
            sender.sendMessage("");
        }
    }

    void reloadCommand(CommandSender sender) {
        if (!sender.hasPermission("taboomenu.command.reload")) {
            sender.sendMessage(TranslateUtils.getMessage("no-reload-permission"));
        } else {
            long times = System.currentTimeMillis();
            List<String> errors = new ArrayList<>();
            TabooMenu.getInst().load(errors);

            if (!errors.isEmpty()) {
                TranslateUtils.printErrors(sender, errors);
            } else {
                sender.sendMessage("§7[TabooMenu] §fLoaded " + TabooMenu.getMenus().size() + " menus. §8(" + (System.currentTimeMillis() - times) + "ms)");
            }
        }
    }

    void openCommand(CommandSender sender, String s, String[] args) {
        boolean bypass = false;
        if (args.length > 0 && args[0].equalsIgnoreCase("-op")) {
            bypass = true;
            List<String> list = ArrayUtils.asList(args);
            list.remove(0);
            args = list.toArray(new String[0]);
        }

        if (args.length < 2) {
            sender.sendMessage("§7[TabooMenu] §fUsage: /" + s + " open §7<§8-op§7> §8[MENU] [PLAYER]");
            return;
        }

        Player target;
        if (args.length > 2) {
            if (!sender.hasPermission("taboomenu.command.open.other")) {
                sender.sendMessage(TranslateUtils.getMessage("no-open-other-permission"));
                return;
            } else {
                target = Bukkit.getPlayerExact(args[2]);
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§7[TabooMenu] §4You must specify a player from the console.");
            return;
        }

        if (target == null) {
            sender.sendMessage("§7[TabooMenu] §4That player is not online.");
        } else {
            String menuName = (args[1].endsWith(".yml") ? args[1] : args[1] + ".yml").replace("__", " ");
            TabooMenuAPI.MenuState menuState = TabooMenuAPI.openMenu(target, menuName, bypass);
            if (menuState == TabooMenuAPI.MenuState.MENU_NOT_FOUND) {
                sender.sendMessage("§7[TabooMenu] §4The menu §c\"" + menuName + "\"§4 was not found.");
            }
        }
    }

    void templateCommand(CommandSender sender, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("§7[TabooMenu] §4You must specify a player from the console.");
        } else if (args.length == 1) {
            sender.sendMessage("§7[TabooMenu] §4Arguments invalid.");
        } else if (args[1].equalsIgnoreCase("create")) {
            templateCreateCommand(sender, args);
        } else if (args[1].equalsIgnoreCase("name")) {
            templateNameCommand(sender, args);
        } else if (args[1].equalsIgnoreCase("rows")) {
            templateRowsCommand(sender, args);
        } else if (args[1].equalsIgnoreCase("open")) {
            templateOpenCommand(sender, args);
        } else {
            sender.sendMessage("§7[TabooMenu] §4Invalid command: §c" + args[1]);
        }
    }

    void templateCreateCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§7[TabooMenu] §4Arguments invalid.");
            return;
        }
        if (TemplateManager.isTemplateExists(args[2])) {
            sender.sendMessage("§7[TabooMenu] §4Template §c" + args[2] + "§4 is already exists.");
            return;
        }
        TemplateManager.createTemplate((Player) sender, args[2], args.length > 3 ? NumberConversions.toInt(args[3]) : 1, args.length > 4 ? arrayJoin(args, 4) : "Template: " + new Random().nextInt(10000));
        sender.sendMessage("§7[TabooMenu] §fTemplate §7" + args[2] + "§f created.");
    }

    void templateNameCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§7[TabooMenu] §4Arguments invalid.");
            return;
        }
        if (!TemplateManager.isTemplateExists(args[2])) {
            sender.sendMessage("§7[TabooMenu] §4Template §c" + args[2] + "§4 not found.");
            return;
        }
        File file = new File(TemplateManager.getTemplateFolder(), args[2].endsWith(".yml") ? args[2] : args[2] + ".yml");
        YamlConfiguration configuration = TranslateUtils.loadConfiguration(file);
        configuration.set("menu-settings.name", arrayJoin(args, 3));
        try {
            configuration.save(file);
        } catch (Exception e) {
            sender.sendMessage("§7[TabooMenu] §4Template §c" + args[2] + "§4 save failed: " + e.toString());
        }
        sender.sendMessage("§7[TabooMenu] §fChange the name of template §7" + args[2] + "§f to §7" + TranslateUtils.colored(arrayJoin(args, 3)));
    }

    void templateRowsCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§7[TabooMenu] §4Arguments invalid.");
            return;
        }
        if (!TemplateManager.isTemplateExists(args[2])) {
            sender.sendMessage("§7[TabooMenu] §4Template §c" + args[2] + "§4 not found.");
            return;
        }
        File file = new File(TemplateManager.getTemplateFolder(), args[2].endsWith(".yml") ? args[2] : args[2] + ".yml");
        YamlConfiguration configuration = TranslateUtils.loadConfiguration(file);
        configuration.set("menu-settings.rows", NumberConversions.toInt(args[3]));
        try {
            configuration.save(file);
        } catch (Exception e) {
            sender.sendMessage("§7[TabooMenu] §4Template §c" + args[2] + "§4 save failed: " + e.toString());
        }
        sender.sendMessage("§7[TabooMenu] §fChange the rows of template §7" + args[2] + "§f to §7" + NumberConversions.toInt(args[3]));
    }

    void templateOpenCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§7[TabooMenu] §4Arguments invalid.");
        } else {
            TemplateManager.openTemplate((Player) sender, args[2]);
        }
    }

    private String arrayJoin(String[] args, int start) {
        return IntStream.range(start, args.length).mapToObj((i) -> args[i] + " ").collect(Collectors.joining()).trim();
    }

    @SafeVarargs
    private final <T> List<T> asList(T... args) {
        List<T> list = new ArrayList();
        Collections.addAll(list, args);
        return list;
    }
}
