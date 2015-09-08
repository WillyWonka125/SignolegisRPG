package io.github.willywonka125.SignolegisRPG.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import io.github.willywonka125.SignolegisRPG.Quests;

public class inventoryManager implements Listener {
	
	//Player stats will be saved under players.playerName.statName
	//Menus will be built from those stats to keep the size of data.yml down
	
	private dataFile df = new dataFile(this);
	
	static Quests q = null;
	public inventoryManager(Quests instance) {
		q = instance;
	}
	

	public Inventory buildQuestMenu(Player player) {
		Inventory tmp = Bukkit.createInventory(null, 54);
		
		if (!df.getData().isConfigurationSection("players." + player.getUniqueId())) {
			
			ItemStack playerInfo = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); //Build playerInfo
			SkullMeta skullmeat = (SkullMeta) playerInfo.getItemMeta();
			skullmeat.setOwner(player.getName()); 
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN + "Total XP earned: 0");
			lore.add(ChatColor.GREEN + "Available XP: 0");
			skullmeat.setLore(lore);
			skullmeat.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Total quests completed: 0");
			playerInfo.setItemMeta(skullmeat);
			tmp.setItem(4, playerInfo);
			
			
		}
		
		
		return tmp;
	}
	
	public List<String> buildPILore(Player player) { //gets playerInfo lore
		List<String> tmp = new ArrayList<String>();
		
		tmp.add(ChatColor.GREEN + "Total XP earned: " + df.getData().getInt("players."+player.getUniqueId()+".totalxp"));
		
		
		return tmp;
	}
	
}
