package com.fantasycloud.fantasycollectionchests.event;

import com.fantasycloud.fantasycollectionchests.struct.BlockLocation;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ChestSellEvent extends PlayerEvent {

    private final CollectionChest chest;
    private double totalValue;

    public ChestSellEvent(Player player, CollectionChest chest, double totalValue) {
        super(player);
        this.chest = chest;
        this.totalValue = totalValue;
    }

    public BlockLocation getLocation() {
        return this.chest.getLocation();
    }

    public CollectionChest getChest() {
        return this.chest;
    }

    public double getTotalValue() {
        return this.totalValue;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return ChestSellEvent.handlers;
    }

}
