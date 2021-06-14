package ru.starrysparklez.noblockdrops;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {
    Logger logger;

    public void onEnable() {
        logger = Bukkit.getLogger();
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
    }
}
