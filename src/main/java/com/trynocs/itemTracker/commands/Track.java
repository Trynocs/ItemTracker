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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@CommandAlias("track|trackitem|itemtracker")
@CommandPermission("trynocs.itemtracker.track")
public class Track extends BaseCommand implements Listener {

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
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "item_uuid");
        String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, main.prefix + "§aGetracktes Item: §6" + uuidString);

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
        inventory.setItem(42, new ItemBuilder(Material.PLAYER_HEAD)
                .setHeadOwner(findItemOwner(item) != null ? findItemOwner(item).getName() : "N/A")
                .setName("§6Holder Infos:")
                .setLore(
                        "§aName: §e" + (findItemOwner(item) != null ? findItemOwner(item).getName() : "N/A"),
                        "§aUUID: §e" + uuidString,
                        "§aOperator: §e" + (findItemOwner(item) != null && findItemOwner(item).isOp() ? "§aJa" : "§cNein"),
                        "§aStatus: " + (findItemOwner(item) != null && findItemOwner(item).isOnline() ? "§aOnline" : "§cOffline"),
                        "§aLetzter Login: §e" + (findItemOwner(item) != null ? findItemOwner(item).getLastPlayed() : "N/A")
                ).build());

        inventory.setItem(38, createItemInfo(item, uuidString));
        inventory.setItem(22, item);

        sender.sendMessage(main.prefix + "§aItem gefunden!");

        if (sender instanceof Player player) {
            player.openInventory(inventory);
        }
    }

    private ItemStack createItemInfo(ItemStack item, String uuidString) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK)
                .setName("§6Item Informationen")
                .setLore(
                        "§aName: §e" + item.getType().toString(),
                        "§aUUID: §e" + uuidString,
                        "§aMenge: §e" + item.getAmount(),
                        "§aHaltbarkeit: §e" + item.getDurability(),
                        "§aEnchantments: §e" + (item.getEnchantments().isEmpty() ? "§cKeine" : item.getEnchantments().toString())
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

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getItem(22) == null || event.getInventory().getItem(22).getType() == Material.AIR) return;
        if (event.getInventory() != null && event.getView().getTitle().equals(main.prefix + "§aGetracktes Item: §6" + event.getClickedInventory().getItem(22).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "item_uuid"), PersistentDataType.STRING))) {
            event.setCancelled(true);
        }
    }
}
