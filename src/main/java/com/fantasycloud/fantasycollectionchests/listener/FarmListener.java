package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasyenchants.FantasyEnchants;
import me.krizzdawg.fantasycore.FantasyCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FarmListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onItemSpawn(ItemSpawnEvent event) {
        boolean cacheContains = FantasyCollectionChests.getInstance().getChestMemory().hasCachedChunk(event.getEntity().getLocation().getChunk());
        if (!cacheContains) {
            Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
                // Force a cache asynchronously.
                FantasyCollectionChests.getInstance().getChestMemory().getChest(event.getEntity().getLocation());
            });
            return;
        }

        CollectionChest chest = FantasyCollectionChests.getInstance().getChestMemory().getChest(event.getEntity().getLocation());
        ItemStack itemStack = event.getEntity().getItemStack();

        // Don't pick up special items in the chest
        if (isEnchantItem(itemStack) || isSoulGem(itemStack) || isScrollItem(itemStack) || isSoulPearl(itemStack)) {
            return;
        }

        if (chest != null && FantasyCollectionChests.getInstance().getChestConfiguration().getAcceptedMaterials().contains(itemStack.getType())) {
            chest.getStorage().addItem(itemStack);
            event.getEntity().remove();
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material blockType = block.getType();

        boolean cacheContains = FantasyCollectionChests.getInstance().getChestMemory().hasCachedChunk(block.getLocation().getChunk());
        if (!cacheContains) {
            Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
                // Force a cache asynchronously.
                FantasyCollectionChests.getInstance().getChestMemory().getChest(block.getLocation());
            });
            return;
        }

        // Check if the block is cactus or a crop (e.g., wheat, carrots, potatoes, etc.)
        if (blockType == Material.CACTUS) {
            CollectionChest chest = FantasyCollectionChests.getInstance().getChestMemory().getChest(block.getLocation());
            if (chest == null) return; // If no collection chest in chunk, do not interfere.

            List<ItemStack> drops = (List<ItemStack>) block.getDrops(player.getItemInHand()); // Get the drops from the block
            List<ItemStack> toRemove = new ArrayList<>();

            for (ItemStack drop : drops) {
                if (isEnchantItem(drop) || isSoulGem(drop) || isScrollItem(drop) || isSoulPearl(drop)) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                    continue;
                }

                if (FantasyCollectionChests.getInstance().getChestConfiguration().getAcceptedMaterials().contains(drop.getType())) {
                    boolean wasAdded = chest.getStorage().addItem(drop);
                    if (wasAdded) {
                        toRemove.add(drop);
                    }
                }
            }

            event.setCancelled(true); // Cancel the original block break event

            // Remove the block
            block.setType(Material.AIR);

            // Drop the remaining items on the ground
            for (ItemStack remainingDrop : toRemove) {
                block.getWorld().dropItemNaturally(block.getLocation(), remainingDrop);
            }
        }
    }


    private boolean isScrollItem(ItemStack drop) {
        return FantasyEnchants.getInstance().getEnchantsAPI().isScrollItem(drop);
    }

    private boolean isSoulGem(ItemStack drop) {
        return FantasyEnchants.getInstance().getSoulHandler().isSoulGem(drop);
    }

    private boolean isEnchantItem(ItemStack drop) {
        return FantasyEnchants.getInstance().getEnchantsAPI().isEnchantItem(drop);
    }

    private boolean isSoulPearl(ItemStack drop) {
        return FantasyCore.getEnchantmentHandler().isSoulPearl(drop);
    }
}