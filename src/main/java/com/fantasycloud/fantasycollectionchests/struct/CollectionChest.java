package com.fantasycloud.fantasycollectionchests.struct;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    }

    public CollectionChest(Location location) {
        this.id = -1; // Assign a unique ID or use a placeholder value
        this.location = null; // Assign the corresponding BlockLocation
        this.storage = null; // Assign the corresponding CollectionStorage
        this.inUse = false;
        this.bukkitLocation = location;
        this.owner = null;
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
