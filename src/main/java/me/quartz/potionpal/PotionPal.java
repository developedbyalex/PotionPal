package me.quartz.potionpal;

import me.quartz.potionpal.commands.PotionPalCommand;
import me.quartz.potionpal.listeners.BlockPlaceListener;
import me.quartz.potionpal.listeners.PlayerItemHeldListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PotionPal extends JavaPlugin {

    private static PotionPal instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("potionpal").setExecutor(new PotionPalCommand());
        getCommand("potionpal").setTabCompleter(new PotionPalCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PotionPal getInstance() {
        return instance;
    }
}
