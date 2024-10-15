package com.trynocs.itemTracker.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import com.trynocs.itemTracker.main;
import com.trynocs.itemTracker.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@CommandAlias("track|trackitem|itemtracker")
@CommandPermission("trynocs.itemtracker.track")
public class Track extends BaseCommand {

    private final Plugin plugin;

    public Track(Plugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(CommandSender sender, @Optional String itemID, String[] args) {
        if (itemID == null) {
            sender.sendMessage(main.prefix + "§cPlease provide a UUID.");
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(itemID);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(main.prefix + "§cInvalid UUID.");
            return;
        }

        sender.sendMessage(main.prefix + "Searching for item...");

        // 1. Checking for ground items in all worlds
        for (World world : Bukkit.getWorlds()) {
            for (Item itemEnt : world.getEntitiesByClass(Item.class)) {
                if (hasUUID(itemEnt.getItemStack(), uuid)) {
                    sendItemInfo(sender, itemEnt.getItemStack());
                    return;
                }
            }
        }

        // 2. Checking for items in online players' inventories
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && hasUUID(item, uuid)) {
                    sendItemInfo(sender, item);
                    return;
                }
            }
        }

        sender.sendMessage(main.prefix + "§cNo item found with this UUID.");
    }

    private boolean hasUUID(ItemStack item, UUID uuid) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                return uuidString.equals(uuid.toString());
            }
        }
        return false;
    }

    private void sendItemInfo(CommandSender sender, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "item_uuid");
        String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, main.prefix + "§aTracked Item: §6" + uuidString);

        // Fill the inventory with glass panes
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName("§7").build());
        }
        inventory.setItem(9, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("§7").build());
        inventory.setItem(17, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("§7").build());
        inventory.setItem(18, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("§7").build());
        inventory.setItem(26, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("§7").build());
        inventory.setItem(27, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("§7").build());
        inventory.setItem(35, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("§7").build());
        inventory.setItem(36, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("§7").build());
        inventory.setItem(44, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName("§7").build());

        // Add the item info
        inventory.setItem(38, createItemInfo(item, uuidString));
        inventory.setItem(22, item); // Center item

        Player onlineOwner = findItemOwner(item);
        if (onlineOwner != null) {
            addPlayerInfoToInventory(inventory, onlineOwner, uuidString, item);
        }

        sender.sendMessage(main.prefix + "§aItem found!");

        if (sender instanceof Player player) {
            player.openInventory(inventory);
        }
    }

    private void addPlayerInfoToInventory(Inventory inventory, Player player, String uuidString, ItemStack item) {
        inventory.setItem(42, new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadOwner(player.getName())
                .setName("§6Holder Info:")
                .setLore(
                        "§aName: §e" + player.getName(),
                        "§aUUID: §e" + uuidString,
                        "§aOperator: §e" + (player.isOp() ? "§aYes" : "§cNo"),
                        "§aStatus: §aOnline",
                        "§aLast Login: §e" + formatDate(player.getLastPlayed())
                ).build());
    }

    private ItemStack createItemInfo(ItemStack item, String uuidString) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK)
                .setName("§6Item Information")
                .setLore(
                        "§aName: §e" + item.getType().toString(),
                        "§aUUID: §e" + uuidString,
                        "§aAmount: §e" + item.getAmount(),
                        "§aDurability: §e" + item.getDurability(),
                        "§aEnchantments: §e" + (item.getEnchantments().isEmpty() ? "§cNone" : item.getEnchantments().toString())
                );
        return itemBuilder.build();
    }

    private Player findItemOwner(ItemStack item) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack playerItem : player.getInventory().getContents()) {
                if (playerItem != null && playerItem.isSimilar(item)) {
                    return player;
                }
            }
        }
        return null;
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
}
