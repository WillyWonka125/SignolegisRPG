package io.github.willywonka125.SignolegisRPG;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import io.github.willywonka125.SignolegisRPG.util.InventoryManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Quests implements CommandExecutor {
	
	Signolegis si = null;
	InventoryManager im;;
	
	ChatColor primaryColor;
	ChatColor secondaryColor;
	ChatColor notYetColor;
	
	public Quests (Signolegis instance) {
		si = instance;
		im = new InventoryManager(this);
		
		primaryColor = si.primaryColor;
		secondaryColor = si.secondaryColor;
		notYetColor = si.notYetColor;
	}
	
	private Set<UUID> inCreateMode = new HashSet<UUID>();
	
	   public String[] help = {
			   primaryColor + "/quests " + secondaryColor + " - Opens the main questing menu."
	   };

 public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
     if (!(sender instanceof Player player)) {
         sender.sendMessage("The Signolegis plugin can only be used by players.");
         return true;
     }
	 
     // Empty args, "help", "?" and unknown subcommands all fall through to default
     String sub = args.length > 0 ? args[0].toLowerCase() : "";
     
     switch (sub) {
	     case "create", "c" -> {
	    	 //First check for permission
	    	 if (!player.hasPermission("quests.create")) {
	    		 player.sendMessage(ChatColor.RED + "You do not have permission to create quests.");
	    		 return true;
	    	 }
	    	 
	    	 //Does player want to exit create mode?
	    	 //Consider making this a switch/case in the future, if needed
	    	 if (args.length > 1) {
	    		 if (args[1].equalsIgnoreCase("exit")) {
	    			 inCreateMode.remove(player.getUniqueId());
	    			 player.sendMessage(ChatColor.GREEN + "Exited Quest Creation mode.");
	    			 return true;
	    		 }
	    	 }
	    	 
	    	 // Now check if player is already in create mode
	    	 if (inCreateMode.contains(player.getUniqueId())) { 
	    		 player.sendMessage(ChatColor.RED + "You are already in Quest Creation mode. Use /quest exit to leave Quest Creation mode.");
	    		 return true;
	    	 } else {
	    		 //Add them to create mode
	    		 inCreateMode.add(player.getUniqueId());
	    		 //TODO: Implement quest creation.
	    	 }
	    	 
	     }
	     default -> player.openInventory(im.buildQuestMenu(player));
     
     }
     
	 return true;
 }

}
