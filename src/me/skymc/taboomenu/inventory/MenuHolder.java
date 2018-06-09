package me.skymc.taboomenu.inventory;

import me.skymc.taboomenu.display.Menu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @Author sky
 * @Since 2018-06-05 22:53
 */
public class MenuHolder implements InventoryHolder {

    private Menu menu;

    public MenuHolder(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
