package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.item.CollectionChestItem;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycommons.nbt.NBTFile;
import com.fantasycloud.fantasycommons.nbt.NBTItem;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    // prevent interfering with factions.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.CHEST) return;
        Chest chest = (Chest) event.getBlock().getState();
        if (!chest.getBlockInventory().getTitle().equalsIgnoreCase(CommonsUtil.color("&a&lCollection Chest"))) return;
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), CollectionChestItem.getItem(1));
        FantasyCollectionChests.getInstance().getChestMemory().removeLocation(event.getBlock().getLocation());
        Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
            FantasyCollectionChests.getInstance().getChestSaver().removeChest(event.getBlock().getLocation());
        });
    }




}
