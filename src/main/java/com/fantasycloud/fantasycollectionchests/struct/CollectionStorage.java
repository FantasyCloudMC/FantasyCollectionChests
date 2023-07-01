package com.fantasycloud.fantasycollectionchests.struct;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.struct.monitor.ChestMonitor;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import com.fantasycloud.fantasyenchants.FantasyEnchants;
import lombok.Getter;
import me.krizzdawg.fantasycore.FantasyCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
     //   this.monitor = new ChestMonitor();
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

    public boolean addItem(ItemStack item) {
        if (isEnchantItem(item) || isSoulGem(item) || isScrollItem(item) || isSoulPearl(item)) {
           // System.out.println(CommonsUtil.color("&cNot adding item to collection chest because it is an enchant item, soul gem, scroll, or soul pearl."));
           // System.out.println(CommonsUtil.color("&cItem: " + item));

            return false; // Return false because the item wasn't added
        }
        Material material = item.getType();
        long amount = item.getAmount();
        if (this.items.containsKey(material)) {
            this.items.put(material, this.items.get(material) + amount);
        } else {
            this.items.put(material, amount);
        }

        //this.monitor.deposit(item);
        return true; // Return true because the item was successfully added
    }





    public void addItems(List<ItemStack> items) {
        items.forEach(this::addItem);
    }

    public void removeItem(Material material) {
       // System.out.println("Test 1 Removing item: " + material);
        this.items.remove(material);
    }

    public boolean removeItem(Material material, int amount) {
        if (this.items.containsKey(material)) {
            if (this.items.get(material) == amount) {
              //  System.out.println("Test 2 Removing item: " + material + " (amount: " + amount + ")");
                this.items.remove(material);
                return true;
            } else if (this.items.get(material) > amount) {
            //    System.out.println("Test 3 Removing item: " + material + " (amount: " + amount + ")");
                this.items.put(material, this.items.get(material) - amount);
                return true;
            } else {
            //    System.out.println("Test 4 Removing item: " + material + " (amount: " + amount + ")");
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

    public void removeAll(List<ItemStack> toNotStore) {
        for (ItemStack item : toNotStore) {
            this.removeItem(item.getType(), item.getAmount());
        }
    }


    private boolean isScrollItem(ItemStack drop) {
        return FantasyEnchants.getInstance().getEnchantsAPI().isScrollItem(drop);
    }

    private boolean isSoulGem(ItemStack drop) {
        return FantasyEnchants.getInstance().getSoulHandler().isSoulGem(drop);
    }

    private boolean isEnchantItem(ItemStack drop) {
        return FantasyEnchants.getInstance().getEnchantsAPI().isEnchantItem(drop);
    }

    private boolean isSoulPearl(ItemStack drop) {
        return FantasyCore.getEnchantmentHandler().isSoulPearl(drop);
    }
}
