package com.fantasycloud.fantasycollectionchests.struct;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.event.ChestSellEvent;
import com.fantasycloud.fantasycollectionchests.gui.CollectionChestInventory;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.UUID;

public class CollectionChest {

    private final int id;
    private final BlockLocation location;
    private final Location bukkitLocation;
    private final CollectionStorage storage;
    private boolean inUse;
    private UUID owner;

    private String lastSellerName;
    private UUID lastSellerId;


    public CollectionChest(int id, Location location) {
        this(id, new BlockLocation(location));
    }

    public CollectionChest(int id, BlockLocation location) {
        this(id, location, new CollectionStorage(id));
    }

    public CollectionChest(int id, Location location, CollectionStorage storage) {
        this(id, new BlockLocation(location), storage);
    }

    public CollectionChest(int id, BlockLocation location, CollectionStorage storage) {
        this.id = id;
        this.location = location;
        this.storage = storage;
        this.inUse = false;
        this.bukkitLocation = null;
        this.owner = null;

        FantasyCollectionChests plugin = FantasyCollectionChests.getInstance();
     /*   new BukkitRunnable() {
            @Override
            public void run() {
                if (storage == null || location == null || bukkitLocation == null || bukkitLocation.getBlock().getType() != Material.CHEST) {
                    cancel();
                    return;
                }

                Player lastUser = getLastUser();

                if (lastUser != null) {
                    long count = storage.calculateItemCount();
                    if (count < 1) {
                        return;
                    }
                    double realPrice = CollectionChestInventory.calculateSellPrice(CollectionChest.this);
                    String realPriceString = plugin.getEconomy().format(realPrice);
                    storage.clearChest();
                    plugin.getChestMemory().registerCacheChest(CollectionChest.this);
                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(lastUser, CollectionChest.this, realPrice));
                    plugin.getEconomy().depositPlayer(lastUser, realPrice);
                    CommonsUtil.sendMessage(lastUser, "&a&l[AUTO SELL] &a&l+ &a&n" + realPriceString);
                }


            }
        }.runTaskTimer(plugin, 20, 60 * 20);
        */
    }

    public void setLastSeller(Player player) {
        this.lastSellerName = player.getName();
        this.lastSellerId = player.getUniqueId();
    }

    public Player getLastUser() {
        if (lastSellerId != null) {
            return Bukkit.getPlayer(lastSellerId);
        }
        return null;
    }


    public int getId() {
        return id;
    }

    public CollectionStorage getStorage() {
        return storage;
    }

    public BlockLocation getLocation() {
        return location;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CollectionChest)) return false;
        CollectionChest that = (CollectionChest) object;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }
}
