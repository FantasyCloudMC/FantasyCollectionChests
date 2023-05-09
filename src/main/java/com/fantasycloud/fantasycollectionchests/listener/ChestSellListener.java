package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.charms.AbstractCharm;
import com.fantasycloud.charms.FantasyCharms;
import com.fantasycloud.charms.GenericCharm;
import com.fantasycloud.charms.factions.FactionCharm;
import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.event.ChestSellEvent;
//import com.fantasycloud.fantasycommons.analytics.AnalyticManager;
//import com.fantasycloud.fantasycommons.analytics.AnalyticPoint;
import com.fantasycloud.fantasycommons.configuration.api.ConfigManager;
import com.fantasycloud.fantasycommons.configuration.api.ConfigNode;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestSellListener implements Listener {

    private final ConfigNode<Double> chestSellMultiplier;
    private final Economy economy;
    
    //private final AnalyticManager.ManagedAnalytic<Double> analytic;

    public ChestSellListener(ConfigManager configManager, Economy economy) {
        this.chestSellMultiplier = configManager.manage("chestSellMultiplier", 1.0);
        this.economy = economy;
        FantasyCharms fantasyCharms =  JavaPlugin.getPlugin(FantasyCharms.class);
        fantasyCharms.getCharmTypeManager().register(new GenericCharm("globalCollectionChest", "Global Collection Chest", this.chestSellMultiplier));
        fantasyCharms.getFactionCharmTypeManager().register(new GenericCharm("factionCollectionChest", "Faction Collection Chest", this.chestSellMultiplier));
        /*this.analytic = FantasyCollectionChests.getInstance().getAnalyticManager().createManagedAnalytic(
                points -> {
                    double val = points.stream().mapToDouble(Double::doubleValue).sum();
                    return FantasyCollectionChests.getInstance().getAnalyticManager().createPoint("sales")
                            .addField("money", val);
                },
                60 * 60 * 20,
                true
        );*/
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHigh(ChestSellEvent event) {
        double multiplier = this.chestSellMultiplier.get() - 1;
        double additional = multiplier * event.getTotalValue();
        CommonsUtil.sendMessage((LivingEntity)event.getPlayer(), "&a&lFantasy&f&lCharms &7&l➥ &aA charm gave you an additional &f" + this.economy.format(additional) + "&a!");
        this.economy.depositPlayer(event.getPlayer(), additional);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMonitor(ChestSellEvent event) {
        FantasyCollectionChests.getInstance().getChestSaver().saveChest(event.getChest(), false, true);
    }

    /*@EventHandler
    public void onAnalytic(ChestSellEvent event) {
        this.analytic.submit(event.getTotalValue());
    }*/

}
