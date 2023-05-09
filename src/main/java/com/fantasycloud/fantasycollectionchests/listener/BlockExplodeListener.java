package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.item.CollectionChestItem;
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
    public void onExplode(EntityExplodeEvent event){
        for (Block chest : event.blockList()){
            if (chest.getType() != Material.CHEST) continue;
            //event.setCancelled(true);
            chest.setType(Material.AIR);
            chest.getWorld().dropItemNaturally(chest.getLocation(), CollectionChestItem.getItem(1));
            FantasyCollectionChests.getInstance().getChestMemory().removeLocation(chest.getLocation());
            Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
                FantasyCollectionChests.getInstance().getChestSaver().removeChest(chest.getLocation());
            });
        }

        }

}
