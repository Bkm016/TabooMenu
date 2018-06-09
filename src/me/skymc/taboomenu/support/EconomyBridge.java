package me.skymc.taboomenu.support;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyBridge {

    private static Economy economy;

    public static boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static boolean hasValidEconomy() {
        return economy != null;
    }

    public static Economy getEconomy() {
        if (!hasValidEconomy()) {
            throw new IllegalStateException("Economy plugin was not found!");
        }
        return economy;
    }

    public static double getMoney(Player player) {
        if (!hasValidEconomy()) {
            throw new IllegalStateException("Economy plugin was not found!");
        }
        return economy.getBalance(player.getName(), player.getWorld().getName());
    }

    public static boolean hasMoney(Player player, double minimum) {
        if (!hasValidEconomy()) {
            throw new IllegalStateException("Economy plugin was not found!");
        }
        if (minimum < 0.0) {
            throw new IllegalArgumentException("Invalid amount of money: " + minimum);
        }
        return !(economy.getBalance(player.getName(), player.getWorld().getName()) < minimum);
    }

    /**
     * @return true if the operation was successful.
     */
    public static boolean takeMoney(Player player, double amount) {
        if (!hasValidEconomy()) {
            throw new IllegalStateException("Economy plugin was not found!");
        }
        if (amount < 0.0) {
            throw new IllegalArgumentException("Invalid amount of money: " + amount);
        }
        EconomyResponse response = economy.withdrawPlayer(player, player.getWorld().getName(), amount);
        return response.transactionSuccess();
    }

    public static boolean giveMoney(Player player, double amount) {
        if (!hasValidEconomy()) {
            throw new IllegalStateException("Economy plugin was not found!");
        }
        if (amount < 0.0) {
            throw new IllegalArgumentException("Invalid amount of money: " + amount);
        }
        EconomyResponse response = economy.depositPlayer(player, player.getWorld().getName(), amount);
        return response.transactionSuccess();
    }

    public static String formatMoney(double amount) {
        return hasValidEconomy() ? economy.format(amount) : Double.toString(amount);
    }
}
