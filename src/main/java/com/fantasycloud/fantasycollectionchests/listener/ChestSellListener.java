package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.event.ChestSellEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChestSellListener implements Listener {
    /*
    private final ConfigNode<Double> chestSellMultiplier;
    private final Economy economy;

    public ChestSellListener(ConfigManager configManager, Economy economy) {
        this.chestSellMultiplier = configManager.manage("chestSellMultiplier", 1.0D);
        this.economy = economy;
        FantasyCharms fantasyCharms = (FantasyCharms)JavaPlugin.getPlugin(FantasyCharms.class);
        fantasyCharms.getCharmTypeManager().register(new GenericCharm("globalCollectionChest", "Global Collection Chest", this.chestSellMultiplier));
        fantasyCharms.getFactionCharmTypeManager().register(new GenericCharm("factionCollectionChest", "Faction Collection Chest", this.chestSellMultiplier));
    }
    */

    /*
    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onHigh(ChestSellEvent event) {
        double multiplier = (Double)this.chestSellMultiplier.get() - 1.0D;
        double additional = multiplier * event.getTotalValue();
        CommonsUtil.sendMessage(event.getPlayer(), "&a&lFantasy&f&lCharms &7&l➥ &aA charm gave you an additional &f" + this.economy.format(additional) + "&a!");
        this.economy.depositPlayer(event.getPlayer(), additional);
    }

    */

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onMonitor(ChestSellEvent event) {
        event.getChest().setLastSeller(event.getPlayer());
        //FantasyCollectionChests.getInstance().getChestSaver().saveChest(event.getChest(), false, true);

    }
}