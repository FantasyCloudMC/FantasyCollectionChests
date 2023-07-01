package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasyenchants.FantasyEnchants;
import me.krizzdawg.fantasycore.FantasyCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MobDeathListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDeathEvent event) {
        boolean cacheContains = FantasyCollectionChests.getInstance().getChestMemory().hasCachedChunk(event.getEntity().getLocation().getChunk());
        if (!cacheContains) {
            Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
                // force a cache asynchronously.
                FantasyCollectionChests.getInstance().getChestMemory().getChest(event.getEntity().getLocation());
            });
            return;
        }
        CollectionChest chest = FantasyCollectionChests.getInstance().getChestMemory().getChest(event.getEntity().getLocation());
        if (chest == null) return; // if no collection chest in chunk do not interfere.

        List<ItemStack> drops = event.getDrops();
        List<ItemStack> toRemove = new ArrayList<>();

        for (ItemStack drop : new ArrayList<>(drops)) { // create a copy to avoid concurrent modification issues
            if (isEnchantItem(drop) || isSoulGem(drop) || isScrollItem(drop) || isSoulPearl(drop)) {
                continue; // Skip to the next drop, it will be dropped naturally
            }

            if (FantasyCollectionChests.getInstance().getChestConfiguration().getAcceptedMaterials().contains(drop.getType())) {
                boolean wasAdded = chest.getStorage().addItem(drop);
                if (wasAdded) {
                    toRemove.add(drop); // If the item was added to the chest, add it to the toRemove list
                }
            }
        }

        // Remove the items that were added to the chest from the drop list
        drops.removeAll(toRemove);
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
