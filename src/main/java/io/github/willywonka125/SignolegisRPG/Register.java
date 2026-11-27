package io.github.willywonka125.SignolegisRPG;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import io.github.willywonka125.SignolegisRPG.Signolegis;
import io.github.willywonka125.SignolegisRPG.handlers.ChestHandler;
import io.github.willywonka125.SignolegisRPG.util.DataFile;

/**
 * This class handles the setting and getting of Lore and Chests.
 */
public class Register {
	
	private DataFile df; 
	private ChestHandler ch = null;
	
	private Signolegis si = null;
	
	public Register (Signolegis instance, DataFile datafile) { //This allows Signolegis to access Register methods, *NOT* visa versa
		si = instance;
		df = datafile;
	}
	public Register(ChestHandler instance) {
		ch = instance;
	}

	public void registerLore(String name, ItemStack book, Player sender) {
		if (isLoreRegistered(name)) {
			sender.sendMessage(ChatColor.RED + "That lore name has already been registered!");
		} else {
			df.getData().set("lores." + name, book);
			df.saveDataFile();
			sender.sendMessage(ChatColor.GREEN + "Lore book registered. Do /sg getlore " + name + " to retrieve it.");
		}
	}
	
	public ItemStack getLore(String name) {
		ItemStack tmp = null;
		if (!isLoreRegistered(name)) {
			return tmp;
		} else {
			tmp = df.getData().getItemStack("lores." + name);
			return tmp;
		}
	}
	
