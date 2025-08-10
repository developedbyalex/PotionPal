package me.quartz.potionpal;

import me.quartz.potionpal.commands.PotionPalCommand;
import me.quartz.potionpal.listeners.BlockPlaceListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class PotionPal extends JavaPlugin {

    private static PotionPal instance;
    private PotionApplicationTask task;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        PluginCommand cmd = getCommand("potionpal");
        if(cmd != null){
            PotionPalCommand command =
                    new PotionPalCommand();
            cmd.setExecutor(command);
            cmd.setTabCompleter(command);
        }
        this.task = new PotionApplicationTask();
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
    }

    public PotionApplicationTask getTask() {
        return task;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PotionPal getInstance() {
        return instance;
    }
}
