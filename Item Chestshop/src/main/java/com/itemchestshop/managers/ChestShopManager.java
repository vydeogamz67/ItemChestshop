package com.itemchestshop.managers;

import com.itemchestshop.ItemChestShopPlugin;
import com.itemchestshop.models.ChestShop;
import com.itemchestshop.utils.DataManager;
import com.itemchestshop.utils.ItemParser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestShopManager {
    
    private final ItemChestShopPlugin plugin;
    private final Map<Location, ChestShop> chestShops;
    private final DataManager dataManager;
    
    public ChestShopManager(ItemChestShopPlugin plugin) {
        this.plugin = plugin;
        this.chestShops = new HashMap<>();
        this.dataManager = new DataManager(plugin);
        
        // Load existing shops on startup
        loadShops();
    }
    
    /**
     * Creates a chest shop from a sign
     * @param signLocation The location of the sign
     * @param owner The UUID of the shop owner
     * @param ownerName The name of the shop owner
     * @param lines The lines on the sign
     * @return ChestShop if successful, null otherwise
     */
    public ChestShop createChestShop(Location signLocation, UUID owner, String ownerName, String[] lines) {
        // Validate sign format
        if (lines.length < 3) {
            return null;
        }
        
        // Parse the sign lines
        // Line 0: Owner name (should match ownerName)
        // Line 1: Want: Amount Item
        // Line 2: Give: Amount Item
        
        String wantLine = lines[1];
        String giveLine = lines[2];
        
        // Parse want line (e.g., "Want: 64 Bread")
        if (!wantLine.toLowerCase().startsWith("want:")) {
            return null;
        }
        String wantItemString = wantLine.substring(5).trim(); // Remove "Want: "
        ItemStack wantItem = ItemParser.parseItem(wantItemString);
        if (wantItem == null) {
            return null;
        }
        
        // Parse give line (e.g., "Give: 1 Diamond")
        if (!giveLine.toLowerCase().startsWith("give:")) {
            return null;
        }
        String giveItemString = giveLine.substring(5).trim(); // Remove "Give: "
        ItemStack giveItem = ItemParser.parseItem(giveItemString);
        if (giveItem == null) {
            return null;
        }
        
        // Find the chest below the sign
        Location chestLocation = findChestBelowSign(signLocation);
        if (chestLocation == null) {
            return null;
        }
        
        // Create the chest shop
        ChestShop chestShop = new ChestShop(signLocation, chestLocation, owner, ownerName, wantItem, giveItem);
        chestShops.put(signLocation, chestShop);
        
        // Auto-save after creating a shop
        saveShops();
        
        return chestShop;
    }
    
    /**
     * Finds a chest below the sign
     * @param signLocation The location of the sign
     * @return Location of the chest or null if not found
     */
    private Location findChestBelowSign(Location signLocation) {
        // Check the block directly below the sign
        Location belowSign = signLocation.clone().subtract(0, 1, 0);
        Block belowBlock = belowSign.getBlock();
        
        if (belowBlock.getType() == Material.CHEST || belowBlock.getType() == Material.TRAPPED_CHEST) {
            return belowSign;
        }
        
        // Check one more block below
        Location twoBelow = signLocation.clone().subtract(0, 2, 0);
        Block twoBelowBlock = twoBelow.getBlock();
        
        if (twoBelowBlock.getType() == Material.CHEST || twoBelowBlock.getType() == Material.TRAPPED_CHEST) {
            return twoBelow;
        }
        
        return null;
    }
    
    /**
     * Gets a chest shop by sign location
     * @param signLocation The location of the sign
     * @return ChestShop or null if not found
     */
    public ChestShop getChestShop(Location signLocation) {
        return chestShops.get(signLocation);
    }
    
    /**
     * Removes a chest shop
     * @param signLocation The location of the sign
     * @return true if removed, false if not found
     */
    public boolean removeChestShop(Location signLocation) {
        boolean removed = chestShops.remove(signLocation) != null;
        
        // Auto-save after removing a shop
        if (removed) {
            saveShops();
        }
        
        return removed;
    }
    
    /**
     * Checks if a location has a chest shop
     * @param signLocation The location to check
     * @return true if there's a chest shop at this location
     */
    public boolean hasChestShop(Location signLocation) {
        return chestShops.containsKey(signLocation);
    }
    
    /**
     * Validates if sign lines represent a valid chest shop format
     * @param lines The sign lines
     * @return true if valid format
     */
    public boolean isValidShopFormat(String[] lines) {
        if (lines.length < 3) {
            return false;
        }
        
        // Check if line 1 starts with "Want:" and line 2 starts with "Give:"
        String wantLine = lines[1].toLowerCase();
        String giveLine = lines[2].toLowerCase();
        
        if (!wantLine.startsWith("want:") || !giveLine.startsWith("give:")) {
            return false;
        }
        
        // Try to parse the items
        String wantItemString = lines[1].substring(5).trim();
        String giveItemString = lines[2].substring(5).trim();
        
        ItemStack wantItem = ItemParser.parseItem(wantItemString);
        ItemStack giveItem = ItemParser.parseItem(giveItemString);
        
        return wantItem != null && giveItem != null;
    }
    
    /**
     * Gets the number of shops owned by a player
     * @param owner The player's UUID
     * @return Number of shops owned
     */
    public int getShopCount(UUID owner) {
        return (int) chestShops.values().stream()
                .filter(shop -> shop.getOwner().equals(owner))
                .count();
    }
    
    /**
     * Gets all chest shops
     * @return Map of all chest shops
     */
    public Map<Location, ChestShop> getAllChestShops() {
        return new HashMap<>(chestShops);
    }
    
    /**
     * Saves all chest shops to persistent storage
     */
    public void saveShops() {
        dataManager.saveShops(chestShops);
    }
    
    /**
     * Loads all chest shops from persistent storage
     */
    public void loadShops() {
        Map<Location, ChestShop> loadedShops = dataManager.loadShops();
        chestShops.clear();
        chestShops.putAll(loadedShops);
    }
}