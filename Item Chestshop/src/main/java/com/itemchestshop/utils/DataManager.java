package com.itemchestshop.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.itemchestshop.ItemChestShopPlugin;
import com.itemchestshop.models.ChestShop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {
    
    private final ItemChestShopPlugin plugin;
    private final File dataFile;
    private final Gson gson;
    
    public DataManager(ItemChestShopPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "shops.json");
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .setPrettyPrinting()
                .create();
        
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }
    
    /**
     * Saves all chest shops to the data file
     * @param chestShops Map of chest shops to save
     */
    public void saveShops(Map<Location, ChestShop> chestShops) {
        try {
            // Convert to serializable format
            Map<String, SerializableChestShop> serializableShops = new HashMap<>();
            
            for (Map.Entry<Location, ChestShop> entry : chestShops.entrySet()) {
                String locationKey = locationToString(entry.getKey());
                SerializableChestShop serializableShop = new SerializableChestShop(entry.getValue());
                serializableShops.put(locationKey, serializableShop);
            }
            
            // Write to file
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(serializableShops, writer);
            }
            
            plugin.getLogger().info("Saved " + chestShops.size() + " chest shops to " + dataFile.getName());
            
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save chest shops", e);
        }
    }
    
    /**
     * Loads all chest shops from the data file
     * @return Map of loaded chest shops
     */
    public Map<Location, ChestShop> loadShops() {
        Map<Location, ChestShop> chestShops = new HashMap<>();
        
        if (!dataFile.exists()) {
            plugin.getLogger().info("No existing shop data found, starting fresh");
            return chestShops;
        }
        
        try {
            // Read from file
            try (FileReader reader = new FileReader(dataFile)) {
                Type type = new TypeToken<Map<String, SerializableChestShop>>(){}.getType();
                Map<String, SerializableChestShop> serializableShops = gson.fromJson(reader, type);
                
                if (serializableShops != null) {
                    // Convert back to ChestShop objects
                    for (Map.Entry<String, SerializableChestShop> entry : serializableShops.entrySet()) {
                        try {
                            Location location = stringToLocation(entry.getKey());
                            ChestShop chestShop = entry.getValue().toChestShop();
                            
                            if (location != null && chestShop != null) {
                                chestShops.put(location, chestShop);
                            }
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.WARNING, "Failed to load shop at " + entry.getKey(), e);
                        }
                    }
                }
            }
            
            plugin.getLogger().info("Loaded " + chestShops.size() + " chest shops from " + dataFile.getName());
            
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load chest shops", e);
        }
        
        return chestShops;
    }
    
    /**
     * Converts a Location to a string representation
     */
    private String locationToString(Location location) {
        return location.getWorld().getName() + "," + 
               location.getX() + "," + 
               location.getY() + "," + 
               location.getZ() + "," + 
               location.getYaw() + "," + 
               location.getPitch();
    }
    
    /**
     * Converts a string representation back to a Location
     */
    private Location stringToLocation(String locationString) {
        try {
            String[] parts = locationString.split(",");
            if (parts.length != 6) {
                return null;
            }
            
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) {
                return null;
            }
            
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            
            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Serializable version of ChestShop for JSON storage
     */
    private static class SerializableChestShop {
        private final String signLocation;
        private final String chestLocation;
        private final String owner;
        private final String ownerName;
        private final ItemStack wantItem;
        private final ItemStack giveItem;
        
        public SerializableChestShop(ChestShop chestShop) {
            this.signLocation = locationToString(chestShop.getSignLocation());
            this.chestLocation = locationToString(chestShop.getChestLocation());
            this.owner = chestShop.getOwner().toString();
            this.ownerName = chestShop.getOwnerName();
            this.wantItem = chestShop.getWantItem();
            this.giveItem = chestShop.getGiveItem();
        }
        
        public ChestShop toChestShop() {
            Location signLoc = stringToLocation(this.signLocation);
            Location chestLoc = stringToLocation(this.chestLocation);
            UUID ownerUUID = UUID.fromString(this.owner);
            
            if (signLoc == null || chestLoc == null) {
                return null;
            }
            
            return new ChestShop(signLoc, chestLoc, ownerUUID, ownerName, wantItem, giveItem);
        }
        
        private static String locationToString(Location location) {
            return location.getWorld().getName() + "," + 
                   location.getX() + "," + 
                   location.getY() + "," + 
                   location.getZ() + "," + 
                   location.getYaw() + "," + 
                   location.getPitch();
        }
        
        private static Location stringToLocation(String locationString) {
            try {
                String[] parts = locationString.split(",");
                if (parts.length != 6) {
                    return null;
                }
                
                World world = Bukkit.getWorld(parts[0]);
                if (world == null) {
                    return null;
                }
                
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                float yaw = Float.parseFloat(parts[4]);
                float pitch = Float.parseFloat(parts[5]);
                
                return new Location(world, x, y, z, yaw, pitch);
            } catch (Exception e) {
                return null;
            }
        }
    }
    
    /**
     * Custom adapter for Location serialization
     */
    private static class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
        @Override
        public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("world", location.getWorld().getName());
            obj.addProperty("x", location.getX());
            obj.addProperty("y", location.getY());
            obj.addProperty("z", location.getZ());
            obj.addProperty("yaw", location.getYaw());
            obj.addProperty("pitch", location.getPitch());
            return obj;
        }
        
        @Override
        public Location deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            JsonObject obj = json.getAsJsonObject();
            World world = Bukkit.getWorld(obj.get("world").getAsString());
            if (world == null) return null;
            
            return new Location(
                world,
                obj.get("x").getAsDouble(),
                obj.get("y").getAsDouble(),
                obj.get("z").getAsDouble(),
                obj.get("yaw").getAsFloat(),
                obj.get("pitch").getAsFloat()
            );
        }
    }
    
    /**
     * Custom adapter for ItemStack serialization
     */
    private static class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
        @Override
        public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", itemStack.getType().name());
            obj.addProperty("amount", itemStack.getAmount());
            
            if (itemStack.hasItemMeta()) {
                obj.addProperty("meta", itemStack.getItemMeta().toString());
            }
            
            return obj;
        }
        
        @Override
        public ItemStack deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            JsonObject obj = json.getAsJsonObject();
            
            try {
                org.bukkit.Material material = org.bukkit.Material.valueOf(obj.get("type").getAsString());
                int amount = obj.get("amount").getAsInt();
                
                ItemStack itemStack = new ItemStack(material, amount);
                
                // Note: For simplicity, we're not deserializing meta data here
                // This could be enhanced to support custom names, lore, enchantments, etc.
                
                return itemStack;
            } catch (Exception e) {
                return null;
            }
        }
    }
}