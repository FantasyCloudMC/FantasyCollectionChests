package com.fantasycloud.fantasycollectionchests.memory;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Guardian;

public class ChestAssurer {

    public void assureAllChests() {
        System.out.println("[Collection Chests] Assuring all chests");
        FantasyCollectionChests.getInstance().getChestFetcher().fetchAllChests().forEach(collectionChest -> {
            this.assureChest(collectionChest.getLocation().getLocation());
        });
    }

    public void assureChest(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.CHEST) {
            FantasyCollectionChests.getInstance().getChestSaver().removeChest(location);
            FantasyCollectionChests.getInstance().getChestMemory().removeLocation(location.getChunk());
            return;
        }
        Chest chest = (Chest) block.getState();
        if (!chest.getBlockInventory().getTitle().equalsIgnoreCase(CommonsUtil.color("&a&lCollection Chest"))) {
            FantasyCollectionChests.getInstance().getChestSaver().removeChest(location);
            FantasyCollectionChests.getInstance().getChestMemory().removeLocation(location.getChunk());
            return;
        }
    }

    public void assureChest(Chunk chunk) {
        CollectionChest collectionChest = FantasyCollectionChests.getInstance().getChestFetcher().fetchChest(chunk);
        if (collectionChest == null) {
            return;
        }
        this.assureChest(collectionChest.getLocation().getLocation());
    }

}
