package com.fantasycloud.fantasycollectionchests.struct;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CollectionChest {

    // id derived from the sql database.
    private final int id;
    private final BlockLocation location;
    private final CollectionStorage storage;

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
    }

    public void setLastSeller(Player player) {
        this.lastSellerName = player.getName();
        this.lastSellerId = player.getUniqueId();
    }

    public int getId() {
        return id;
    }

    public CollectionStorage getStorage() {
        return this.storage;
    }


    public BlockLocation getLocation() {
        return this.location;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CollectionChest)) return false;
        return ((CollectionChest) object).getId() == this.id;
    }

    @Override
    public int hashCode() {
        long hilo = this.getLocation().hashCode() ^ this.getId();
        return ((int) (hilo >> 32)) ^ (int) hilo;
    }

}
