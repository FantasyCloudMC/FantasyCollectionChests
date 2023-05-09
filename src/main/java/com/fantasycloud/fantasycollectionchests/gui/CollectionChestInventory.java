package com.fantasycloud.fantasycollectionchests.gui;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.event.ChestSellEvent;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycollectionchests.struct.MaterialConfiguration;
import com.fantasycloud.fantasycollectionchests.struct.monitor.ChestMonitor;
import com.fantasycloud.fantasycommons.inventory.ClickableItem;
import com.fantasycloud.fantasycommons.inventory.InventoryListener;
import com.fantasycloud.fantasycommons.inventory.SmartInventory;
import com.fantasycloud.fantasycommons.inventory.content.InventoryContents;
import com.fantasycloud.fantasycommons.inventory.content.InventoryProvider;
import com.fantasycloud.fantasycommons.inventory.content.SlotPos;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import com.fantasycloud.fantasycommons.util.TimeUtil;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CollectionChestInventory implements InventoryProvider {

    private Map<UUID, CollectionChest> playerChest;
    private final SmartInventory inventory;

    public CollectionChestInventory() {
        this.playerChest = ExpiringMap.builder()
                .expiration(1, TimeUnit.MINUTES)
                .expirationListener((ExpirationListener<UUID, CollectionChest>) (uuid, collectionChest) -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player.isOnline()) {
                        Inventory topInventory = player.getOpenInventory().getTopInventory();
                        if (topInventory != null && topInventory.getTitle().equalsIgnoreCase("Collection Chest Contents")) {
                            player.closeInventory();
                        }
                    } else {
                        this.playerChest.remove(uuid);
                    }
                }).build();
        this.inventory = SmartInventory.builder()
                .title("Collection Chest Contents")
                .type(InventoryType.CHEST)
                .manager(FantasyCollectionChests.getInstance().getInventoryManager())
                .id("collection-contents")
                .size(6, 9)
                .provider(this)
                .listener(new InventoryListener<>(InventoryCloseEvent.class, event -> {
                    this.playerChest.remove(event.getPlayer().getUniqueId());
                }))
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        CollectionChest chest = this.playerChest.get(player.getUniqueId());
        contents.fill(ClickableItem.empty(
                        CommonsUtil.createItem(
                                Material.STAINED_GLASS_PANE,
                                "",
                                1,
                                7
                        )
                )
        );

        FantasyCollectionChests.getInstance().getChestConfiguration().getMaterialConfigurations().forEach(materialConfiguration -> {
            contents.set(
                    SlotPos.of(materialConfiguration.getRow(), materialConfiguration.getColumn()),
                    ClickableItem.of(
                            CommonsUtil.createItem(
                                    materialConfiguration.getMaterial(),
                                    FantasyCollectionChests.getInstance().getChestConfiguration().getMaterialName()
                                            .replace("%name%", materialConfiguration.getDisplayName())
                                            .replace("%amount%", String.valueOf(chest.getStorage().getCount(materialConfiguration.getMaterial()))),
                                    (int) Math.min(chest.getStorage().getCount(materialConfiguration.getMaterial()), 64L),
                                    0,
                                    FantasyCollectionChests.getInstance().getChestConfiguration().getMaterialLore()
                            ), event -> {
                                Material material = materialConfiguration.getMaterial();
                                if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) {
                                    if (chest.getStorage().getCount(material) < 1) {
                                        player.sendMessage(FantasyCollectionChests.getInstance().getMessage("not-enough-items"));
                                        player.closeInventory();
                                        return;
                                    }
                                    player.sendMessage(
                                            FantasyCollectionChests.getInstance().getMessage("sold-items")
                                                    .replace("%amount%", "1")
                                                    .replace("%price%", String.valueOf(materialConfiguration.getSellPrice()))
                                    );
                                    chest.getStorage().removeItem(material, 1);
                                    double value = materialConfiguration.getSellPrice();
                                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(player, chest, value));
                                    FantasyCollectionChests.getInstance().getEconomy().depositPlayer(player, value);
                                } else if (event.getClick() == ClickType.RIGHT) {
                                    if (chest.getStorage().getCount(material) < 64) {
                                        player.sendMessage(FantasyCollectionChests.getInstance().getMessage("not-enough-items"));
                                        player.closeInventory();
                                        return;
                                    }
                                    player.sendMessage(
                                            FantasyCollectionChests.getInstance().getMessage("sold-items")
                                                    .replace("%amount%", String.valueOf(chest.getStorage().getCount(material)))
                                                    .replace("%price%", String.valueOf(materialConfiguration.getSellPrice() * 64))
                                    );
                                    double value = materialConfiguration.getSellPrice() * 64;
                                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(player, chest, value));
                                    chest.getStorage().removeItem(material, material.getMaxStackSize());
                                    FantasyCollectionChests.getInstance().getEconomy().depositPlayer(player, value);
                                } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                                    if (chest.getStorage().getCount(material) < 1) {
                                        player.sendMessage(FantasyCollectionChests.getInstance().getMessage("not-enough-items"));
                                        player.closeInventory();
                                        return;
                                    }
                                    player.sendMessage(
                                            FantasyCollectionChests.getInstance().getMessage("sold-items")
                                                    .replace("%amount%", String.valueOf(chest.getStorage().getCount(material)))
                                                    .replace("%price%", String.valueOf(materialConfiguration.getSellPrice() * chest.getStorage().getCount(material)))
                                    );
                                    double value = materialConfiguration.getSellPrice() * chest.getStorage().getCount(material);
                                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(player, chest, value));
                                    FantasyCollectionChests.getInstance().getEconomy().depositPlayer(player, value);
                                    chest.getStorage().removeItem(material);
                                }
                                player.playSound(event.getWhoClicked().getLocation(), Sound.NOTE_PLING, 5f, 1f);
                                this.init(player, contents);
                            }
                    )
            );
        });
        double sellPrice = this.calculateSellPrice(chest);
        String sellPriceString = FantasyCollectionChests.getInstance().getEconomy().format(sellPrice);
        List<String> lore = new ArrayList<>(FantasyCollectionChests.getInstance().getChestConfiguration().getSellAllLore());
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, lore.get(i).replace("%value%", sellPriceString));
        }
        contents.set(SlotPos.of(4, 5), ClickableItem.of(
                CommonsUtil.createItem(
                        FantasyCollectionChests.getInstance().getChestConfiguration().getSellAllMaterial(),
                        FantasyCollectionChests.getInstance().getChestConfiguration().getSellAllName(),
                        1,
                        0,
                        lore
                ), event -> {
                    long count = chest.getStorage().calculateItemCount();
                    if (count < 1) {
                        event.getWhoClicked().sendMessage(
                                FantasyCollectionChests.getInstance().getMessage("not-enough-items")
                        );
                        return;
                    }
                    double realPrice = this.calculateSellPrice(chest);
                    String realPriceString = FantasyCollectionChests.getInstance().getEconomy().format(realPrice);
                    event.getWhoClicked().sendMessage(
                            FantasyCollectionChests.getInstance().getMessage("sold-items")
                                    .replace("%amount%", String.valueOf(count))
                                    .replace("%price%", realPriceString)
                    );
                    chest.getStorage().clearChest();
                    FantasyCollectionChests.getInstance().getChestMemory().registerCacheChest(chest);
                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(player, chest, realPrice));
                    FantasyCollectionChests.getInstance().getEconomy().depositPlayer(player, realPrice);
                    event.getWhoClicked().closeInventory();
                }
        ));

    }

    @Override
    public void update(Player player, InventoryContents contents) {
        CollectionChest chest = this.playerChest.get(player.getUniqueId());

        ItemStack chestMonitor = CommonsUtil.createItem(Material.WATCH, "&a&lChest Monitor", "&7Calculate your chest earnings.",
                "&aIron Ingot: &fx" + chest.getStorage().getMonitor().getPerMinAvg(Material.IRON_INGOT) + "&7/min",
                "&7&oUpdating in " + (int) (chest.getStorage().getMonitor().getMillisUntilUpdate() / 1000) + "s ");
        contents.set(SlotPos.of(4, 3), ClickableItem.empty(chestMonitor));
    }

    private double calculateSellPrice(CollectionChest chest) {
        double price = 0.0;
        for (MaterialConfiguration materialConfiguration : FantasyCollectionChests.getInstance().getChestConfiguration().getMaterialConfigurations()) {
            long amount = chest.getStorage().getCount(materialConfiguration.getMaterial());
            price += amount * materialConfiguration.getSellPrice();
        }
        return price;
    }

    /**
     * @return if that chest was already registered.
     */
    public boolean registerPlayerChest(Player player, CollectionChest chest) {
        if (this.getPlayerChests().values().contains(chest)) return true;
        this.playerChest.put(player.getUniqueId(), chest);
        return false;
    }

    public Map<UUID, CollectionChest> getPlayerChests() {
        return this.playerChest;
    }

    public SmartInventory getInventory() {
        return this.inventory;
    }

}
