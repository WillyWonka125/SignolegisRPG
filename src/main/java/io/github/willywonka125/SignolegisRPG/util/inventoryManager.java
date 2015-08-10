package io.github.willywonka125.SignolegisRPG.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import io.github.willywonka125.SignolegisRPG.Quests;
import io.github.willywonka125.SignolegisRPG.Signolegis;

public class inventoryManager implements Listener {
	
	private Signolegis si = null;
	public inventoryManager(Signolegis instance) {
		si = instance;
	}
	
	private Quests q = null;
	public inventoryManager(Quests instance) {
		q = instance;
	}
	
	ArrayList<Inventory> inventories = null;
	
	public Inventory getQuestMenuInventory(Player player) { //A concrete method to get the Quest inventory! Integrated with config.yml
		if (si.equals(null)) {
			System.out.println("si is null so that means nothing will work fuck me");
		}
		Inventory tmp = Bukkit.createInventory(null, 54); //We begin with an initialized empty inventory
		try {
			Set<String> sec = si.getConfig().getConfigurationSection("menus.main").getKeys(false); //Key will be display name of item
			
			for (String key : sec) {
				tmp.setItem(si.getConfig().getInt("menus.main." + key + ".slot"), si.getConfig().getItemStack("menus." + key + ".item"));
			}
		} catch (NullPointerException e) { //I'll just build the default inventory in this method
			si.getLogger().log(Level.WARNING, "Error loading menu from config.yml", e);
			si.getLogger().info("Will now attempt to create default menu");
			
			ItemStack playerInfo = new ItemStack(Material.SKULL_ITEM);
			SkullMeta skullmeat = (SkullMeta) playerInfo.getItemMeta();
			skullmeat.setOwner(player.getName()); //Skullmeat, lol.....
			playerInfo.setItemMeta(skullmeat); //may have to cast ItemStack to skullmeat here
			tmp.setItem(0, playerInfo);
			si.getConfig().set("menus.main.playerInfo.slot", 0);
			si.getConfig().set("menus.main.playerInfo.item", playerInfo); //This sets the playerInfo item as a skull with the first player to run this's head
			
		}
		return tmp;
	}
	
	public Inventory addItem(Inventory inv, ItemStack item, int row, int slot) {
		inv.setItem(row*9 + slot, item);
		return inv;
	}
	
	public ItemMeta setMeta (String name, List<String> lore, ItemMeta meta) {
		meta.setDisplayName(name);
		List<String> tmp = new ArrayList<String>(); 
		meta.setLore(lore);
		
		return meta;
	}
	
	@EventHandler
	public void onQuestMenuClick(InventoryClickEvent event) {
		
	}
	
}
