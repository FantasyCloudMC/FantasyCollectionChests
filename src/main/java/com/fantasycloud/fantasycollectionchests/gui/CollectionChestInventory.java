package com.fantasycloud.fantasycollectionchests.gui;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.configuration.ChestConfiguration;
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
import java.util.logging.Level;

public class CollectionChestInventory implements InventoryProvider {

    private Map<UUID, CollectionChest> playerChest;
    private final SmartInventory inventory;

    public CollectionChestInventory() {
        this.playerChest = new HashMap<>();
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
        if (chest == null) {
            // Handle the case when the chest is not found
            player.sendMessage("Error: Collection Chest not found!");
            player.closeInventory();
            return;
        }

        contents.fill(ClickableItem.empty(
                CommonsUtil.createItem(
                        Material.STAINED_GLASS_PANE,
                        "",
                        1,
                        7
                )
        ));

        FantasyCollectionChests plugin = FantasyCollectionChests.getInstance();
        if (plugin == null) {
            // Handle the case when the plugin instance is null
            player.sendMessage("Error: Plugin instance not found!");
            player.closeInventory();
            return;
        }

        ChestConfiguration chestConfig = plugin.getChestConfiguration();
        if (chestConfig == null) {
            // Handle the case when the chest configuration is null
            player.sendMessage("Error: Chest configuration not found!");
            player.closeInventory();
            return;
        }

        List<MaterialConfiguration> materialConfigurations = chestConfig.getMaterialConfigurations();
        if (materialConfigurations == null) {
            // Handle the case when the material configurations list is null
            player.sendMessage("Error: Material configurations not found!");
            player.closeInventory();
            return;
        }

        for (MaterialConfiguration materialConfiguration : materialConfigurations) {
            if (materialConfiguration == null) {
                // Handle the case when a material configuration is null
                player.sendMessage("Error: Material configuration not found!");
                continue;
            }

            Material material = materialConfiguration.getMaterial();
            if (material == null) {
                // Handle the case when the material is null
                player.sendMessage("Error: Material not found in material configuration!");
                continue;
            }

            int row = materialConfiguration.getRow();
            int column = materialConfiguration.getColumn();

            contents.set(
                    SlotPos.of(row, column),
                    ClickableItem.of(
                            CommonsUtil.createItem(
                                    material,
                                    chestConfig.getMaterialName()
                                            .replace("%name%", materialConfiguration.getDisplayName())
                                            .replace("%amount%", String.valueOf(chest.getStorage().getCount(material))),
                                    (int) Math.min(chest.getStorage().getCount(material), 64L),
                                    0,
                                    chestConfig.getMaterialLore()
                            ), event -> {
                                Material materialClicked = materialConfiguration.getMaterial();
                                if (materialClicked == null) {
                                    // Handle the case when the clicked material is null
                                    player.sendMessage("Error: Clicked material not found!");
                                    return;
                                }

                                if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT) {
                                    if (chest.getStorage().getCount(materialClicked) < 1) {
                                        player.sendMessage(plugin.getMessage("not-enough-items"));
                                        player.closeInventory();
                                        return;
                                    }
                                    player.sendMessage(
                                            plugin.getMessage("sold-items")
                                                    .replace("%amount%", "1")
                                                    .replace("%price%", String.valueOf(materialConfiguration.getSellPrice()))
                                    );
                                    chest.getStorage().removeItem(materialClicked, 1);
                                    double value = materialConfiguration.getSellPrice();
                                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(player, chest, value));
                                    plugin.getEconomy().depositPlayer(player, value);
                                } else if (event.getClick() == ClickType.RIGHT) {
                                    if (chest.getStorage().getCount(materialClicked) < 64) {
                                        player.sendMessage(plugin.getMessage("not-enough-items"));
                                        player.closeInventory();
                                        return;
                                    }
                                    player.sendMessage(
                                            plugin.getMessage("sold-items")
                                                    .replace("%amount%", String.valueOf(chest.getStorage().getCount(materialClicked)))
                                                    .replace("%price%", String.valueOf(materialConfiguration.getSellPrice() * 64))
                                    );
                                    double value = materialConfiguration.getSellPrice() * 64;
                                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(player, chest, value));
                                    chest.getStorage().removeItem(materialClicked, materialClicked.getMaxStackSize());
                                    plugin.getEconomy().depositPlayer(player, value);
                                } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                                    if (chest.getStorage().getCount(materialClicked) < 1) {
                                        player.sendMessage(plugin.getMessage("not-enough-items"));
                                        player.closeInventory();
                                        return;
                                    }
                                    player.sendMessage(
                                            plugin.getMessage("sold-items")
                                                    .replace("%amount%", String.valueOf(chest.getStorage().getCount(materialClicked)))
                                                    .replace("%price%", String.valueOf(materialConfiguration.getSellPrice() * chest.getStorage().getCount(materialClicked)))
                                    );
                                    double value = materialConfiguration.getSellPrice() * chest.getStorage().getCount(materialClicked);
                                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(player, chest, value));
                                    plugin.getEconomy().depositPlayer(player, value);
                                    chest.getStorage().removeItem(materialClicked);
                                }
                                player.playSound(event.getWhoClicked().getLocation(), Sound.NOTE_PLING, 5f, 1f);
                                this.init(player, contents);
                            }
                    )
            );
        }

        double sellPrice = this.calculateSellPrice(chest);
        String sellPriceString = plugin.getEconomy().format(sellPrice);
        List<String> lore = new ArrayList<>(chestConfig.getSellAllLore());
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, lore.get(i).replace("%value%", sellPriceString));
        }
        contents.set(SlotPos.of(4, 5), ClickableItem.of(
                CommonsUtil.createItem(
                        chestConfig.getSellAllMaterial(),
                        chestConfig.getSellAllName(),
                        1,
                        0,
                        lore
                ), event -> {
                    long count = chest.getStorage().calculateItemCount();
                    if (count < 1) {
                        event.getWhoClicked().sendMessage(
                                plugin.getMessage("not-enough-items")
                        );
                        return;
                    }
                    double realPrice = this.calculateSellPrice(chest);
                    String realPriceString = plugin.getEconomy().format(realPrice);
                    event.getWhoClicked().sendMessage(
                            plugin.getMessage("sold-items")
                                    .replace("%amount%", String.valueOf(count))
                                    .replace("%price%", realPriceString)
                    );
                    chest.getStorage().clearChest();
                    plugin.getChestMemory().registerCacheChest(chest);
                    Bukkit.getPluginManager().callEvent(new ChestSellEvent(player, chest, realPrice));
                    plugin.getEconomy().depositPlayer(player, realPrice);
                    event.getWhoClicked().closeInventory();
                }
        ));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        CollectionChest chest = this.playerChest.get(player.getUniqueId());
        if (chest == null) {
            // Handle the case when the chest is not found
            player.sendMessage("Error: Collection Chest not found!");
            player.closeInventory();
            return;
        }

        ItemStack chestMonitor = CommonsUtil.createItem(Material.WATCH, "&a&lChest Monitor", "&7Calculate your chest earnings.",
                "&aIron Ingot: &fx" + chest.getStorage().getMonitor().getRate(Material.IRON_INGOT, "5m") + "&7/5 minutes",
                "&aGold Ingot: &fx" + chest.getStorage().getMonitor().getRate(Material.GOLD_INGOT, "5m") + "&7/5 minutes",
                "&aDiamond: &fx" + chest.getStorage().getMonitor().getRate(Material.DIAMOND, "5m") + "&7/5 minutes",
                "",
                "&7&oUpdating in " + (int) (chest.getStorage().getMonitor().getMillisUntilUpdate() / 1000) + "s "
        );
        contents.set(SlotPos.of(4, 3), ClickableItem.empty(chestMonitor));
    }

    private double calculateSellPrice(CollectionChest chest) {
        double price = 0.0;
        for (MaterialConfiguration materialConfiguration : FantasyCollectionChests.getInstance().getChestConfiguration().getMaterialConfigurations()) {
            if (materialConfiguration == null) {
                FantasyCollectionChests.getInstance().getLogger().log(Level.WARNING, "Material configuration is null.");
                continue;
            }
            Material material = materialConfiguration.getMaterial();
            if (material == null) {
                FantasyCollectionChests.getInstance().getLogger().log(Level.WARNING, "Material is null for a material configuration.");
                continue;
            }
            long amount = chest.getStorage().getCount(material);
            price += amount * materialConfiguration.getSellPrice();
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(price));
    }

    public void openInventory(Player player, CollectionChest chest) {
        this.playerChest.put(player.getUniqueId(), chest);
        this.inventory.open(player);
    }

    public Map<UUID, CollectionChest> getPlayerChests() {
        return this.playerChest;
    }

    public boolean registerPlayerChest(Player player, CollectionChest cc) {
        for (CollectionChest chest : playerChest.values()) {
            if (chest.getLocation().equals(cc.getLocation())) {
                return false; // Another player is already in the collection chest
            }
        }

        playerChest.put(player.getUniqueId(), cc);
        return true;
    }

}
