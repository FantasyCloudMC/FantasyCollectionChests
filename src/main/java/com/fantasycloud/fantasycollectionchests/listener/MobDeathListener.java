package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import org.bukkit.Bukkit;
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
        List<ItemStack> toRemove = new ArrayList<>();
        for (ItemStack drop : event.getDrops()) {
            if (FantasyCollectionChests.getInstance().getChestConfiguration().getAcceptedMaterials().contains(drop.getType())) {
                chest.getStorage().addItem(drop);
                toRemove.add(drop);
            }
        }
        event.getDrops().removeAll(toRemove);
    }



}
