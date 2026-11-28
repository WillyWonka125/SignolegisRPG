package io.github.willywonka125.SignolegisRPG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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
			return ChatColor.DARK_RED + "An error occurred while reading lores. Please see console log.";
		}
		
		if (loresConfigSection == null) {
			return ChatColor.RED + "No registered lores.";
		}
		
		Set<String> keys = loresConfigSection.getKeys(false);
		if (keys.isEmpty()) {
			return ChatColor.RED + "No registered lores.";
		} 
		
		return String.join(", ", keys);
	}
	
	public String deleteLore(String name) {
		if (!isLoreRegistered(name)) {
			return ChatColor.RED + "No lore with this name found.";
		}
		df.getData().set("lores." + name, null);
		df.saveDataFile();
		return ChatColor.GREEN + "Lore " + name + " deleted.";
	}
	
	//Down here deals will all the chests
	
	public String registerChest(Block chest, Player player, String name) {
		if (!(chest.getState() instanceof Chest || chest.getState() instanceof DoubleChest)) {
			return "You are not looking at a chest. Material: " + chest.getBlockData().getMaterial();
		}
		if (isChestRegistered(chest)) {
			return "This chest is already registered as " + getChestName(chest);
		}
		
		//First register the chest
		String key = serializeLoc(chest.getLocation());
		df.getData().set("chests." + key + ".name", name); //Set the Name
		df.saveDataFile();

		// Now save its inventory
		Inventory inv = ((Chest) chest.getState()).getInventory();
		saveChestInventory(key, inv);
		return ChatColor.GREEN + "Registered chest and saved its inventory.";
		
	};
	
	public String deleteChest(Block chest, Player player) {
		if (!(chest.getState() instanceof Chest || chest.getState() instanceof DoubleChest)) {
			return "You are not looking at a chest. Material: " + chest.getBlockData().getMaterial();
		}
		if (!isChestRegistered(chest)) {
			return "This chest is not registered.";
		}
		
		df.getData().set("chests." + findRegisteredKey(chest), null);
		df.saveDataFile();

		return ChatColor.GREEN + "Deleted chest.";

	}
	
	public String deleteChest(String name, Player player) {
		List<String> keys = findKeysByName(name);
		if (keys.isEmpty()) {
			return ChatColor.RED + "There are no chests registered with this name.";
		}
		if (keys.size() > 1) {
			String string = ChatColor.RED + "There are multiple chests registered under this name (see below). Look at the chest to delete it.\n";
			string += String.join("; ", keys);
			return string;
		}
		//There is exactly 1 chest with this name! Delete it.
		df.getData().set("chests." + keys.get(0), null);
		df.saveDataFile();
		return ChatColor.GREEN + "Deleted chest " + name;
	}
	
	/**
	 * Gets the chest's registered name.
	 * @param chest
	 * @return The name of the chest, or null if the Block is not a chest or is not registered.
	 */
	public String getChestName (Block chest) {
		if (!(isChestRegistered(chest))) {
			return null;
		}
		return df.getData().getString("chests." + findRegisteredKey(chest) + ".name");
	}
	
	/**
	 * Determines if a chest is registered given an InventoryHolder. This handles both single and double chests.
	 * @param holder 
	 * @return true if the chest is registered, false if no registered chest at location.
	 */
	public boolean isChestRegistered(InventoryHolder holder) {
		return findRegisteredKey(holder) != null;
	}
	
	/**
	 * Determines if a chest is registered given a Block. This handles both single and double chests.
	 * @param block 
	 * @return true if the chest is registered, false if no registered chest at location.
	 */
	public boolean isChestRegistered (Block block) {
		return findRegisteredKey(block) != null;
	}
	
	/**
	 * Determines if a chest is registered under the given name.
	 * @param name Name of chest to check (case-insensitive)
	 * @return true if the chest name is registered at least once, false if no chest is registered under that name.
	 */
	public boolean isChestRegistered (String name) {
		return !findKeysByName(name).isEmpty();
	}
	
	public String listChests() {
		ConfigurationSection section;
		try {
			section = df.getData().getConfigurationSection("chests");

		} catch (Exception e) {
			si.getLogger().log(Level.SEVERE, "Failed to read chests from config.", e);
			return ChatColor.DARK_RED + "An error occurred while reading chests. Check console log.";
		}
		if (section == null) { 
			return ChatColor.RED + "No registered chests."; 
		}
		Set<String> keys = section.getKeys(false);
		if (keys.isEmpty()) {
			return ChatColor.RED + "No registered chests.";
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("There are ").append(keys.size()).append(" registered chests:");
			sb.append("\nName | World | Coordinates");

			for (String key : keys) {
			    ConfigurationSection chest = section.getConfigurationSection(key);
			    String name = chest.getString("name", "unnamed");
			    String[] coords = key.split(",");
			    sb.append("\n").append(name).append(" | ").append(coords[0]).append(" | ")
			    .append(coords[1]).append(", ")
			    .append(coords[2]).append(", ")
			    .append(coords[3]);
			}
			return sb.toString();
		}
	}
	
	// Utility methods
	
	/**
	 * Checks if a chest at loc is registered. 
	 * @param loc Serialized string location of the chest (world,x,y,z)
	 * @return true if loc is a registered chest location, false if no registered chest at location.
	 */
	private boolean isKeyRegistered(String loc) {
		return df.getData().isConfigurationSection("chests." + loc);
	}
	
	/**
	 *  Determines the location a given chest is registered under (if any).
	 * @param holder
	 * @return Returns the registered key for this chest or double chest, or null if neither half of the chest is registered
	 */
	public String findRegisteredKey(InventoryHolder holder) {
	    if (holder instanceof Chest) {
	        String key = serializeLoc(((Chest) holder).getLocation());
	        return isKeyRegistered(key) ? key : null;
	    }
	    if (holder instanceof DoubleChest) {
	        DoubleChest dc = (DoubleChest) holder;
	        InventoryHolder[] halves = { dc.getLeftSide(), dc.getRightSide() };
	        for (InventoryHolder half : halves) {
	            if (half instanceof Chest) {
	                String key = serializeLoc(((Chest) half).getLocation());
	                if (isKeyRegistered(key)) {
	                    return key;
	                }
	            }
	        }
	    }
	    return null;
	}
	
	/**
	 *  Determines the location a given chest is registered under (if any).
	 * @param block
	 * @return Returns the registered key for this block, or null if there is no chest or double chest registered at this block's location.
	 */
	public String findRegisteredKey(Block block) {
		
		// Is block null?
		if (block == null) { return null; }
		
		//Is block even a chest?
		Material type = block.getType();
		if (type != Material.CHEST && type != Material.TRAPPED_CHEST) {
			return null;
		}
		
		//Finally, check BlockState.
		BlockState state = block.getState();
		if (!(state instanceof Chest)) {
			return null;
		}
		
		return findRegisteredKey (((Chest) state).getInventory().getHolder());
		
	}
	
	/**
	 * Attempts to find a chest's registered key given its registered name (case insensitive).
	 * @param name The name associated with the chest.
	 * @return The keys associated with the name, or an empty List if no matches.
	 */
	public List<String> findKeysByName(String name) {
		
		List<String> matchedKeys = new ArrayList<>();
		
		// Loop through all the chests and check each of their name values.
		//Get the chests section.
		ConfigurationSection section;
		try {
			section = df.getData().getConfigurationSection("chests");

		} catch (Exception e) {
			si.getLogger().log(Level.SEVERE, "Failed to read chests from config.", e);
			return matchedKeys;
		}
		if (section == null) { 
			return matchedKeys; 
		}
		//We have the chests configuration section. Get the keys and iterate through them.
		Set<String> keys = section.getKeys(false);

		for (String key : keys) {
			String n = section.getString(key + ".name");
			if (name.equalsIgnoreCase(n)) {
				matchedKeys.add(key);
			}
		}
		return matchedKeys;
	}
	
	public String serializeLoc(Location loc){
	    return loc.getWorld().getName()+","+ loc.getBlockX() +","+ loc.getBlockY()+","+ loc.getBlockZ();
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
