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

import java.util.*;
import java.util.stream.Collectors;

public class ChestMonitor extends BukkitRunnable {

    private final Map<Material, Deque<DepositRecord>> depositRecords = new HashMap<>();
    private final Map<Material, Map<String, Long>> depositRates = new HashMap<>();
    private long lastResetMillis;

    private static final String FIVE_MINUTES = "5m";
    private static final String FIFTEEN_MINUTES = "15m";
    private static final String THIRTY_MINUTES = "30m";
    private static final String ONE_HOUR = "1h";

    public ChestMonitor() {
        this.runTaskTimerAsynchronously(FantasyCollectionChests.getInstance(), 0L, 1200L); // Runs every minute
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        for (Material material : depositRecords.keySet()) {
            cleanRecords(material, currentTime);
            recalculateRates(material);
        }
    }

    private void cleanRecords(Material material, long currentTime) {
        Deque<DepositRecord> records = depositRecords.get(material);
        while (!records.isEmpty() && currentTime - records.peekFirst().getTime() > 3600000) { // 1 hour in milliseconds
            records.removeFirst();
        }
    }

    private void recalculateRates(Material material) {
        Deque<DepositRecord> records = depositRecords.get(material);
        Map<String, Long> rates = new HashMap<>();
        rates.put(FIVE_MINUTES, calculateRate(records, 300000)); // 5 minutes in milliseconds
        rates.put(FIFTEEN_MINUTES, calculateRate(records, 900000)); // 15 minutes in milliseconds
        rates.put(THIRTY_MINUTES, calculateRate(records, 1800000)); // 30 minutes in milliseconds
        rates.put(ONE_HOUR, calculateRate(records, 3600000)); // 1 hour in milliseconds
        depositRates.put(material, rates);
    }

    private long calculateRate(Deque<DepositRecord> records, long timePeriod) {
        return records.stream()
                .filter(record -> System.currentTimeMillis() - record.getTime() <= timePeriod)
                .mapToLong(DepositRecord::getAmount)
                .sum();
    }

    public void deposit(Material material, long amount) {
        if (!depositRecords.containsKey(material)) {
            depositRecords.put(material, new LinkedList<>());
        }
        depositRecords.get(material).addLast(new DepositRecord(System.currentTimeMillis(), amount));
    }

    public long getRate(Material material, String timePeriod) {
        if (!depositRates.containsKey(material)) {
            return 0;
        }
        return depositRates.get(material).getOrDefault(timePeriod, 0L);
    }

    private static class DepositRecord {
        private final long time;
        private final long amount;

        DepositRecord(long time, long amount) {
            this.time = time;
            this.amount = amount;
        }

        long getTime() {
            return time;
        }

        long getAmount() {
            return amount;
        }
    }

    public long getMillisUntilUpdate() {
        return 60000 - (System.currentTimeMillis() - lastResetMillis);
    }
}