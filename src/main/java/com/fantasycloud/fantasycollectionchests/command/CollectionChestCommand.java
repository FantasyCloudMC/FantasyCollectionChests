package com.fantasycloud.fantasycollectionchests.command;

import com.fantasycloud.fantasycollectionchests.FantasyCollectionChests;
import com.fantasycloud.fantasycollectionchests.gui.CollectionChestInventory;
import com.fantasycloud.fantasycollectionchests.item.CollectionChestItem;
import com.fantasycloud.fantasycommons.acf.BaseCommand;
import com.fantasycloud.fantasycommons.acf.annotation.CommandAlias;
import com.fantasycloud.fantasycommons.acf.annotation.CommandPermission;
import com.fantasycloud.fantasycommons.acf.annotation.Default;
import com.fantasycloud.fantasycommons.acf.annotation.Subcommand;
import com.fantasycloud.fantasycommons.util.CommonsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CommandAlias("collectionchest|collectionchests")
public class CollectionChestCommand extends BaseCommand {

    @Default
    @CommandPermission("collectionchests.collectionchest")
    public void onItemCommand(CommandSender sender, String user, int amount) {
        Player target = Bukkit.getPlayer(user);
        if (target == null) {
            sender.sendMessage(CommonsUtil.color("&c&l(!) That player is not online!"));
            return;
        }
        Map<Integer, ItemStack> items = target.getInventory().addItem(CollectionChestItem.getItem(amount));
        if (items.keySet().size() > 0) {
            int leftover = items.get(0).getAmount();
            sender.sendMessage(CommonsUtil.color("&c&l(!) &cThe player could not make room for &e" + leftover + " &ccollection chests."));
        } else {
            sender.sendMessage(CommonsUtil.color("&a&l(&f&l!&a&l) &aSuccess! " + amount + " Collection chests were added to " + target.getName() + "'s inventory!"));
        }
    }

    @Subcommand("forceupdate")
    @CommandPermission("collectionchests.forceupdate")
    public void onUpdateCommand(CommandSender sender) {
        int count = FantasyCollectionChests.getInstance().getChestMemory().getChestCache().size();
        FantasyCollectionChests.getInstance().getChestMemory().getChestCache().forEach((chunk, chest) -> {
            // ignore null cached chests.
            if (chest == null) return;
            FantasyCollectionChests.getInstance().getChestSaver().saveChest(chest);
        });
        sender.sendMessage(CommonsUtil.color("&a&l(&f!&a&l) &aSuccess! &e" + count + " &achests were updated."));
    }

    @Subcommand("reload")
    @CommandPermission("collectionchests.reload")
    public void onReloadCommand(CommandSender commandSender) {
        List<UUID> toClose = new ArrayList<>();
        CollectionChestInventory chestInventory = FantasyCollectionChests.getInstance().getChestInventory();

        chestInventory.getPlayerChests().forEach((uuid, chest) -> {
            Player player = Bukkit.getPlayer(String.valueOf(uuid));
            if (player != null) {
                player.closeInventory();
                toClose.add(uuid);
            }
        });




        toClose.forEach(uuid -> chestInventory.getPlayerChests().remove(uuid));
        FantasyCollectionChests.getInstance().getChestConfiguration().reloadConfiguration();
        commandSender.sendMessage(CommonsUtil.color("&a&l(&f!&a&l) &aConfiguration has been reloaded."));
    }



    @Subcommand("ensure")
    @CommandPermission("collectionchests.ensure")
    public void onEnsureCommand(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        FantasyCollectionChests.getInstance().getChestAssurer().assureChest(chunk);
        CommonsUtil.sendMessage(player, "&a&l(&f!&a&l) &aChest in chunk (&e" + chunk.getX() + "&a,&e" + chunk.getZ() + "&a) assured.");
    }

}
