package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.item.CollectionChestItem;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycommons.nbt.NBTItem;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.type.Switch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class BlockPlaceListener implements Listener {

    private static final BlockFace[] faces = {
            BlockFace.WEST,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.NORTH
    };

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.CHEST) return;
        NBTItem nbtItem = new NBTItem(event.getItemInHand());
        boolean isCollectionChest = nbtItem.hasKey("isCollectionChest");
        // Check for turning a collection chest into double chest.
        for (BlockFace blockFace : faces) {
            Block block = event.getBlockPlaced().getRelative(blockFace);
            if (block.getType() != Material.CHEST) continue;
            if (isCollectionChest) {
                event.getPlayer().sendMessage(FantasyCollectionChests.getInstance().getMessage("conjoined-chest"));
                event.setCancelled(true);
                return;
            }
            Chest chest = (Chest) event.getBlockPlaced().getState();
            if (chest.getInventory().getTitle().equalsIgnoreCase(FantasyCollectionChests.getInstance()
                    .getChestConfiguration().getChestName())) {
                event.getPlayer().sendMessage(FantasyCollectionChests.getInstance().getMessage("conjoined-chest"));
                event.setCancelled(true);
                return;
            }
        }
        if (!isCollectionChest) return;
        // return if 1 collection chest already in chunk.
        Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
                if (FantasyCollectionChests.getInstance().getChestMemory().getChest(event.getBlockPlaced().getLocation()) != null) {
                    Bukkit.getScheduler().runTask(FantasyCollectionChests.getInstance(), () -> {
                        event.getBlockPlaced().setType(Material.AIR);
                        event.getPlayer().sendMessage(FantasyCollectionChests.getInstance().getMessage("too-many-chests"));
                        event.getPlayer().getInventory().addItem(CollectionChestItem.getItem(1));
                    });
                } else {
                    CollectionChest chest = FantasyCollectionChests.getInstance().getChestSaver().addNewChest(event.getBlockPlaced().getLocation());
                    FantasyCollectionChests.getInstance().getChestMemory().registerCacheChest(chest);
                }
        });
    }

}
