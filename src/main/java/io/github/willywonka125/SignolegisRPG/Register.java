package io.github.willywonka125.SignolegisRPG;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.willywonka125.SignolegisRPG.Signolegis;
import io.github.willywonka125.SignolegisRPG.handlers.ChestHandler;
import io.github.willywonka125.SignolegisRPG.util.dataFile;

public class Register {
	
	private dataFile df = new dataFile(this); //This allows Register to use dataFile methods
	private ChestHandler ch = null;
	
	private Signolegis si = null;
	public Register (Signolegis instance) { //This allows Signolegis to access Register methods, *NOT* visa versa
		si = instance;
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
		String tmp = "";
		Set<String> sec = df.getData().getConfigurationSection("lores").getKeys(false);
		Iterator<String> it = sec.iterator();
		
		if (df.getData().getConfigurationSection("lores").getKeys(false).isEmpty()) {
			tmp = "none";
			return tmp;
		} else {
			while (it.hasNext()) {
				tmp = tmp + it.next() + ", ";
			}
			return tmp;
		}
	}
	
	//Down here deals will all the chests
	
	public String serializeLoc(Location loc){
	    return loc.getWorld().getName()+","+ (int)loc.getX() +","+ (int)loc.getY()+","+ (int)loc.getZ();
	}
	 
	public Location deserializeLoc(String str){
	    String[] loc = str.split(",");
	    return new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
	}
	
	//^Thanks to shukari for posting this googled code in a Bukkit thread :P
	
	public void registerChest(Location loc, Player player, String lore, String name) { //It's all location based!
		if (!(isChestRegistered(serializeLoc(loc)))) { //Why did I make two methods for that...
			df.getData().set("chests." + serializeLoc(loc) + ".name", name); //Set the location
			df.getData().set("chests." + serializeLoc(loc) + ".lore", lore);
			df.saveDataFile();
			player.sendMessage(ChatColor.GREEN + "Registered chest '" + name + "' at " + serializeLoc(loc));
		} else {
			player.sendMessage(ChatColor.RED + "That location/name is already registered!");
		}
	}
	
	public ItemStack getChestLore (Location loc) {
		return getLore(df.getData().getString("chests." + serializeLoc(loc) + ".lore"));
	}
	

	public boolean isChestRegistered(String loc) {
		try {
			if (df.getData().isConfigurationSection("chests." + loc)) {
				return true;
			} else return false;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public String listChests() {
		String tmp = "";
		
		try {
			Set<String> sec = df.getData().getConfigurationSection("chests").getKeys(false);
			
			for (String key : sec) {
				tmp = tmp + df.getData().getString("chests." + key + ".name") + ",";
			}
		} catch (NullPointerException e) {
			tmp = "None";
		}
		return tmp;
	}
	
}
