package com.trynocs.itemTracker.utils.config;

import com.trynocs.itemTracker.main;
import com.trynocs.itemTracker.main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Configmanager {
    private final main plugin;
    private FileConfiguration config = null;
    private File configFile = null;
    private final Map<String, FileConfiguration> customConfigs = new HashMap<>();
    private final Map<String, File> customConfigFiles = new HashMap<>();

    public Configmanager(main plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void saveDefaultConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }

    public void createCustomConfig(String name) {
        if (!customConfigFiles.containsKey(name)) {
            File file = new File(plugin.getDataFolder(), name + ".yml");
            customConfigFiles.put(name, file);
            if (!file.exists()) {
                plugin.saveResource(name + ".yml", false);
            }
        }
    }

    public void reloadCustomConfig(String name) {
        if (!customConfigFiles.containsKey(name)) {
            createCustomConfig(name);
        }
        File file = customConfigFiles.get(name);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(file);
        customConfigs.put(name, customConfig);
    }

    public FileConfiguration getCustomConfig(String name) {
        if (!customConfigs.containsKey(name)) {
            reloadCustomConfig(name);
        }
        return customConfigs.get(name);
    }

    public void saveCustomConfig(String name) {
        if (customConfigs.containsKey(name)) {
            File file = customConfigFiles.get(name);
            FileConfiguration customConfig = customConfigs.get(name);
            try {
                customConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
