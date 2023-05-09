package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AntiHopperListener implements Listener {

    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.HOPPER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.MINECART_HOPPER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem().getType() == Material.HOPPER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(CraftItemEvent event) {
        HumanEntity player = event.getWhoClicked();
        ItemStack craftResult = event.getInventory().getResult();

        if (craftResult != null && craftResult.getType() == Material.HOPPER) {
            event.setCancelled(true);
            CommonsUtil.sendMessage(player, "&7Hoppers are no longer available on the server, you must now update to using &aCollection Chests&7 to filter your loot from dropped mobs.");
        }
    }

}
