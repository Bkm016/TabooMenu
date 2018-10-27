package me.skymc.taboomenu.listener;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * @Author sky
 * @Since 2018-06-05 22:35
 */
public class ListenerCommand implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) {
            return;
        }

        String command = e.getMessage().substring(1);
        if (command.isEmpty()) {
            return;
        }

        Menu menu = TabooMenu.getMenus().stream().filter(x -> x.getOpenCommand().stream().anyMatch(command::equals)).findFirst().orElse(null);
        if (menu != null) {
            e.setCancelled(true);
            if (menu.isPermissionBypass() || e.getPlayer().hasPermission(menu.getPermission())) {
                menu.open(e.getPlayer());
            } else {
                e.getPlayer().sendMessage(TranslateUtils.getMessage("no-open-permission").replace("{permission}", menu.getPermission()));
            }
        }
    }
}
