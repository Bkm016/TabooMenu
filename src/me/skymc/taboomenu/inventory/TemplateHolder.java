package me.skymc.taboomenu.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @Author sky
 * @Since 2018-06-05 22:53
 */
public class TemplateHolder implements InventoryHolder {

    private String template;

    public TemplateHolder(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
