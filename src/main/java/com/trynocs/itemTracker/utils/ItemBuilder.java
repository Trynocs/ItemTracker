package com.trynocs.itemTracker.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;
    private SkullMeta skullMeta;

    public ItemBuilder(Material material, short subID) {
        item = new ItemStack(material, 1, subID);
        meta = item.getItemMeta();

        if (meta instanceof SkullMeta) {
            skullMeta = (SkullMeta) meta;
        } else {
            skullMeta = null; // oder andere Logik
        }
    }

    public ItemBuilder(Material material) {
        this(material, (short) 0);
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLocalizedName(String localName) {
        meta.setLocalizedName(localName);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        meta.setCustomModelData(customModelData);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setHeadOwner(String owner) {
        if (Bukkit.getPlayer(owner) != null && Bukkit.getPlayer(owner).isOnline()) {
            Player player = Bukkit.getPlayerExact(owner);
            skullMeta.setOwningPlayer(player);
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);
            skullMeta.setOwningPlayer(offlinePlayer);
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
