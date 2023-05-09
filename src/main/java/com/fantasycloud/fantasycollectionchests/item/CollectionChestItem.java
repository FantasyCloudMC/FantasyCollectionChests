package com.fantasycloud.fantasycollectionchests.item;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycommons.nbt.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CollectionChestItem {

    public static ItemStack getItem(int amount) {
        ItemStack item = new ItemStack(Material.CHEST);
        item.setAmount(amount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(FantasyCollectionChests.getInstance().getChestConfiguration().getChestName());
        itemMeta.setLore(FantasyCollectionChests.getInstance().getChestConfiguration().getChestLore());
        item.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("isCollectionChest", true);
        return nbtItem.getItem();
    }

}
