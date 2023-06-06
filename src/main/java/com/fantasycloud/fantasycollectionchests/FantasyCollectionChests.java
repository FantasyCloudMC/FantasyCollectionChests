package com.fantasycloud.fantasycollectionchests;

import com.fantasycloud.fantasycollectionchests.command.CollectionChestCommand;
import com.fantasycloud.fantasycollectionchests.configuration.ChestConfiguration;
import com.fantasycloud.fantasycollectionchests.gui.CollectionChestInventory;
import com.fantasycloud.fantasycollectionchests.listener.*;
import com.fantasycloud.fantasycollectionchests.memory.ChestAssurer;
import com.fantasycloud.fantasycollectionchests.memory.ChestFetcher;
import com.fantasycloud.fantasycollectionchests.memory.ChestMemory;
import com.fantasycloud.fantasycollectionchests.memory.ChestSaver;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.fantasycloud.fantasycommons.acf.PaperCommandManager;
//import com.fantasycloud.fantasycommons.analytics.AnalyticManager;
import com.fantasycloud.fantasycommons.configuration.DynamicConfigurationManager;
import com.fantasycloud.fantasycommons.configuration.api.ConfigManager;
import com.fantasycloud.fantasycommons.inventory.InventoryManager;
import com.fantasycloud.fantasycommons.storage.DatabaseManager;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class FantasyCollectionChests extends JavaPlugin {

    private static FantasyCollectionChests instance;

    public FantasyCollectionChests() {
        FantasyCollectionChests.instance = this;
    }

    //private AnalyticManager analyticManager;
    private DatabaseManager collectionStorage;
    private ConfigManager configManager;

    private ChestConfiguration chestConfiguration;

    private Economy economy;

    private PaperCommandManager commandManager;
    private InventoryManager inventoryManager;

    private ChestFetcher chestFetcher;
    private ChestMemory chestMemory;
    private ChestSaver chestSaver;
    private ChestAssurer chestAssurer;

    private CollectionChestInventory chestInventory;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        //this.analyticManager = AnalyticManager.get(this);

        this.collectionStorage = new DatabaseManager(this, "collectionstorage");

        this.configManager = DynamicConfigurationManager.get(this);
        
        this.chestConfiguration = new ChestConfiguration();

        this.setupEconomy();

        this.commandManager = new PaperCommandManager(this);
        this.commandManager.registerCommand(new CollectionChestCommand());

        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();

        this.chestFetcher = new ChestFetcher();
        this.chestMemory = new ChestMemory();
        this.chestSaver = new ChestSaver();
        this.chestAssurer = new ChestAssurer();

        this.chestInventory = new CollectionChestInventory();

        Arrays.asList(
                new AntiHopperListener(),
                new MobDeathListener(),
                new BlockPlaceListener(),
                new PlayerInteractListener(),
                new BlockBreakListener(),
                new ChestSellListener(this.configManager, this.economy),
                new BlockExplodeListener(),
                new FarmListener()
        ).forEach(this::registerListener);

        this.chestAssurer.assureAllChests();
    }

    @Override
    public void onDisable() {
        this.getChestMemory().getChestCache().entrySet().iterator().forEachRemaining(entry -> {
            CollectionChest chest = entry.getValue();
            // ignore null caches.
            if (chest == null) return;
            this.getChestSaver().saveChest(chest, false, false);
        });
    }

    public static FantasyCollectionChests getInstance() {
        return instance;
    }

    public String getMessage(String message) {
        return CommonsUtil.color(this.getConfig().getString("messages." + message));
    }

   /* public AnalyticManager getAnalyticManager() {
        return this.analyticManager;
    }*/

    public DatabaseManager getCollectionStorage() {
        return this.collectionStorage;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public PaperCommandManager getCommandManager() {
        return this.commandManager;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public ChestConfiguration getChestConfiguration() {
        return this.chestConfiguration;
    }

    public void setChestConfiguration(ChestConfiguration chestConfiguration) {
        this.chestConfiguration = chestConfiguration;
    }

    public ChestFetcher getChestFetcher() {
        return this.chestFetcher;
    }

    public ChestMemory getChestMemory() {
        return this.chestMemory;
    }

    public ChestSaver getChestSaver() {
        return this.chestSaver;
    }

    public ChestAssurer getChestAssurer() {
        return this.chestAssurer;
    }

    public CollectionChestInventory getChestInventory() {
        return this.chestInventory;
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        this.economy = rsp.getProvider();
        return this.economy != null;
    }

}
