package com.fantasycloud.fantasycollectionchests.struct.monitor;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycollectionchests.struct.CollectionStorage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.mockito.internal.util.collections.IdentitySet;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChestMonitor extends BukkitRunnable {

    private CollectionStorage storage;
    @Getter
    private Map<Material, Long> deposits = Maps.newHashMap();
    private Map<Material, List<Long>> perMinValues = Maps.newHashMap();
    private long lastResetMillis;

    public ChestMonitor(CollectionStorage storage) {
        this.storage = storage;
        this.runTaskTimer(FantasyCollectionChests.getInstance(), 0, 60 * 20);
    }

    @Override
    public void run() {
        for (Material material : deposits.keySet()) {
            this.addToPerMin(material, deposits.getOrDefault(material, 0L));
        }
        deposits.clear();
        this.lastResetMillis = System.currentTimeMillis();
    }

    public void addToPerMin(Material material, long minuteAmount) {
        List<Long> amounts = perMinValues.getOrDefault(material, Lists.newArrayList());
        amounts.add(0, minuteAmount);

        if (amounts.size() >= 5) {
            amounts.remove(4);
        }

        perMinValues.put(material, amounts);
    }

    public long getPerMinAvg(Material material) {
        if (!perMinValues.containsKey(material)) {
            return 0;
        }
        return perMinValues.get(material).stream().collect(Collectors.summingLong(Long::longValue)) / perMinValues.get(material).size();
    }

    public void deposit(Material material, long amount) {
        long current = deposits.getOrDefault(material, 0L);
        deposits.put(material, current + amount);
    }

    public long getMillisUntilUpdate() {
        return 60000 - (System.currentTimeMillis() - lastResetMillis);
    }
}
