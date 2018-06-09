package me.skymc.taboomenu;

import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2018-06-07 10:58
 */
public class TabooMenuCommand implements CommandExecutor, TabCompleter {

    private List<String> commands = Arrays.asList("open", "list", "reload");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            return commands;
        } else if (args.length == 1) {
            return commands.stream().filter(str -> str.toLowerCase().startsWith(args[0])).collect(Collectors.toList());
        } else if (args[0].equalsIgnoreCase("open")) {
            if (args.length == 2) {
                return TabooMenuAPI.getMenus().stream().filter(str -> str.toLowerCase().startsWith(args[1])).map(x -> x = x.replace(" ", "__")).collect(Collectors.toList());
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
        }
        return true;
    }

    void helpCommand(CommandSender sender, String s) {
        if (sender.hasPermission("taboomenu.command.help")) {
            sender.sendMessage("");
            sender.sendMessage("§7[TabooMenu] §fCommands:");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " open §8[MENU] [PLAYER] §7- §8Opens a menu for a player.");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " list §7- §8Lists the loaded menus.");
            sender.sendMessage("§7[TabooMenu] §f/" + s + " reload §7- §8Reloads the plugin.");
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
                sender.sendMessage("§4#------------------- TabooMenu Errors -------------------#");
                int count = 1;
                for (String error : errors) {
                    sender.sendMessage("§c(" + (count++) + ") §f" + error);
                }
                sender.sendMessage("§4#--------------------------------------------------------#");
            } else {
                sender.sendMessage("§7[TabooMenu] §fLoaded " + TabooMenu.getMenus().size() + " menus. §8(" + (System.currentTimeMillis() - times) + "ms)");
            }
        }
    }

    void openCommand(CommandSender sender, String s, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§7[TabooMenu] §fUsage: /" + s + " open §8[MENU] [PLAYER]");
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
            sender.sendMessage("§4You must specify a player from the console.");
            return;
        }

        if (target == null) {
            sender.sendMessage("§4That player is not online.");
        } else {
            String menuName = args[1].endsWith(".yml") ? args[1] : args[1] + ".yml";
            menuName = menuName.replace("__", " ");

            TabooMenuAPI.MenuState menuState = TabooMenuAPI.openMenu(target, menuName, false);
            if (menuState == TabooMenuAPI.MenuState.MENU_NOT_FOUND) {
                sender.sendMessage("§4The menu §c\"" + menuName + "\"§4 was not found.");
            }
        }
    }
}
