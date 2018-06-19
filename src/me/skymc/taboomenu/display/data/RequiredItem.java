package me.skymc.taboomenu.display.data;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @Author sky
 * @Since 2018-06-13 18:27
 */
public class RequiredItem {

    private final String material;
    private final String name;
    private final String lore;
    private final int damage;
    private final int amount;

    public RequiredItem(String material, String name, String lore, int damage, int amount) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.damage = damage;
        this.amount = amount;
    }

    public boolean isRequired(ItemStack itemStack) {
        return (material == null || itemStack.getType().name().equalsIgnoreCase(material)) && (damage == -1 || Integer.valueOf(itemStack.getDurability()).equals(damage)) && (name == null || (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains(name))) && (lore == null || (itemStack.getItemMeta().hasLore() && itemStack.getItemMeta().getLore().toString().contains(lore)));
    }

    public boolean hasItem(Player player) {
        int checkAmount = amount;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR) && isRequired(itemStack)) {
                checkAmount -= itemStack.getAmount();
                if (checkAmount <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void takeItem(Player player) {
        int takeAmount = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack != null && !itemStack.getType().equals(Material.AIR) && isRequired(itemStack)) {
                takeAmount -= itemStack.getAmount();
                if (takeAmount < 0) {
                    itemStack.setAmount(itemStack.getAmount() - (takeAmount + itemStack.getAmount()));
                    return;
                } else {
                    player.getInventory().setItem(i, null);
                    if (takeAmount == 0) {
                        return;
                    }
                }
            }
        }
    }

    public static RequiredItem valueOf(String source) {
        String material = null;
        String name = null;
        String lore = null;
        int amount = 1;
        int damage = -1;
        for (String condition : source.split(",")) {
            String[] data = condition.split(":");
            if (data.length == 2) {
                switch (data[0].toLowerCase()) {
                    case "material": {
                        material = data[1];
                        break;
                    }
                    case "name": {
                        name = data[1];
                        break;
                    }
                    case "lore": {
                        lore = data[1];
                        break;
                    }
                    case "amount": {
                        amount = NumberConversions.toInt(data[1]);
                        break;
                    }
                    case "damage": {
                        damage = NumberConversions.toInt(data[1]);
                        break;
                    }
                    default:
                }
            }
        }
        return new RequiredItem(material, name, lore, damage, amount);
    }

    @Override
    public String toString() {
        return MessageFormat.format("RequiredItem'{'material=''{0}'', name=''{1}'', lore=''{2}'', damage={3}, amount={4}'}'", material, name, lore, damage, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequiredItem)) {
            return false;
        }
        RequiredItem that = (RequiredItem) o;
        return damage == that.damage &&
                getAmount() == that.getAmount() &&
                Objects.equals(getMaterial(), that.getMaterial()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getLore(), that.getLore());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterial(), getName(), getLore(), damage, getAmount());
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public String getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public String getLore() {
        return lore;
    }

    public int getAmount() {
        return amount;
    }
}
