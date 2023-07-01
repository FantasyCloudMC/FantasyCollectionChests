package com.fantasycloud.fantasycollectionchests.listener;

import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Inventory openInventory = player.getOpenInventory().getTopInventory();

        if (openInventory != null && openInventory.getTitle().equalsIgnoreCase(CommonsUtil.color("&a&lCollection Chest"))) {
            player.closeInventory();
            player.sendMessage(CommonsUtil.color("&a&lCollection Chests &7&l➥ &cYou cannot teleport while viewing a collection chest."));
            event.setCancelled(true);
        }
    }
}

