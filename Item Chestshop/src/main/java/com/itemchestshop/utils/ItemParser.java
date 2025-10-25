package com.itemchestshop.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemParser {
    
    /**
     * Parses a string like "Diamond 1" or "Bread 64" into an ItemStack
     * @param itemString The string to parse (e.g., "Diamond 1", "Oak_Log 32")
     * @return ItemStack or null if parsing fails
     */
    public static ItemStack parseItem(String itemString) {
        if (itemString == null || itemString.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = itemString.trim().split("\\s+");
        if (parts.length != 2) {
            return null;
        }
        
        String materialName = parts[0].toUpperCase();
        String amountString = parts[1];
        
        // Try to parse the amount
        int amount;
        try {
            amount = Integer.parseInt(amountString);
            if (amount <= 0 || amount > 64) {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        
        // Try to get the material
        Material material = getMaterialFromString(materialName);
        if (material == null || material == Material.AIR) {
            return null;
        }
        
        return new ItemStack(material, amount);
    }
    
    /**
     * Gets a Material from a string, handling common variations
     * @param materialName The material name to parse
     * @return Material or null if not found
     */
    private static Material getMaterialFromString(String materialName) {
        // First try direct match
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try with common variations
            String normalized = normalizeItemName(materialName);
            try {
                return Material.valueOf(normalized);
            } catch (IllegalArgumentException e2) {
                return null;
            }
        }
    }
    
    /**
     * Normalizes item names to match Minecraft material names
     * @param itemName The item name to normalize
     * @return Normalized material name
     */
    private static String normalizeItemName(String itemName) {
        String normalized = itemName.toUpperCase().replace(" ", "_");
        
        // Handle common aliases
        switch (normalized) {
            case "WOOD":
            case "LOG":
                return "OAK_LOG";
            case "STONE":
                return "COBBLESTONE";
            case "DIRT":
                return "DIRT";
            case "GRASS":
                return "GRASS_BLOCK";
            case "PLANK":
            case "PLANKS":
                return "OAK_PLANKS";
            case "STICK":
                return "STICK";
            case "COAL":
                return "COAL";
            case "IRON":
                return "IRON_INGOT";
            case "GOLD":
                return "GOLD_INGOT";
            case "DIAMOND":
                return "DIAMOND";
            case "EMERALD":
                return "EMERALD";
            case "BREAD":
                return "BREAD";
            case "WHEAT":
                return "WHEAT";
            case "APPLE":
                return "APPLE";
            case "BEEF":
                return "BEEF";
            case "PORK":
                return "PORKCHOP";
            case "CHICKEN":
                return "CHICKEN";
            case "LEATHER":
                return "LEATHER";
            case "WOOL":
                return "WHITE_WOOL";
            default:
                return normalized;
        }
    }
    
    /**
     * Converts an ItemStack to a readable string format
     * @param item The ItemStack to convert
     * @return String representation (e.g., "Diamond 1")
     */
    public static String itemToString(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "Air 0";
        }
        
        String materialName = item.getType().name().toLowerCase();
        // Convert DIAMOND to Diamond, OAK_LOG to Oak_Log, etc.
        materialName = capitalizeWords(materialName.replace("_", " "));
        
        return materialName + " " + item.getAmount();
    }
    
    /**
     * Capitalizes the first letter of each word
     * @param str The string to capitalize
     * @return Capitalized string
     */
    private static String capitalizeWords(String str) {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            if (words[i].length() > 0) {
                result.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    result.append(words[i].substring(1).toLowerCase());
                }
            }
        }
        
        return result.toString();
    }
}