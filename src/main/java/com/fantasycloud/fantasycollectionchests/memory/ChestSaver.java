package com.fantasycloud.fantasycollectionchests.memory;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.BlockLocation;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ChestSaver {

    private static final Pattern COLON_PATTERN = Pattern.compile(";");

    public ChestSaver() {

    }

    public CollectionChest addNewChest(Location location) {
        return this.addNewChest(new BlockLocation(location));
    }

    public CollectionChest addNewChest(BlockLocation location) {
        int id = FantasyCollectionChests.getInstance().getCollectionStorage().getDatabase().getInt("currentid");
        String toSave = id + ";" + this.fromChunk(location.getLocation().getChunk()) +
                ";" + location.toString();
        FantasyCollectionChests.getInstance().getCollectionStorage().setNoAsync(toSave, "");
        FantasyCollectionChests.getInstance().getCollectionStorage().setNoAsync("currentid", id + 1);
        return new CollectionChest(id, location);
    }

    public void saveChest(CollectionChest collectionChest) {
        this.saveChest(collectionChest, false);
    }

    public void saveChest(CollectionChest collectionChest, boolean doTask) {
        this.saveChest(collectionChest, doTask, false);
    }

    public void saveChest(CollectionChest collectionChest, boolean doTask, boolean doAsync) {
        if (doTask && !doAsync) {
            Bukkit.getScheduler().runTask(FantasyCollectionChests.getInstance(), () -> {
                FantasyCollectionChests.getInstance().getCollectionStorage().setNoAsync(this.fromChest(collectionChest), this.fromChestStorage(collectionChest));
            });
        } else if (doAsync) {
            Bukkit.getScheduler().runTask(FantasyCollectionChests.getInstance(), () -> {
                String fromChest = this.fromChest(collectionChest);
                String fromChestStorage = this.fromChestStorage(collectionChest);
                Bukkit.getScheduler().runTaskAsynchronously(FantasyCollectionChests.getInstance(), () -> {
                    FantasyCollectionChests.getInstance().getCollectionStorage().setNoAsync(fromChest, fromChestStorage);
                });
            });
        } else {
            FantasyCollectionChests.getInstance().getCollectionStorage().setNoAsync(this.fromChest(collectionChest), this.fromChestStorage(collectionChest));
        }
    }

    public void removeChest(Location location) {
        this.removeChest(new BlockLocation(location));
    }

    public void removeChest(CollectionChest collectionChest) {
        this.removeChest(this.fromChest(collectionChest));
    }

    public void removeChest(BlockLocation location) {
        this.removeChest(this.buildStringFromChunk(location.getChunk()));
    }

    public void removeChest(Chunk chunk) {
        this.removeChest(this.buildStringFromChunk(chunk));
    }

    private void removeChest(String chestString) {
        if (chestString == null) return;
        FantasyCollectionChests.getInstance().getCollectionStorage().setNoAsync(chestString, null);
    }

    @Nullable
    private String buildStringFromChunk(Chunk chunk) {
        for (String key : FantasyCollectionChests.getInstance().getCollectionStorage().getDatabase().getKeys(false)) {
            String[] results = COLON_PATTERN.split(key);
            try {
                if (results[1].equalsIgnoreCase(this.fromChunk(chunk))) {
                    return key;
                }
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
        }
        return null;
    }

    private String fromChunk(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

    private String fromChest(CollectionChest collectionChest) {
        return collectionChest.getId() + ";" + this.fromChunk(collectionChest.getLocation().getLocation().getChunk()) +
                ";" + collectionChest.getLocation().toString();
    }

    private String fromChestStorage(CollectionChest collectionChest) {
        return collectionChest.getStorage().toString();
    }

}
