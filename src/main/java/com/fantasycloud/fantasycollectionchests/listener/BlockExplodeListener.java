package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.item.CollectionChestItem;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycommons.nbt.NBTItem;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockExplodeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (!FantasyCollectionChests.getInstance().getChestMemory().hasCachedChunk(event.getLocation().getChunk())) {
            return;
        }

        for (Block block : event.blockList()) {
            if (block.getType() != Material.CHEST) continue;
            CollectionChest collectionChest = FantasyCollectionChests.getInstance().getChestFetcher().fetchChest(block.getLocation());

            if (collectionChest == null) { // there is no collection chest in the chunk
                return;
            }

            Chest chest = (Chest) block.getState();
            if (!chest.getBlockInventory().getTitle().equalsIgnoreCase(CommonsUtil.color("&a&lCollection Chest")))
                continue;

            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(block.getLocation(), CollectionChestItem.getItem(1));
            FantasyCollectionChests.getInstance().getChestMemory().removeLocation(block.getLocation());
            Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
                FantasyCollectionChests.getInstance().getChestSaver().removeChest(block.getLocation());
            });
            break;

        }
    }

}
