package me.skymc.taboomenu.template;

import me.skymc.taboomenu.TabooMenu;
import me.skymc.taboomenu.display.Menu;
import me.skymc.taboomenu.inventory.TemplateHolder;
import me.skymc.taboomenu.serialize.MenuSerializer;
import me.skymc.taboomenu.util.TranslateUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

/**
 * @Author sky
 * @Since 2018-06-30 12:14
 */
public class TemplateManager {

    private static File templateFolder;

    public static void init() {
        createTemplateFolder();
    }

    public static boolean isTemplateExists(String template) {
        return new File(templateFolder, template.endsWith(".yml") ? template : template + ".yml").exists();
    }

    public static List<String> getTemplates() {
        List<String> list = new ArrayList<>();
        Arrays.stream(templateFolder.listFiles()).forEach(x -> list.add(x.getName()));
        return list;
    }

    public static void createTemplate(Player player, String template, int rows, String name) {
        File file = new File(templateFolder, template.endsWith(".yml") ? template : template + ".yml");
        if (file.exists()) {
            return;
        }
        try {
            file.createNewFile();
        } catch (Exception e) {
            player.sendMessage("§7[TabooMenu] §4Template §c" + template + "§4 create failed: " + e.toString());
            return;
        }
        YamlConfiguration configuration = TranslateUtils.loadConfiguration(file);
        configuration.set("menu-settings.name", name);
        configuration.set("menu-settings.rows", rows);
        try {
            configuration.save(file);
        } catch (Exception e) {
            player.sendMessage("§7[TabooMenu] §4Template §c" + template + "§4 save failed: " + e.toString());
        }
    }

    public static void openTemplate(Player player, String template) {
        File file = new File(templateFolder, template.endsWith(".yml") ? template : template + ".yml");
        if (!file.exists()) {
            player.sendMessage("§7[TabooMenu] §4Template §c" + template + "§4 not found.");
            return;
        }
        List<String> errors = new ArrayList<>();
        Menu menu = MenuSerializer.loadMenu(file, errors);
        if (!errors.isEmpty()) {
            TranslateUtils.printErrors(player, errors);
        }
        Inventory inventory = Bukkit.createInventory(new TemplateHolder(template), menu.getRows() * 9, TranslateUtils.format(player, menu.getName()));
        menu.refresh(player, inventory);
        player.openInventory(inventory);
    }

    public static void saveTemplate(Player player, String template, Inventory inventory) {
        File file = new File(templateFolder, template.endsWith(".yml") ? template : template + ".yml");
        if (!file.exists()) {
            player.sendMessage("§7[TabooMenu] §4Template §c" + template + "§4 not found.");
            return;
        }
        YamlConfiguration configuration = TranslateUtils.loadConfiguration(file);
        String name = configuration.getString("menu-settings.name");
        int rows = configuration.getInt("menu-settings.rows");
        configuration = new YamlConfiguration();
        configuration.set("menu-settings.name", name);
        configuration.set("menu-settings.rows", rows);
        HashMap<ItemStack, List<Integer>> items = new HashMap<>();
        boolean isEmpty = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                items.computeIfAbsent(itemStack, x -> new ArrayList<>()).add(i);
            } else {
                isEmpty = true;
            }
        }
        List<List<Integer>> slotRepeats = new ArrayList<>();
        if (!isEmpty) {
            slotRepeats.addAll(items.values());
            slotRepeats.sort((b, a) -> Integer.compare(a.size(), b.size()));
        }
        HashMap<Integer, Map<String, Object>> icons = new HashMap<>();
        for (Map.Entry<ItemStack, List<Integer>> itemEntry : items.entrySet()) {
            Map<String, Object> map = TemplateSerializer.serialize(itemEntry.getKey());
            if (!isEmpty && itemEntry.getValue().equals(slotRepeats.iterator().next())) {
                map.put("full", true);
            } else if (itemEntry.getValue().size() > 1) {
                List<Integer> slotCopy = new ArrayList<>(itemEntry.getValue());
                slotCopy.remove(0);
                map.put("slot-copy", slotCopy);
            }
            icons.put(itemEntry.getValue().get(0), map);
        }
        List<Integer> slots = new ArrayList<>(icons.keySet());
        slots.sort(Integer::compareTo);
        for (Integer slot : slots) {
            configuration.set(String.valueOf(slot), icons.get(slot));
        }
        try {
            configuration.save(file);
            player.sendMessage("§7[TabooMenu] §fTemplate §7" + template + "§f save successfully.");
        } catch (Exception e) {
            player.sendMessage("§7[TabooMenu] §4Template §c" + template + "§4 save failed: " + e.toString());
        }
    }

    private static void createTemplateFolder() {
        templateFolder = new File(TabooMenu.getInst().getDataFolder(), "template");
        if (!templateFolder.exists()) {
            templateFolder.mkdirs();
        }
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static File getTemplateFolder() {
        return templateFolder;
    }
}
