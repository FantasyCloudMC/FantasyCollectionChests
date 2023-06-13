package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import com.massivecraft.factions.*;
import com.massivecraft.factions.access.AccessChunk;
import com.massivecraft.factions.access.AccessPerm;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerInteractListener implements Listener {

    private final Set<Location> lockedChests = new HashSet<>();
    private final Map<Location, UUID> chestOwners = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        Chest chest = (Chest) event.getClickedBlock().getState();
        if (!chest.getBlockInventory().getTitle().equalsIgnoreCase(CommonsUtil.color("&a&lCollection Chest"))) return;

        event.setCancelled(true);

        Location chestLocation = chest.getLocation();
        CollectionChest collectionChest = FantasyCollectionChests.getInstance().getChestMemory().getChest(chestLocation);

        if (collectionChest == null) {
            collectionChest = new CollectionChest(new Random().nextInt(9999), chestLocation);
            FantasyCollectionChests.getInstance().getChestMemory().registerCacheChest(chestLocation, collectionChest);
        }

        Player player = event.getPlayer();

        if (!isChestAvailable(chestLocation)) {
            player.sendMessage(CommonsUtil.color("&a&lFantasy&f&lCChest &7&l➥ &cYou cannot open this collection chest because it is already opened by another player."));
            return;
        }

        if (FantasyCollectionChests.getInstance().getChestInventory().registerPlayerChest(player, collectionChest)) {
            // Open the collection chest inventory for the player
            FantasyCollectionChests.getInstance().getChestInventory().openInventory(player, collectionChest);
            player.sendMessage(CommonsUtil.color("&a&lFantasy&f&lCChest &7&l➥ &aYou have successfully opened the collection chest."));
        } else {
            player.sendMessage(CommonsUtil.color("&a&lFantasy&f&lCChest &7&l➥ &cYou cannot open this collection chest because it is already opened by another player."));
        }
    }

    private boolean isChestAvailable(Location chestLocation) {
        synchronized (lockedChests) {
            return !lockedChests.contains(chestLocation);
        }
    }

}


