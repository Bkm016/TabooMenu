package me.skymc.taboomenu.iconcommand.impl;

import com.google.common.collect.Lists;
import me.skymc.taboomenu.iconcommand.AbstractIconCommand;
import me.skymc.taboomenu.serialize.IconSerializer;
import me.skymc.taboomenu.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;

import java.util.Collections;
import java.util.List;

/**
 * @Author sky
 * @Since 2018-06-08 18:39
 */
public class IconCommandGiveItem extends AbstractIconCommand {

    private Material material;
    private short data;
    private int amount = 1;
    private String name;
    private String[] lore;
    private ItemStack itemStack;

    public IconCommandGiveItem(String command) {
        super(command);
        String[] data = command.split(" ");
        String[] material = data[0].split(":");
        this.material = IconSerializer.getMaterialSimilar(material[0].toUpperCase());
        this.data = material.length > 1 ? NumberConversions.toShort(material[1]) : 0;
        if (data.length > 1) {
            for (int i = 1; i < data.length; i++) {
                if (StringUtils.isInt(data[i])) {
                    this.amount = NumberConversions.toInt(data[i]);
                } else if (data[i].startsWith("name:")) {
                    this.name = data[i].substring("name:".length()).replace("&", "§");
                } else if (data[i].startsWith("lore:")) {
                    this.lore = data[i].substring("lore:".length()).replace("&", "§").split("\\|");
                }
            }
        }
        this.itemStack = new ItemStack(this.material, amount, this.data);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (this.name != null) {
            itemMeta.setDisplayName(this.name);
        }
        if (this.lore != null) {
            itemMeta.setLore(toList(this.lore));
        }
        this.itemStack.setItemMeta(itemMeta);
    }

    private <T> List<String> toList(T[] array) {
        List list = Lists.newArrayList();
        Collections.addAll(list, array);
        return list;
    }

    @Override
    public void execute(Player player) {
        player.getInventory().addItem(itemStack.clone()).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
    }
}
