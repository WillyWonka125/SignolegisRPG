package io.github.willywonka125.SignolegisRPG;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.willywonka125.SignolegisRPG.Signolegis;
import io.github.willywonka125.SignolegisRPG.util.dataFile;

public class Register {
	
	private dataFile df = new dataFile();
	private Signolegis si;
	
	public void registerPlugin (Signolegis plugin) {
		si = plugin;
	}
	
	public void registerLore(String name, ItemStack book, Player sender) {
		if (isLoreRegistered(name)) {
			sender.sendMessage(ChatColor.RED + "That lore name has already been registered!");
		} else {
			df.getData().getConfigurationSection("lores").set(name, book);
			sender.sendMessage(ChatColor.GREEN + "Lore book registered. Do /sg getlore <name> to retrieve it.");
		}
	}
	
	public ItemStack getLore(String name, Player player) {
		ItemStack tmp = null;
		if (!isLoreRegistered(name)) {
			player.sendMessage(ChatColor.RED + "That lore can not be found!");
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
	
}
