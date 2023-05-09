package com.fantasycloud.fantasycollectionchests.struct;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.monitor.ChestMonitor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CollectionStorage {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final Pattern COLON_PATTERN = Pattern.compile(":");

    private final int id;
    private final EnumMap<Material, Long> items;
    @Getter
    private ChestMonitor monitor;

    public CollectionStorage(int id) {
        this(id, "");
    }

    public CollectionStorage(int id, String itemData) {
        this.id = id;
        this.items = this.getItemsFromString(itemData);
        this.monitor = new ChestMonitor(this);
    }

    public long calculateItemCount() {
        long count = 0;
        for (Map.Entry<Material, Long> entry : this.items.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    public long getCount(Material material) {
        return this.items.getOrDefault(material, 0L);
    }

    public void addItem(Material material, long amount) {
        if (this.items.containsKey(material)) {
            this.items.put(material, this.items.get(material) + amount);
        } else {
            this.items.put(material, amount);
        }

        monitor.deposit(material, amount);
    }

    public void addItem(ItemStack item) {
        this.addItem(item.getType(), item.getAmount());
    }

    public void addItems(List<ItemStack> items) {
        items.forEach(this::addItem);
    }

    public void removeItem(Material material) {
        this.items.remove(material);
    }

    /**
     * @return whether the map actually had that many items.
     */
    public boolean removeItem(Material material, int amount) {
        if (this.items.containsKey(material)) {
            if (this.items.get(material) == amount) {
                this.items.remove(material);
                return true;
            } else if (this.items.get(material) > amount) {
                this.items.put(material, this.items.get(material) - amount);
                return true;
            } else {
                this.items.remove(material);
                return false;
            }
        }
        return false;
    }

    public void clearChest() {
        this.items.clear();
    }

    private EnumMap<Material, Long> getItemsFromString(String string) {
        EnumMap<Material, Long> map = new EnumMap<>(Material.class);
        if (string.equalsIgnoreCase("")) return map;
        String[] split = COMMA_PATTERN.split(string);
        for (String splitString : split) {
            String[] materialAmountArray = COLON_PATTERN.split(splitString);
            Material material = Material.getMaterial(materialAmountArray[0]);
            long amount = Long.parseLong(materialAmountArray[1]);
            map.put(material, amount);
        }
        return map;
    }

    @Override
    public String toString() {
        if (this.items.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        this.items.forEach((material, integer) -> {
            builder.append(material.name());
            builder.append(":");
            builder.append(integer);
            builder.append(",");
        });
        // remove last redundant commma.
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public Map<Material, Long> getItems() {
        return this.items;
    }

    public ChestMonitor getMonitor() {
        return monitor;
    }
}
