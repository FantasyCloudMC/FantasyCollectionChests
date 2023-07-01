package com.fantasycloud.fantasycollectionchests.event;

import com.fantasycloud.fantasycollectionchests.struct.BlockLocation;
import com.fantasycloud.fantasycollectionchests.struct.CollectionChest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ChestSellEvent extends PlayerEvent
{
    private final CollectionChest chest;
    private final double totalValue;
    private static final HandlerList handlers;

    public ChestSellEvent(final Player player, final CollectionChest chest, final double totalValue) {
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

    public static HandlerList getHandlerList() {
        return ChestSellEvent.handlers;
    }

    public HandlerList getHandlers() {
        return ChestSellEvent.handlers;
    }

    static {
        handlers = new HandlerList();
    }
}
