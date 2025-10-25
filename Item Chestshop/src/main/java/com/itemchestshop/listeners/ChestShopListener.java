package com.itemchestshop.listeners;

import com.itemchestshop.ItemChestShopPlugin;
import com.itemchestshop.managers.ChestShopManager;
import com.itemchestshop.models.ChestShop;
import com.itemchestshop.utils.ConfigManager;
import com.itemchestshop.utils.ItemParser;
import com.itemchestshop.utils.TransactionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestShopListener implements Listener {
    
    private final ItemChestShopPlugin plugin;
    private final ChestShopManager chestShopManager;
    private final ConfigManager configManager;
    
    public ChestShopListener(ItemChestShopPlugin plugin) {
        this.plugin = plugin;
        this.chestShopManager = plugin.getChestShopManager();
        this.configManager = plugin.getConfigManager();
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !isSign(clickedBlock.getType())) {
            return;
        }
        
        Player player = event.getPlayer();
        ChestShop chestShop = chestShopManager.getChestShop(clickedBlock.getLocation());
        
        if (chestShop == null) {
            return;
        }
        
        // Check permissions
        if (!player.hasPermission("itemchestshop.use")) {
            player.sendMessage(Component.text("You don't have permission to use shops!", NamedTextColor.RED));
            return;
        }
        
        // Cancel the event to prevent other interactions
        event.setCancelled(true);
        
        // Show shop info if player is sneaking
        if (player.isSneaking()) {
            showShopInfo(player, chestShop);
            return;
        }
        
        // Process the transaction
        TransactionManager.TransactionResult result = TransactionManager.processTransaction(player, chestShop);
        
        if (result.isSuccess()) {
            // Send success message to buyer
            player.sendMessage(Component.text("Transaction successful!", NamedTextColor.GREEN));
            
            // Notify shop owner if they're online
            Player owner = Bukkit.getPlayer(chestShop.getOwner());
            if (owner != null && owner.isOnline()) {
                owner.sendMessage(Component.text("Someone bought from your shop!", NamedTextColor.GOLD));
            }
        } else {
            // Send error message
            player.sendMessage(Component.text("Transaction failed!", NamedTextColor.RED));
        }
    }
    
    /**
     * Shows information about the chest shop
     * @param player The player to show info to
     * @param chestShop The chest shop
     */
    private void showShopInfo(Player player, ChestShop chestShop) {
        player.sendMessage(Component.text("=== Chest Shop Info ===", NamedTextColor.GOLD));
        player.sendMessage(Component.text("Owner: ", NamedTextColor.YELLOW)
            .append(Component.text(chestShop.getOwnerName(), NamedTextColor.WHITE)));
        player.sendMessage(Component.text("Selling: ", NamedTextColor.YELLOW)
            .append(Component.text(ItemParser.itemToString(chestShop.getGiveItem()), NamedTextColor.WHITE)));
        player.sendMessage(Component.text("Price: ", NamedTextColor.YELLOW)
            .append(Component.text(ItemParser.itemToString(chestShop.getWantItem()), NamedTextColor.WHITE)));
        
        // Show stock status
        if (chestShop.hasStock()) {
            player.sendMessage(Component.text("In Stock", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Out of Stock", NamedTextColor.RED));
        }
        
        // Show if shop can accept payment
        if (chestShop.canAcceptPayment()) {
            player.sendMessage(Component.text("Accepting Payments", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Shop Full - Cannot Accept Payment", NamedTextColor.RED));
        }
        
        player.sendMessage(Component.text("Right-click to buy, Shift+Right-click for info", NamedTextColor.GRAY));
    }
    
    /**
     * Checks if a material is a sign
     * @param material The material to check
     * @return true if it's a sign
     */
    private boolean isSign(Material material) {
        return material.name().contains("SIGN");
    }
}