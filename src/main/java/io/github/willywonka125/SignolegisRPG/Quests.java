package io.github.willywonka125.SignolegisRPG;

import java.util.logging.Level;

import io.github.willywonka125.SignolegisRPG.util.inventoryManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Quests implements CommandExecutor {
	
	Signolegis si = null;
	inventoryManager im = new inventoryManager(this);
	
	public Quests (Signolegis instance) {
		si = instance;
	}

 public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
	 if (sender.equals(si.getServer().getConsoleSender())) {
		   si.getLogger().log(Level.WARNING, "The Signolegis plugin can only be used by players.");
	 } else {
	 	sender.sendMessage("Not yet implemented");
		 //Player player = (Player) sender;
		 //player.openInventory(im.getQuestMenuInventory(player));
	 }
	 
	 return true;
 }

}
