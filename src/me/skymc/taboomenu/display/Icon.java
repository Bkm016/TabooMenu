package me.skymc.taboomenu.display;

import com.google.common.collect.ImmutableMap;
import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.TabooMenuAPI;
import me.skymc.taboomenu.display.data.ClickType;
import me.skymc.taboomenu.display.data.RequiredItem;
import me.skymc.taboomenu.display.data.Requirement;
import me.skymc.taboomenu.event.IconClickEvent;
import me.skymc.taboomenu.handler.JavaScriptHandler;
import me.skymc.taboomenu.iconcommand.IconCommand;
import me.skymc.taboomenu.iconcommand.impl.DelayIconCommand;
import me.skymc.taboomenu.support.EconomyBridge;
import me.skymc.taboomenu.support.PlayerPointsBridge;
import me.skymc.taboomenu.util.AttributeUtils;
import me.skymc.taboomenu.util.StringUtils;
import me.skymc.taboomenu.util.TranslateUtils;
import me.skymc.taboomenu.util.VersionUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2018-06-05 20:07
 */
public class Icon implements Cloneable {

    private Material material;
    private short data;
    private int amount;

    private String name;
    private List<String> lore;
    private List<String> bannerPatterns;

    private Color color;
    private String skullOwner;

    private double price;
    private int points;
    private int levels;

    private String permission;
    private String permissionMessage;
    private String permissionView;

    private boolean shiny;
    private boolean hideAttribute;

    private Set<Integer> slotCopy = new HashSet<>();
    private List<Requirement> requirements = new ArrayList<>();
    private List<IconCommand> iconCommands = new ArrayList<>();
    private List<RequiredItem> requiredItems = new ArrayList<>();

    public Icon(Material material, short data, int amount) {
        this.material = material;
        this.data = data;
        this.amount = amount;
    }

    public void onClick(Player player, ClickType clickType) {
        Icon icon = getEffectiveIcon(player, clickType);

        IconClickEvent event = new IconClickEvent(player, TabooMenuAPI.getPlayerCurrentMenu(player), this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        // ****************************************
        //
        //           Check Requirement
        //
        // ****************************************

        if (!icon.canViewIcon(player)) {
            return;
        }

        if (!icon.canClickIcon(player)) {
            player.sendMessage(permissionMessage != null ? permissionMessage : TranslateUtils.getMessage("no-permission"));
            return;
        }

        if (icon.price > 0) {
            if (!EconomyBridge.hasValidEconomy()) {
                player.sendMessage(ChatColor.RED + "This command has a price, but Vault with a compatible economy plugin was not found. For security, the command has been blocked. Please inform the staff.");
                return;
            }
            if (!EconomyBridge.hasMoney(player, icon.price)) {
                player.sendMessage(TranslateUtils.getMessage("no-money").replace("{money}", EconomyBridge.formatMoney(icon.price)));
                return;
            }
        }

        if (icon.points > 0) {
            if (!PlayerPointsBridge.hasValidPlugin()) {
                player.sendMessage(ChatColor.RED + "This command has a price in points, but the plugin PlayerPoints was not found. For security, the command has been blocked. Please inform the staff.");
                return;
            }

            if (!PlayerPointsBridge.hasPoints(player, icon.points)) {
                player.sendMessage(TranslateUtils.getMessage("no-points").replace("{points}", Integer.toString(icon.points)));
                return;
            }
        }

        if (icon.levels > 0) {
            if (player.getLevel() < icon.levels) {
                player.sendMessage(TranslateUtils.getMessage("no-exp").replace("{levels}", Integer.toString(icon.levels)));
                return;
            }
        }

        if (!icon.requiredItems.isEmpty()) {
            if (icon.requiredItems.stream().anyMatch(x -> !x.hasItem(player))) {
                player.sendMessage(TranslateUtils.getMessage("no-required-item"));
                return;
            }
        }

        // ****************************************
        //
        //           Take Requirement
        //
        // ****************************************

        if (icon.price > 0 && !EconomyBridge.takeMoney(player, icon.price)) {
            player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
            return;
        }

        if (icon.points > 0 && !PlayerPointsBridge.takePoints(player, icon.points)) {
            player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
            return;
        }

        if (icon.levels > 0) {
            player.setLevel(player.getLevel() - icon.levels);
        }

        if (!icon.requiredItems.isEmpty()) {
            icon.requiredItems.forEach(x -> x.takeItem(player));
        }

        executeCommand(player, icon.iconCommands.stream().filter(iconCommand -> iconCommand.getClickType().contains(ClickType.ALL) || iconCommand.getClickType().contains(clickType)).collect(Collectors.toList()));
    }

    public void executeCommand(Player player, List<IconCommand> iconCommands) {
        int delay = 0;
        for (IconCommand iconCommand : iconCommands) {
            if (iconCommand.getCommand() instanceof DelayIconCommand) {
                delay += ((DelayIconCommand) iconCommand.getCommand()).getDelay();
            } else {
                Bukkit.getScheduler().runTaskLater(TabooMenu.getInst(), () -> iconCommand.getCommand().execute(player), delay);
            }
        }
    }

    public boolean canClickIcon(Player player) {
        return StringUtils.isBlank(permission) || (permission.startsWith("-") ? !player.hasPermission(permission.substring(1)) : player.hasPermission(permission));
    }

    public boolean canViewIcon(Player player) {
        return StringUtils.isBlank(permissionView) || (permissionView.startsWith("-") ? !player.hasPermission(permissionView.substring(1)) : player.hasPermission(permissionView));
    }

    public Icon getEffectiveIcon(Player player, ClickType clickType) {
        if (requirements.isEmpty()) {
            return this;
        }
        SimpleBindings bindings = new SimpleBindings(ImmutableMap.of("player", player, "bukkit", Bukkit.getServer(), "clickType", clickType.name()));
        for (Requirement requirement : requirements) {
            try {
                Object result;
                if (requirement.isPreCompile()) {
                    result = requirement.getCompiledScript().eval(bindings);
                } else {
                    CompiledScript script = JavaScriptHandler.compile(TranslateUtils.format(player, requirement.getExpression()));
                    result = script.eval(bindings);
                }
                if (result instanceof Boolean && (Boolean) result) {
                    return requirement.getIcon();
                }
            } catch (ScriptException e) {
                TabooMenu.getTLogger().error("Requirement javascript <" + requirement.getExpression() + "> is invalid and does not return a boolean!");
            }
        }
        return this;
    }

    public ItemStack createItemStack(Player player) {
        ItemStack itemStack = new ItemStack(material, amount, data);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!StringUtils.isBlank(name)) {
            itemMeta.setDisplayName(TranslateUtils.format(player, name));
        }

        if (lore != null) {
            itemMeta.setLore(TranslateUtils.format(player, lore));
        }

        if (color != null && itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(color);
        }

        if (!StringUtils.isBlank(skullOwner) && itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwner(TranslateUtils.format(player, skullOwner));
        }

        if (bannerPatterns != null && VersionUtils.getVersionNumber() >= 10900 && itemMeta instanceof org.bukkit.inventory.meta.BannerMeta) {
            for (String patternStr : bannerPatterns) {
                String[] type = patternStr.split(" ");
                if (type.length == 1) {
                    try {
                        ((org.bukkit.inventory.meta.BannerMeta) itemMeta).setBaseColor(DyeColor.valueOf(type[0].toUpperCase()));
                    } catch (Exception ignored) {
                        ((org.bukkit.inventory.meta.BannerMeta) itemMeta).setBaseColor(DyeColor.BLACK);
                    }
                } else if (type.length == 2) {
                    try {
                        ((org.bukkit.inventory.meta.BannerMeta) itemMeta).addPattern(new org.bukkit.block.banner.Pattern(DyeColor.valueOf(type[0].toUpperCase()), org.bukkit.block.banner.PatternType.valueOf(type[1].toUpperCase())));
                    } catch (Exception ignored) {
                        ((org.bukkit.inventory.meta.BannerMeta) itemMeta).addPattern(new org.bukkit.block.banner.Pattern(DyeColor.BLACK, org.bukkit.block.banner.PatternType.BASE));
                    }
                }
            }
        }

        itemStack.setItemMeta(itemMeta);

        if (shiny) {
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        }

        if (hideAttribute) {
            itemStack = AttributeUtils.hideAttributes(itemStack);
        }
        return itemStack;
    }

