package com.fantasycloud.fantasycollectionchests.memory;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.BlockLocation;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycollectionchests.struct.SimpleChunk;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChestMemory {

    private final Map<Chunk, CollectionChest> chestCache;

    public ChestMemory() {
        this.chestCache = ExpiringMap.builder()
                .expiration(5, TimeUnit.MINUTES)
                .expirationPolicy(ExpirationPolicy.CREATED)
                .asyncExpirationListener(
                        (ExpirationListener<Chunk, CollectionChest>) (chunk, collectionChest) -> {
                            if (collectionChest == null) return; // prevent null-filled entry for npe
                            FantasyCollectionChests.getInstance().getChestSaver().saveChest(collectionChest, false, true);
                })
                .build();
    }

    public Map<Chunk, CollectionChest> getChestCache() {
        return this.chestCache;
    }

    public CollectionChest getChest(BlockLocation location) {
        return this.getChest(location.getLocation());
    }

    public CollectionChest getChest(Location location) {
        return this.getChest(location.getChunk());
    }

    @Nullable
    public CollectionChest getChest(Chunk chunk) {
        if (this.chestCache.containsKey(chunk)) {
            return this.chestCache.get(chunk);
        }
        CollectionChest fetched = FantasyCollectionChests.getInstance().getChestFetcher().fetchChest(chunk);
        this.registerCacheChest(chunk, fetched); // can be null, fill map with null value to prevent repetitive queries. (TEMPORARILY DISABLED)
        return fetched;
    }

    public void registerCacheChest(Chunk chunk, CollectionChest collectionChest) {
        if(collectionChest == null) return; // In attempt to fix collection chests resetting, disabling null-values.
        this.getChestCache().put(chunk, collectionChest);
    }

    public void registerCacheChest(Location location, CollectionChest collectionChest) {
        this.registerCacheChest(location.getChunk(), collectionChest);
    }

    public void registerCacheChest(CollectionChest collectionChest) {
        this.registerCacheChest(collectionChest.getLocation().getLocation(), collectionChest);
    }

    public void forceCacheClear() {
        this.chestCache.clear();
    }

    public void removeLocation(Chunk chunk) {
        this.getChestCache().remove(chunk);
    }

    public void removeLocation(Location location) {
        this.removeLocation(location.getChunk());
    }

    public boolean hasCachedChunk(Chunk chunk) {
        return this.chestCache.containsKey(chunk);
    }


}
