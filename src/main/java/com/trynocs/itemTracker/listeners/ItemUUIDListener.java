package com.trynocs.itemTracker.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ItemUUIDListener implements Listener {

    private final Plugin plugin;
    private final Set<UUID> usedUUIDs = new HashSet<>();
    private final Map<ItemStack, String> itemUUIDMap = new HashMap<>();

    public ItemUUIDListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemCraft(CraftItemEvent event) {
        ItemStack result = event.getCurrentItem();
        if (result != null) {
            addUUIDToItem(result);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        addUUIDToItem(event.getItem().getItemStack());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        addUUIDToItem(event.getItemDrop().getItemStack());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            addUUIDToItem(currentItem);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (ItemStack item : event.getNewItems().values()) {
            addUUIDToItem(item);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack drop = new ItemStack(event.getBlock().getType());
        addUUIDToItem(drop);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        for (ItemStack item : event.getDrops()) {
            addUUIDToItem(item);
        }
    }

    @EventHandler
    public void onSmithItem(PrepareSmithingEvent event) {
        ItemStack result = event.getResult();
        if (result != null) {
            addUUIDToItem(result);
        }
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        ItemStack item = new ItemStack(event.getItemType(), event.getItemAmount());
        addUUIDToItem(item);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        for (ItemStack item : inventory.getContents()) {
            addUUIDToItem(item);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() != Material.AIR) {
            addUUIDToItem(item);
        }
    }

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result != null) {
            addUUIDToItem(result);
        }
    }

    @EventHandler
    public void onGrindstoneUse(PrepareGrindstoneEvent event) {
        ItemStack result = event.getResult();
        if (result != null) {
            addUUIDToItem(result);
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        addUUIDToItem(event.getItem());
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() instanceof ItemStack caughtItem) {
            addUUIDToItem(caughtItem);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (command.startsWith("/give")) {
            String[] args = command.split(" ");
            if (args.length >= 3) {
                Material material = Material.matchMaterial(args[2]);
                if (material != null) {
                    ItemStack item = new ItemStack(material, 1);
                    addUUIDToItem(item);
                }
            }
        }
    }

    private void addUUIDToItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey(plugin, "item_uuid");

        if (!meta.getEnchants().isEmpty() || meta.hasDisplayName() || meta.hasLore()) {
            if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String newUUID = generateUniqueUUID();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, newUUID);
                item.setItemMeta(meta);
                itemUUIDMap.put(item, newUUID);
            }
        }
    }

    private String generateUniqueUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (usedUUIDs.contains(uuid));
        usedUUIDs.add(uuid);
        return uuid.toString();
    }

    public Set<UUID> getUsedUUIDs() {
        return usedUUIDs;
    }
}