	public boolean isLoreRegistered(String name) {
		try {
			if (df.getData().isItemStack("lores." + name)) {
				return true;
			} else return false; 
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public String listLores() {
		ConfigurationSection loresConfigSection;
		try {
			loresConfigSection = df.getData().getConfigurationSection("lores");
		} catch (Exception e) {
			si.getLogger().log(Level.SEVERE, "Failed to read lores from config.", e);
			return "An error occurred while reading lores. There may be no registered lores.";
		}
		
		if (loresConfigSection == null) {
			return "No registered lores.";
		}
		
		Set<String> keys = loresConfigSection.getKeys(false);
		if (keys.isEmpty()) {
			return "No registered lores.";
		} 
		
		return String.join(", ", keys);
	}
	
	public String deleteLore(String name) {
		if (!isLoreRegistered(name)) {
			return "No lore with this name found.";
		}
		df.getData().set("lores." + name, null);
		return ChatColor.DARK_RED + "Lore" + name + " deleted.";
	}
	
	//Down here deals will all the chests

	public void registerChest(Location loc, Player player, String lore, String name) { //It's all location based!
		if (!(isChestRegistered(serializeLoc(loc)))) { //Why did I make two methods for that...
			df.getData().set("chests." + serializeLoc(loc) + ".name", name); //Set the location
			df.getData().set("chests." + serializeLoc(loc) + ".lore", lore);
			df.saveDataFile();
			player.sendMessage(ChatColor.GREEN + "Registered chest '" + name + "' at " + serializeLoc(loc));
		} else {
			player.sendMessage(ChatColor.RED + "This chest is already registered!");
		}
	}
	
	public String registerChest(Block chest, Player player, String name) {
		if (!(chest.getState() instanceof Chest || chest.getState() instanceof DoubleChest)) {
			return "You are not looking at a chest. Material: " + chest.getBlockData().getMaterial();
		} else {
			//First register the chest
			df.getData().set("chests." + serializeLoc(chest.getLocation()) + ".name", name); //Set the location
			df.saveDataFile();
			
			// Now save its inventory
			Inventory inv = ((Chest) chest.getState()).getInventory();
			saveChestInventory(serializeLoc(chest.getLocation()), inv);
			return ChatColor.BLUE + "Registered chest and saved its inventory.";
		}
	};
	
	public String deleteChest(Block chest, Player player) {
		if (!(chest.getState() instanceof Chest || chest.getState() instanceof DoubleChest)) {
			return "You are not looking at a chest. Material: " + chest.getBlockData().getMaterial();
		} else {
			df.getData().set("chests." + serializeLoc(chest.getLocation()), null);
			df.saveDataFile();

			return ChatColor.GREEN + "Deleted chest.";
		}
	}
	
	public ItemStack getChestLore (Location loc) {
		return getLore(df.getData().getString("chests." + serializeLoc(loc) + ".lore"));
	}
	
//	public ItemStack getChestLore(InventoryHolder holder) {
//		Location registeredChestLocation = ;
//		return null;
//	}
	
	/**
	 * Checks if chest at loc is registered. 
	 * @param loc Serialized string location of the chest (world,x,y,z)
	 * @return true if loc is a registered chest location, false if no registered chest at location.
	 */
	public boolean isChestRegistered(String loc) {
		return df.getData().isConfigurationSection("chests." + loc);
	}
	
	/**
	 * Determines if a chest is registered given an InventoryHolder. This handles both single and double chests.
	 * @param holder 
	 * @return true if the chest is registered, false if no registered chest at location.
	 */
	public boolean isChestRegistered(InventoryHolder holder) {
		return findRegisteredKey(holder) != null;
	}
	
	public String listChests() {
		ConfigurationSection section;
		try {
			section = df.getData().getConfigurationSection("chests");

		} catch (Exception e) {
			si.getLogger().log(Level.SEVERE, "Failed to read chests from config.", e);
			return "An error occurred while reading chests. There may be no registered chests.";
		}
		if (section == null) { return "No registered chests."; }
		Set<String> keys = section.getKeys(false);
		if (keys.isEmpty()) {
			return "No registered chests.";
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("There are ").append(keys.size()).append(" registered chests:");
			sb.append("\nName | World | Coordinates");

			for (String key : keys) {
			    ConfigurationSection chest = section.getConfigurationSection(key);
			    String name = chest.getString("name", "unnamed");
			    Location chestLoc = deserializeLoc(key);
			    String world = chestLoc.getWorld().getName();
			    String coords = (int)chestLoc.getX() + ", " + (int)chestLoc.getY() + ", " + (int)chestLoc.getZ();
			    sb.append("\n").append(name).append(" | ").append(world).append(" | ").append(coords);
			}
			return sb.toString();
		}
	}
	
	// Utility methods
	
	/**
	 *  Determines the location a given chest is registered under (if any).
	 * @param holder
	 * @return Returns the registered key for this chest or double chest, or null if neither half is registered
	 */
	private String findRegisteredKey(InventoryHolder holder) {
	    if (holder instanceof Chest) {
	        String key = serializeLoc(((Chest) holder).getLocation());
	        return isChestRegistered(key) ? key : null;
	    }
	    if (holder instanceof DoubleChest) {
	        DoubleChest dc = (DoubleChest) holder;
	        InventoryHolder[] halves = { dc.getLeftSide(), dc.getRightSide() };
	        for (InventoryHolder half : halves) {
	            if (half instanceof Chest) {
	                String key = serializeLoc(((Chest) half).getLocation());
	                if (isChestRegistered(key)) {
	                    return key;
	                }
	            }
	        }
	    }
	    return null;
	}
	
	public String serializeLoc(Location loc){
	    return loc.getWorld().getName()+","+ (int)loc.getX() +","+ (int)loc.getY()+","+ (int)loc.getZ();
	}
	 
	public Location deserializeLoc(String str){
	    String[] loc = str.split(",");
	    return new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
	}
	
	//^Thanks to shukari for posting this googled code in a Bukkit thread :P
	
	/**
	 * Writes an Inventory to the datafile. Should be used to store chest inventories only.
	 * Note that this will override any previously-saved inventory. Use with caution.
	 * @param key Serialized Location under which the chest is registered.
	 * @param inv The Inventory object to serialize and write to data.yml
	 */
	public void saveChestInventory(String key, Inventory inv) {
		FileConfiguration data = df.getData();
		String base = "chests." + key + ".template";
		
		data.set(base + ".size", inv.getSize());
		data.set(base + ".slots", null); // clear any previous template
		
		for (int i = 0; i < inv.getSize(); i++) {
	        ItemStack item = inv.getItem(i);
	        if (item != null && item.getType() != Material.AIR) {
	            data.set(base + ".slots." + i, item.clone());
	        }
	    }
		
		df.saveDataFile();
	}
	
	// Returns a slot -> item map, so each item stays at its original index
	public Map<Integer, ItemStack> loadChestInventory(String key) {
	    Map<Integer, ItemStack> slots = new HashMap<>();
	    ConfigurationSection slotSection = df.getData()
	            .getConfigurationSection("chests." + key + ".template.slots");
	    if (slotSection == null) {
	        return slots;
	    }

	    for (String slot : slotSection.getKeys(false)) {
	        Object obj = slotSection.get(slot);
	        if (obj instanceof ItemStack) {
	            slots.put(Integer.parseInt(slot), ((ItemStack) obj).clone());
	        }
	    }
	    return slots;
	}

}
