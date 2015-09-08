package io.github.willywonka125.SignolegisRPG;

import java.util.ArrayList;
import java.util.logging.Level;

import io.github.willywonka125.SignolegisRPG.util.inventoryManager;

import org.bukkit.ChatColor;
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
	
	private ArrayList<String> inCreateMode = new ArrayList<String>();

 public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
	 if (sender.equals(si.getServer().getConsoleSender())) {
		   si.getLogger().log(Level.WARNING, "The Signolegis plugin can only be used by players.");
	 } else {
		 Player player = (Player) sender;
//		 if (!(args.length == 0) & args[0].equalsIgnoreCase("create")) {
//			 if (player.hasPermission("quests.create")) {
//				 if (inCreateMode.contains(player.getName())) {
//					 player.sendMessage(ChatColor.RED + "You are already in creation mode!");
//				 } else {
//					 
//				 }
//			 }
//		 } else {
		 player.openInventory(im.buildQuestMenu(player));
		 }
	 //}
	 
	 return true;
 }

}
