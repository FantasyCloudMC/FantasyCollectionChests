package com.fantasycloud.fantasycollectionchests.memory;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.BlockLocation;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycollectionchests.struct.CollectionStorage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ChestFetcher {

    private static final Pattern COLON_PATTERN = Pattern.compile(";");

    public List<CollectionChest> fetchAllChests() {
        Set<String> collectionStrings = FantasyCollectionChests.getInstance().getCollectionStorage().getDatabase().getKeys(false);
        List<CollectionChest> collectionChests = new ArrayList<>();
        collectionStrings.forEach(collectionString -> {
            String[] results = COLON_PATTERN.split(collectionString);
            int id;
            try {
                id = Integer.parseInt(results[0]);
            } catch (NumberFormatException e) {
                return;
            }
            BlockLocation location = BlockLocation.from(results[2]);
            String itemData = FantasyCollectionChests.getInstance().getCollectionStorage().getDatabase().getString(collectionString);
            collectionChests.add(new CollectionChest(id, location, new CollectionStorage(id, itemData)));
        });
        return collectionChests;
    }

    public CollectionChest fetchChest(BlockLocation location) {
        return this.fetchChest(location.getLocation());
    }

    public CollectionChest fetchChest(Location location) {
        return this.fetchChest(location.getChunk());
    }

    @Nullable
    public CollectionChest fetchChest(Chunk chunk) {
        for (String string : FantasyCollectionChests.getInstance().getCollectionStorage().getDatabase().getKeys(false)) {
            if (string.startsWith("currentid")) continue;
            String[] results = COLON_PATTERN.split(string);
            int id;
            try {
                id = Integer.parseInt(results[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                continue;
            }
            BlockLocation location = BlockLocation.from(results[2]);

            String parsedResult = results[1];

            if (!parsedResult.equalsIgnoreCase(this.fromChunk(chunk))) {
                //        Bukkit.broadcastMessage("cont2");
                continue;
            }
            String itemData = FantasyCollectionChests.getInstance().getCollectionStorage().getDatabase().getString(string);

            return new CollectionChest(id, location, new CollectionStorage(id, itemData));
        }
        return null;
    }

    private String fromChunk(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

}
