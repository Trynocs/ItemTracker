package com.trynocs.itemTracker.listeners;

import com.trynocs.itemTracker.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class DupeDetector implements Listener {

    private ItemUUIDListener itemUUIDListener;

    private void notifyDupe(Player player, String eventName, UUID uuids, String uuidString) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("trynocs.itemtracker.dupenotify")) {
                onlinePlayer.sendMessage(main.prefix + "§c§lDupe detected.");
                onlinePlayer.sendMessage(main.prefix + "§c§lEvent: §4§l" + eventName);
                onlinePlayer.sendMessage(main.prefix + "§c§lPlayer Name: §4§l" + player.getName());
                onlinePlayer.sendMessage(main.prefix + "§c§lItem UUID: §4§l" + uuids);
                onlinePlayer.sendMessage(main.prefix + "§c§lMatched UUID: §4§l" + uuidString);
            }
        }
    }

    @EventHandler
    public void onItemCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                    if (uuidString.equals(uuids.toString())) {
                        notifyDupe((Player) event.getWhoClicked(), "CraftItemEvent", uuids, uuidString);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                    if (uuidString.equals(uuids.toString())) {
                        notifyDupe(event.getPlayer(), "PlayerPickupItemEvent", uuids, uuidString);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                    if (uuidString.equals(uuids.toString())) {
                        notifyDupe(event.getPlayer(), "PlayerDropItemEvent", uuids, uuidString);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                    if (uuidString.equals(uuids.toString())) {
                        notifyDupe((Player) event.getWhoClicked(), "InventoryClickEvent", uuids, uuidString);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (ItemStack item : event.getNewItems().values()) {
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
                if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                        if (uuidString.equals(uuids.toString())) {
                            notifyDupe((Player) event.getWhoClicked(), "InventoryDragEvent", uuids, uuidString);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack item = new ItemStack(event.getBlock().getType());
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                    if (uuidString.equals(uuids.toString())) {
                        notifyDupe(event.getPlayer(), "BlockBreakEvent", uuids, uuidString);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        for (ItemStack item : event.getDrops()) {
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
                if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                        if (uuidString.equals(uuids.toString())) {
                            notifyDupe(event.getEntity().getKiller(), "EntityDeathEvent", uuids, uuidString);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSmithItem(PrepareSmithingEvent event) {
        ItemStack item = event.getResult();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                    if (uuidString.equals(uuids.toString())) {
                        notifyDupe((Player) event.getView().getPlayer(), "PrepareSmithingEvent", uuids, uuidString);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        ItemStack item = new ItemStack(event.getItemType(), event.getItemAmount());
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                for (UUID uuids : itemUUIDListener.getUsedUUIDs()) {
                    if (uuidString.equals(uuids.toString())) {
                        notifyDupe(event.getPlayer(), "FurnaceExtractEvent", uuids, uuidString);
                    }
                }
            }
        }
    }
}