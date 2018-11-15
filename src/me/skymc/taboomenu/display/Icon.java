package me.skymc.taboomenu.display;

import com.google.common.collect.ImmutableMap;
import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.TabooMenuAPI;
import me.skymc.taboomenu.condition.IconCondition;
import me.skymc.taboomenu.display.data.*;
import me.skymc.taboomenu.event.IconClickEvent;
import me.skymc.taboomenu.handler.ScriptHandler;
import me.skymc.taboomenu.handler.script.InternalAPI;
import me.skymc.taboomenu.iconcommand.impl.IconCommandDelay;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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

    private static Random random = new Random();

    private Material material;
    private short data;
    private int amount;

    private String name;
    private List<String> lore;
    private List<String> bannerPatterns;

    private Color color;
    private String skullOwner;

    private String skullId;
    private String skullTexture;

    private double price;
    private int points;
    private int levels;

    private String permission;
    private String permissionMessage;
    private String permissionView;

    private boolean full;
    private boolean shiny;
    private boolean hideAttribute;

    private IconAction iconAction;

    private String eggType;
    private String[] potionType;

    private Set<Integer> slotCopy = new HashSet<>();
    private List<Requirement> requirements = new ArrayList<>();
    private List<IconCommand> iconCommands = new ArrayList<>();
    private List<RequiredItem> requiredItems = new ArrayList<>();

    private String menuName;
    private String iconName;
    private int requirementIndex;

    private ItemStack itemSource;
    private Item item;

    public Icon(Material material, short data, int amount) {
        this.material = material;
        this.data = data;
        this.amount = amount;
        this.item = new Item(this);
    }

    /**
     * 是否可以查看物品
     *
     * @param player 玩家
     * @return boolean
     */
    public boolean canIconView(Player player) {
        return IconCondition.getIconConditions().stream().filter(IconCondition::inView).anyMatch(condition -> condition.check(player, null, null, this));
    }

    /**
     * 当物品被点击
     *
     * @param player     玩家
     * @param clickEvent 点击事件
     * @param clickType  点击类型
     */
    public void onClick(Player player, InventoryClickEvent clickEvent, ClickType clickType) {
        Icon icon = getEffectiveIcon(player, clickType);
        IconClickEvent event = new IconClickEvent(player, TabooMenuAPI.getPlayerCurrentMenu(player), icon);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (IconCondition.getIconConditions().stream().anyMatch(condition -> !condition.check(player, clickEvent, clickType, event.getIcon()))) {
            return;
        }
        IconCondition.getIconConditions().forEach(iconCondition -> iconCondition.change(player, clickEvent, clickType, event.getIcon()));
        executeClickAction(player, clickEvent, clickType, event.getIcon());
        executeCommand(player, event.getIcon().iconCommands.stream().filter(iconCommand -> iconCommand.getClickType().contains(ClickType.ALL) || iconCommand.getClickType().contains(clickType)).collect(Collectors.toList()));
    }

    /**
     * 执行物品命令
     *
     * @param player       玩家
     * @param iconCommands 命令
     */
    public void executeCommand(Player player, List<IconCommand> iconCommands) {
        int delay = 0;
        for (IconCommand iconCommand : iconCommands) {
            if (iconCommand.getCommands().get(0) instanceof IconCommandDelay) {
                delay += ((IconCommandDelay) iconCommand.getCommands().get(0)).getDelay();
            } else if (random.nextDouble() <= iconCommand.getChange()) {
                Bukkit.getScheduler().runTaskLater(TabooMenu.getInst(), () -> iconCommand.getCommands().forEach(command -> command.execute(player)), delay);
            }
        }
    }

    /**
     * 执行物品点击动作
     *
     * @param player     玩家
     * @param clickEvent 点击事件
     * @param clickType  点击类型
     * @param icon       物品对象
     */
    public void executeClickAction(Player player, InventoryClickEvent clickEvent, ClickType clickType, Icon icon) {
        if (icon.getIconAction() != null && icon.getIconAction().getClickAction() != null) {
            SimpleBindings bindings = new SimpleBindings(ImmutableMap.of("player", player, "bukkit", Bukkit.getServer(), "clickType", clickType.name(), "clickEvent", clickEvent, "api", new InternalAPI(player, icon)));
            try {
                if (icon.getIconAction().isClickPrecompile()) {
                    icon.getIconAction().getClickActionScript().eval(bindings);
                } else {
                    ScriptHandler.compile(TranslateUtils.format(player, icon.getIconAction().getClickAction())).eval(bindings);
                }
            } catch (Exception e) {
                TabooMenu.getTLogger().error("Action-Click javascript is invalid: " + e.toString());
            }
        }
    }

    /**
     * 执行物品展示动作
     *
     * @param player    玩家
     * @param itemStack 物品
     * @param icon      物品对象
     */
    public void executeViewAction(Player player, ItemStack itemStack, Icon icon) {
        if (icon.getIconAction() != null && icon.getIconAction().getViewAction() != null) {
            SimpleBindings bindings = new SimpleBindings(ImmutableMap.of("player", player, "bukkit", Bukkit.getServer(), "viewItem", itemStack, "api", new InternalAPI(player, icon)));
            try {
                if (icon.getIconAction().isViewPrecompile()) {
                    icon.getIconAction().getViewActionScript().eval(bindings);
                } else {
                    ScriptHandler.compile(TranslateUtils.format(player, icon.getIconAction().getViewAction())).eval(bindings);
                }
            } catch (Exception e) {
                TabooMenu.getTLogger().error("Action-View javascript is invalid: " + e.toString());
            }
        }
    }

    /**
     * 获取有效物品图标（判断 Requirement）
     *
     * @param player    玩家
     * @param clickType 点击类型
     * @return {@link Icon}
     */
    public Icon getEffectiveIcon(Player player, ClickType clickType) {
        if (requirements.isEmpty()) {
            return this;
        }
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("player", player);
        bindings.put("bukkit", Bukkit.getServer());
        bindings.put("clickType", clickType.name());
        for (Requirement requirement : requirements) {
            bindings.put("api", new InternalAPI(player, requirement.getIcon()));
            try {
                Object result;
                if (requirement.isPreCompile()) {
                    result = requirement.getCompiledScript().eval(bindings);
                } else {
                    CompiledScript script = ScriptHandler.compile(TranslateUtils.format(player, requirement.getExpression()));
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
                isFull() == icon.isFull() &&
                isShiny() == icon.isShiny() &&
                isHideAttribute() == icon.isHideAttribute() &&
                getRequirementIndex() == icon.getRequirementIndex() &&
                getMaterial() == icon.getMaterial() &&
                Objects.equals(getName(), icon.getName()) &&
                Objects.equals(getLore(), icon.getLore()) &&
                Objects.equals(getBannerPatterns(), icon.getBannerPatterns()) &&
                Objects.equals(getColor(), icon.getColor()) &&
                Objects.equals(getSkullOwner(), icon.getSkullOwner()) &&
                Objects.equals(getSkullId(), icon.getSkullId()) &&
                Objects.equals(getSkullTexture(), icon.getSkullTexture()) &&
                Objects.equals(getPermission(), icon.getPermission()) &&
                Objects.equals(getPermissionMessage(), icon.getPermissionMessage()) &&
                Objects.equals(getPermissionView(), icon.getPermissionView()) &&
                Objects.equals(getIconAction(), icon.getIconAction()) &&
                Objects.equals(getEggType(), icon.getEggType()) &&
                Arrays.equals(getPotionType(), icon.getPotionType()) &&
                Objects.equals(getSlotCopy(), icon.getSlotCopy()) &&
                Objects.equals(getRequirements(), icon.getRequirements()) &&
                Objects.equals(getIconCommands(), icon.getIconCommands()) &&
                Objects.equals(getRequiredItems(), icon.getRequiredItems()) &&
                Objects.equals(getMenuName(), icon.getMenuName()) &&
                Objects.equals(getIconName(), icon.getIconName()) &&
                Objects.equals(getItemSource(), icon.getItemSource()) &&
                Objects.equals(getItem(), icon.getItem());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getMaterial(), getData(), getAmount(), getName(), getLore(), getBannerPatterns(), getColor(), getSkullOwner(), getSkullId(), getSkullTexture(), getPrice(), getPoints(), getLevels(), getPermission(), getPermissionMessage(), getPermissionView(), isFull(), isShiny(), isHideAttribute(), getIconAction(), getEggType(), getSlotCopy(), getRequirements(), getIconCommands(), getRequiredItems(), getMenuName(), getIconName(), getRequirementIndex(), getItemSource(), getItem());
        result = 31 * result + Arrays.hashCode(getPotionType());
        return result;
    }

    @Override
    public String toString() {
        return "Icon{" +
                "material=" + material +
                ", data=" + data +
                ", amount=" + amount +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", bannerPatterns=" + bannerPatterns +
                ", color=" + color +
                ", skullOwner='" + skullOwner + '\'' +
                ", skullId='" + skullId + '\'' +
                ", skullTexture='" + skullTexture + '\'' +
                ", price=" + price +
                ", points=" + points +
                ", levels=" + levels +
                ", permission='" + permission + '\'' +
                ", permissionMessage='" + permissionMessage + '\'' +
                ", permissionView='" + permissionView + '\'' +
                ", full=" + full +
                ", shiny=" + shiny +
                ", hideAttribute=" + hideAttribute +
                ", iconAction=" + iconAction +
                ", eggType='" + eggType + '\'' +
                ", potionType=" + Arrays.toString(potionType) +
                ", slotCopy=" + slotCopy +
                ", requirements=" + requirements +
                ", iconCommands=" + iconCommands +
                ", requiredItems=" + requiredItems +
                ", menuName='" + menuName + '\'' +
                ", iconName='" + iconName + '\'' +
                ", requirementIndex=" + requirementIndex +
                ", itemSource=" + itemSource +
                ", item=" + item +
                '}';
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

    public List<String> getBannerPatterns() {
        return bannerPatterns;
    }

    public void setBannerPatterns(List<String> bannerPatterns) {
        this.bannerPatterns = bannerPatterns;
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

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public IconAction getIconAction() {
        return iconAction;
    }

    public void setIconAction(IconAction iconAction) {
        this.iconAction = iconAction;
    }

    public Set<Integer> getSlotCopy() {
        return slotCopy;
    }

    public void setSlotCopy(Set<Integer> slotCopy) {
        this.slotCopy = slotCopy;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<IconCommand> getIconCommands() {
        return iconCommands;
    }

    public void setIconCommands(List<IconCommand> iconCommands) {
        this.iconCommands = iconCommands;
    }

    public List<RequiredItem> getRequiredItems() {
        return requiredItems;
    }

    public void setRequiredItems(List<RequiredItem> requiredItems) {
        this.requiredItems = requiredItems;
    }

    public String getEggType() {
        return eggType;
    }

    public void setEggType(String eggType) {
        this.eggType = eggType;
    }

    public String[] getPotionType() {
        return potionType;
    }

    public void setPotionType(String[] potionType) {
        this.potionType = potionType;
    }

    public String getSkullTexture() {
        return skullTexture;
    }

    public void setSkullTexture(String skullTexture) {
        this.skullTexture = skullTexture;
    }

    public String getSkullId() {
        return skullId;
    }

    public void setSkullId(String skullId) {
        this.skullId = skullId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getRequirementIndex() {
        return requirementIndex;
    }

    public void setRequirementIndex(int requirementIndex) {
        this.requirementIndex = requirementIndex;
    }

    public String getIndex() {
        return menuName.replace(".yml", "") + "," + iconName + "," + requirementIndex;
    }

    public String getIndexNoRequirement() {
        return menuName.replace(".yml", "") + "," + iconName;
    }

    public ItemStack getItemSource() {
        return itemSource;
    }

    public void setItemSource(ItemStack itemSource) {
        this.itemSource = itemSource;
    }

    public Item getItem() {
        return item;
    }
}
