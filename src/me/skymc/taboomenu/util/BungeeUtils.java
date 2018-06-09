package me.skymc.taboomenu.util;

import me.skymc.taboomenu.TabooMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class BungeeUtils {

    public static void connect(Player player, String server) {
        try {
            if (server.length() == 0) {
                player.sendMessage("Target server was \"\" (empty string) cannot connect to it.");
                return;
            }

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArray);

            out.writeUTF("Connect");
            out.writeUTF(server);

            player.sendPluginMessage(TabooMenu.getInst(), "BungeeCord", byteArray.toByteArray());
        } catch (Exception e) {
            player.sendMessage("§4An unexpected exception has occurred. Please notify the server's staff about this. (They should look at the console).");
            TabooMenu.getTLogger().error("Could not connect \"" + player.getName() + "\" to the server \"" + server + "\": " + e.toString());
        }
    }
}