    @Override
    public Object clone() {
        Icon icon = null;
        try {
            icon = (Icon) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Icon)) {
            return false;
        }
        Icon icon = (Icon) o;
        return getData() == icon.getData() &&
                getAmount() == icon.getAmount() &&
                Double.compare(icon.getPrice(), getPrice()) == 0 &&
                getPoints() == icon.getPoints() &&
                getLevels() == icon.getLevels() &&
                isShiny() == icon.isShiny() &&
                isHideAttribute() == icon.isHideAttribute() &&
                getMaterial() == icon.getMaterial() &&
                Objects.equals(getName(), icon.getName()) &&
                Objects.equals(getLore(), icon.getLore()) &&
                Objects.equals(getColor(), icon.getColor()) &&
                Objects.equals(getSkullOwner(), icon.getSkullOwner()) &&
                Objects.equals(getPermission(), icon.getPermission()) &&
                Objects.equals(getPermissionMessage(), icon.getPermissionMessage()) &&
                Objects.equals(getPermissionView(), icon.getPermissionView()) &&
                Objects.equals(getSlotCopy(), icon.getSlotCopy()) &&
                Objects.equals(getRequirements(), icon.getRequirements()) &&
                Objects.equals(getIconCommands(), icon.getIconCommands());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterial(), getData(), getAmount(), getName(), getLore(), getColor(), getSkullOwner(), getPrice(), getPoints(), getLevels(), getPermission(), getPermissionMessage(), getPermissionView(), isShiny(), isHideAttribute(), getSlotCopy(), getRequirements(), getIconCommands());
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************


    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getSkullOwner() {
        return skullOwner;
    }

    public void setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    public String getPermissionView() {
        return permissionView;
    }

    public void setPermissionView(String permissionView) {
        this.permissionView = permissionView;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public boolean isHideAttribute() {
        return hideAttribute;
    }

    public void setHideAttribute(boolean hideAttribute) {
        this.hideAttribute = hideAttribute;
    }

    public Set<Integer> getSlotCopy() {
        return slotCopy;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public List<IconCommand> getIconCommands() {
        return iconCommands;
    }

    public List<RequiredItem> getRequiredItems() {
        return requiredItems;
    }

    public void setIconCommands(List<IconCommand> iconCommands) {
        this.iconCommands = iconCommands;
    }

    public List<String> getBannerPatterns() {
        return bannerPatterns;
    }

    public void setBannerPatterns(List<String> bannerPatterns) {
        this.bannerPatterns = bannerPatterns;
    }
}
