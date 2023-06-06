package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import com.massivecraft.factions.*;
import com.massivecraft.factions.access.AccessChunk;
import com.massivecraft.factions.access.AccessPerm;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Random;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        Chest chest = (Chest) event.getClickedBlock().getState();
        if (!chest.getBlockInventory().getTitle().equalsIgnoreCase(CommonsUtil.color("&a&lCollection Chest"))) return;
        event.setCancelled(true);
        FPlayer opener = FPlayers.getInstance().getByPlayer(event.getPlayer());
        Faction factionAt = Board.getInstance().getFactionAt(new FLocation(chest.getLocation()));

        if (event.getPlayer().getLocation().distance(chest.getLocation()) > 10.0) { // stop damn freecammers.
            return;
        }

        try {
            AccessChunk accessChunk = factionAt.getFactionAccess().getAccessChunk(event.getClickedBlock().getLocation().getChunk(), false);
            boolean hasChunkAccess = false;

            if (accessChunk != null) {
                hasChunkAccess = accessChunk.hasPermission(opener, AccessPerm.COLLECTIONCHEST);
            }

            if (opener.isAdminBypassing()) {
                hasChunkAccess = true;
            }

            if (!hasChunkAccess) {
                if (!factionAt.isWilderness() && opener.getFaction().getUniqueId() != factionAt.getUniqueId()) {
                    event.getPlayer().sendMessage(CommonsUtil.color("&c&l(&c!&c&l) &cYou cannot access a collection chest you do not own!"));
                    return;
                }

            }
        } catch (NullPointerException e) {
            if (!factionAt.getUniqueId().equals(opener.getFaction().getUniqueId()) && !opener.isAdminBypassing()) {
                event.getPlayer().sendMessage(CommonsUtil.color("&c&l(&c!&c&l) &cYou cannot access a collection chest you do not own!"));
                return;
            }
        }

        CollectionChest collectionChest = FantasyCollectionChests.getInstance().getChestMemory().getChest(event.getClickedBlock().getLocation());

     //   if (collectionChest == null) {
     //       FantasyCollectionChests.getInstance().getChestMemory().registerCacheChest(event.getClickedBlock().getLocation(), new CollectionChest(new Random().nextInt(9999), event.getClickedBlock().getLocation()));
     //   }

        Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
            CollectionChest cc = FantasyCollectionChests.getInstance().getChestMemory().getChest(event.getClickedBlock().getLocation());

            if (FantasyCollectionChests.getInstance().getChestInventory().registerPlayerChest(event.getPlayer(), cc)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(FantasyCollectionChests.getInstance().getMessage("player-using-chest"));
                return;
            }
            event.setCancelled(true);
            FantasyCollectionChests.getInstance().getChestInventory().getInventory().open(event.getPlayer());
        });
    }

}
