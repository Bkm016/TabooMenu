package me.skymc.taboomenu.display.data;

import me.skymc.taboomenu.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import java.util.Objects;

/**
 * @Author sky
 * @Since 2018-06-13 18:27
 */
public class RequiredItem {

    private String material;
    private String name;
    private String lore;
    private Integer damage;
    private Integer amount;
    private Boolean hasName;
    private Boolean hasLore;

    public RequiredItem() {
    }

    public RequiredItem(String material, String name, String lore, Integer damage, Integer amount, Boolean hasName, Boolean hasLore) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.damage = damage;
        this.amount = amount;
        this.hasName = hasName;
        this.hasLore = hasLore;
    }

    public boolean isRequired(ItemStack itemStack) {
        return (material == null || itemStack.getType().name().equalsIgnoreCase(material))
                && (damage == -1 || Integer.valueOf(itemStack.getDurability()).equals(damage))
                && (hasName == null || hasName.equals(itemStack.getItemMeta().hasDisplayName()))
                && (hasLore == null || hasLore.equals(itemStack.getItemMeta().hasLore()))
                && (name == null || (itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains(name)))
                && (lore == null || (itemStack.getItemMeta().hasLore() && itemStack.getItemMeta().getLore().toString().contains(lore)));
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
        Integer amount = 1;
        Integer damage = -1;
        Boolean hasName = null;
        Boolean hasLore = null;
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
                    case "isname":
                    case "hasname": {
                        hasName = StringUtils.toBooleanObject(data[1]);
                        break;
                    }
                    case "islore":
                    case "haslore": {
                        hasLore = StringUtils.toBooleanObject(data[1]);
                        break;
                    }
                    default:
                }
            }
        }
        return new RequiredItem(material, name, lore, damage, amount, hasName, hasLore);
    }

    @Override
    public String toString() {
        return "RequiredItem{" +
                "material='" + material + '\'' +
                ", name='" + name + '\'' +
                ", lore='" + lore + '\'' +
                ", damage=" + damage +
                ", amount=" + amount +
                ", hasName=" + hasName +
                ", hasLore=" + hasLore +
                '}';
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
        return Objects.equals(getMaterial(), that.getMaterial()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getLore(), that.getLore()) &&
                Objects.equals(getDamage(), that.getDamage()) &&
                Objects.equals(getAmount(), that.getAmount()) &&
                Objects.equals(hasName, that.hasName) &&
                Objects.equals(hasLore, that.hasLore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterial(), getName(), getLore(), getDamage(), getAmount(), hasName, hasLore);
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

    public Integer getDamage() {
        return damage;
    }

    public Boolean isHasName() {
        return hasName;
    }

    public Boolean isHasLore() {
        return hasLore;
    }

    public RequiredItem setMaterial(String material) {
        this.material = material;
        return this;
    }

    public RequiredItem setName(String name) {
        this.name = name;
        return this;
    }

    public RequiredItem setLore(String lore) {
        this.lore = lore;
        return this;
    }

    public RequiredItem setDamage(Integer damage) {
        this.damage = damage;
        return this;
    }

    public RequiredItem setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public RequiredItem setHasName(Boolean hasName) {
        this.hasName = hasName;
        return this;
    }

    public RequiredItem setHasLore(Boolean hasLore) {
        this.hasLore = hasLore;
        return this;
    }
}
