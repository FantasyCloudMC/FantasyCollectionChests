package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import com.massivecraft.factions.*;
import com.massivecraft.factions.access.AccessChunk;
import com.massivecraft.factions.access.AccessPerm;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerInteractListener implements Listener {

    private final Set<Location> lockedChests = new HashSet<>();
    private final Map<Location, UUID> chestOwners = new HashMap<>();
    private final Map<UUID, Long> lastAccessed = new HashMap<>();

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

        FPlayer opener = FPlayers.getInstance().getByPlayer(event.getPlayer());
        Faction factionAt = Board.getInstance().getFactionAt(new FLocation(chest.getLocation()));
        if (event.getPlayer().getLocation().distance(chest.getLocation()) > 10.0D)
            return;
        try {
            AccessChunk accessChunk = factionAt.getFactionAccess().getAccessChunk(event.getClickedBlock().getLocation().getChunk(), false);
            boolean hasChunkAccess = false;
            if (accessChunk != null)
                hasChunkAccess = accessChunk.hasPermission(opener, AccessPerm.COLLECTIONCHEST);
            if (!hasChunkAccess &&
                    !factionAt.isWilderness() && opener.getFaction().getUniqueId() != factionAt.getUniqueId()) {
                event.getPlayer().sendMessage(CommonsUtil.color("&a&lCollection Chests &7&l➥ &cYou cannot access a collection chest you do not own!"));
                return;
            }
        } catch (NullPointerException e) {
            if (!factionAt.getUniqueId().equals(opener.getFaction().getUniqueId())) {
                event.getPlayer().sendMessage(CommonsUtil.color("&a&lCollection Chests &7&l➥ &cYou cannot access a collection chest you do not own!"));
                return;
            }
        }

        String coordString = chestLocation.getBlock().getChunk().getX() + ":" + chestLocation.getBlock().getChunk().getZ();
        UUID chestUUID = UUID.nameUUIDFromBytes(coordString.getBytes(StandardCharsets.UTF_8));

        if (lastAccessed.containsKey(chestUUID) && System.currentTimeMillis() - lastAccessed.get(chestUUID) < 10000) {
            String timeLeft = String.valueOf((10000 - (System.currentTimeMillis() - lastAccessed.get(chestUUID))) / 1000);
            player.sendMessage(CommonsUtil.color("&a&lCollection Chests &7&l➥ &cYou have to wait a &f&l"+ timeLeft +"&c seconds before you can open this chest again."));
            return;
        }

        if (!isChestAvailable(chestLocation)) {
            player.sendMessage(CommonsUtil.color("&a&lCollection Chests &7&l➥ &cYou cannot open this collection chest because it is already opened by another player."));
            return;
        }

        if (FantasyCollectionChests.getInstance().getChestInventory().registerPlayerChest(player, collectionChest)) {
            // Open the collection chest inventory for the player
            FantasyCollectionChests.getInstance().getChestInventory().openInventory(player, collectionChest);
            player.sendMessage(CommonsUtil.color("&a&lCollection Chests &7&l➥ &aYou have successfully opened the collection chest."));
            lastAccessed.put(chestUUID, System.currentTimeMillis());
        } else {
            player.sendMessage(CommonsUtil.color("&a&lCollection Chests &7&l➥ &cYou cannot open this collection chest because it is already opened by another player."));
        }
    }

    private boolean isChestAvailable(Location chestLocation) {
        synchronized (lockedChests) {
            return !lockedChests.contains(chestLocation);
        }
    }

}


