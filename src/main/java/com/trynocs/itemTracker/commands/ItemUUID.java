package com.trynocs.itemTracker.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.trynocs.itemTracker.main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@CommandAlias("getuuid")
@CommandPermission("trynocs.itemtracker.getuuid")
public class ItemUUID extends BaseCommand {
    @Default
    public void onExecute(CommandSender sender) {
        if (sender instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(main.prefix + "§cDu hältst kein Item in der Hand.");
                return;
            }
            ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    NamespacedKey key = new NamespacedKey(main.plugin, "item_uuid");
                    if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                        String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                        player.sendMessage(main.prefix + "§aUUID: §6" + uuidString);
                    } else {
                        player.sendMessage(main.prefix + "§cKeine UUID für dieses Item gefunden.");
                    }
                }
        }
    }
}
