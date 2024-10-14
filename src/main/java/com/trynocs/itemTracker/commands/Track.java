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
            sender.sendMessage(main.prefix + "§cBitte eine UUID angeben.");
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(itemID);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(main.prefix + "§cUngültige UUID.");
            return;
        }

        sender.sendMessage(main.prefix + "Suche Item...");

        for (World world : Bukkit.getWorlds()) {
            for (Item itemEnt : world.getEntitiesByClass(Item.class)) {
                if (itemEnt.getUniqueId().equals(uuid)) {
                    ItemStack item = itemEnt.getItemStack();
                    sendItemInfo(sender, item);
                    return;
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    if (hasUUID(item, uuid)) {
                        sendItemInfo(sender, item);
                        return;
                    }
                }
            }
        }

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getPlayer() != null) {
                for (int i = 0; i < offlinePlayer.getPlayer().getInventory().getSize(); i++) {
                    ItemStack item = offlinePlayer.getPlayer().getInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        if (hasUUID(item, uuid)) {
                            sendItemInfo(sender, item);
                            return;
                        }
                    }
                }
            }
        }

        sender.sendMessage(main.prefix + "§cKein Item mit dieser UUID gefunden.");
    }

    private boolean hasUUID(ItemStack item, UUID uuid) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(plugin, "item_uuid");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (main.getPlugin().getConfigManager().getConfig().getBoolean("mode.debug"))
                    System.out.println("Vergleiche UUID: " + uuidString + " mit " + uuid.toString());
                return uuidString.equals(uuid.toString());
            }
        }
        return false;
    }

    private void sendItemInfo(CommandSender sender, ItemStack item) {
        String itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                ? item.getItemMeta().getDisplayName()
                : item.getType().toString();

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "item_uuid");
        String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, main.prefix + "§aGetracktes Item: §6" + uuidString);

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, main.plugin.getPlacerholderItem());
        }

        inventory.setItem(22, item);

        Player owner = findItemOwner(item);
        if (owner != null) {
            if (owner.isOnline()) {
                inventory.setItem(38, new ItemBuilder(Material.PLAYER_HEAD).setHeadOwner(owner.getName()).setName("§aHolder Infos:").setLore("§aName: §6" + owner.getName(), "§aUUID: §6" + owner.getUniqueId(), "§aOperator: §6" + owner.isOp(), "§aStatus: §aOnline").build());
            }else inventory.setItem(38, new ItemBuilder(Material.PLAYER_HEAD).setHeadOwner(owner.getName()).setName("§aHolder Infos:").setLore("§aName: §6" + owner.getName(), "§aUUID: §6" + owner.getUniqueId(), "§aOperator: §6" + owner.isOp(), "§aStatus: §7Offline", "§aLetzter Login: §6" + owner.getLastPlayed()).build());
        } else inventory.setItem(38, new ItemBuilder(Material.SKELETON_SKULL).setName("§aHolder Infos:").setLore("Name: §7N/A", "UUID: §7N/A", "Operator: §7N/A", "Status: §7N/A", "§aLetzter Login: §7N/A").build());

        sender.sendMessage(main.prefix + "§aItem gefunden!");

        if (sender instanceof Player player) {
            player.openInventory(inventory);
        }
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
}