package com.trynocs.itemTracker;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.contexts.ContextResolver;
import com.trynocs.itemTracker.commands.ItemUUID;
import com.trynocs.itemTracker.commands.Track;
import com.trynocs.itemTracker.listeners.DupeDetector;
import com.trynocs.itemTracker.listeners.ItemUUIDListener;
import com.trynocs.itemTracker.utils.ItemBuilder;
import com.trynocs.itemTracker.utils.config.Configmanager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.basic.BasicSliderUI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class main extends JavaPlugin {

    public static main plugin;

    public static String prefix;
    public static String noperm;
    public static String beplayer;
    public static String noplayer;

    private Configmanager configManager;
    private PaperCommandManager commandManager;
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        getLogger().info("");
        getLogger().info("██╗████████╗");
        getLogger().info("██║╚══██╔══╝");
        getLogger().info("██║░░░██║░░░");
        getLogger().info("██║░░░██║░░░");
        getLogger().info("██║░░░██║░░░");
        getLogger().info("╚═╝░░░╚═╝░░░");
        getLogger().info("");
        getLogger().info("Plugin wird aktiviert...");

        plugin = this;
        configManager = new Configmanager(this);
        pluginManager = Bukkit.getPluginManager();
        commandManager = new PaperCommandManager(this);

        registerUUIDContextResolver();
        loadConfigValues();
        register();
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin wurde deaktiviert.");
    }

    private void registerUUIDContextResolver() {
        commandManager.getCommandContexts().registerContext(UUID.class, (c) -> {
            String arg = c.popFirstArg();
            try {
                return UUID.fromString(arg);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ungültige UUID: " + arg);
            }
        });
    }

    public void register() {
        // Command Registrierung
        Track track = new Track(this);
        commandManager.registerCommand(track);
        commandManager.registerCommand(new ItemUUID());
        pluginManager.registerEvents((Listener) track, this);
        pluginManager.registerEvents(new ItemUUIDListener(this), this);
        pluginManager.registerEvents(new DupeDetector(), this);
    }

    private void loadConfigValues() {
        String prefix2 = configManager.getConfig().getString("messages.prefix", "&b&lItemTracker &8» &7");
        String noperm2 = configManager.getConfig().getString("messages.no-perm", "Dazu hast du keine Rechte.");
        String beplayer2 = configManager.getConfig().getString("messages.not-player", "Du musst ein Spieler sein, um diesen Command auszuführen.");
        String noplayer2 = configManager.getConfig().getString("messages.no-player", "Dieser Spieler ist offline oder existiert nicht.");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix2);
        noperm = main.prefix + ChatColor.translateAlternateColorCodes('&', noperm2);
        beplayer = main.prefix + ChatColor.translateAlternateColorCodes('&', beplayer2);
        noplayer = main.prefix + ChatColor.translateAlternateColorCodes('&', noplayer2);
    }

    public static main getPlugin() {
        return plugin;
    }

    public Configmanager getConfigManager() {
        return configManager;
    }

    public static String translateColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translateColors(List<String> texts) {
        return texts.stream().map(main::translateColors).collect(Collectors.toList());
    }

    public ItemStack getPlacerholderItem() {
        return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§7").build();
    }
}
