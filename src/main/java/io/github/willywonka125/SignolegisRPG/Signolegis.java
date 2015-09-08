package io.github.willywonka125.SignolegisRPG;


import java.util.HashSet;
import java.util.logging.Level;

import io.github.willywonka125.SignolegisRPG.handlers.ChestHandler;
import io.github.willywonka125.SignolegisRPG.util.dataFile;
import io.github.willywonka125.SignolegisRPG.util.generalUtil;
import io.github.willywonka125.SignolegisRPG.util.questMaster;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Signolegis extends JavaPlugin  {
	
	private ChestHandler ch = null;
	private dataFile df = null;
	private Register r = null;
	private Quests q = null;
	private generalUtil gu = null;
	
	
	ChatColor primary = ChatColor.AQUA;
	ChatColor secondary = ChatColor.GREEN;
	ChatColor notYet = ChatColor.DARK_RED;

public void onEnable() {
	
		getConfig().createSection("menus");
		getConfig().createSection("test");
	
		ch = new ChestHandler(this);
		df = new dataFile(this);
		r = new Register(this);
		q = new Quests(this);
		gu = new generalUtil(this);
	
	   df.saveDefaultData();
	   
	   getCommand("quests").setExecutor((CommandExecutor) q);
	   getLogger().info("SignolegisRPG is starting");
	   getServer().getPluginManager().registerEvents(ch, this);
	   getServer().getPluginManager().registerEvents(gu, this);
	   //getServer().getPluginManager().registerEvents(im, this);
	   this.saveDefaultConfig();
	   
	   if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}
	   
	   net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(questMaster.class).withName("questmaster"));
   }

   public void onDisable() {
	   this.saveConfig(); //I think this makes the plugin unable to accept config changes made while running
	   df.saveDataFile();
	   getLogger().info("Config changes made while the server is running will not be accepted.");
	   getLogger().info("However, you may do /sg reload to bypass this.");
   }
   
   public String getArgs(String[] args, int num){ //You can use a method if you want
	    StringBuilder sb = new StringBuilder(); //We make a String Builder
	    for(int i = num; i < args.length; i++) { //We get all the arguments with a for loop
	        sb.append(args[i]).append(" "); //We add the argument and the space
	    }
	    return sb.toString().trim(); //We return the message
	} //Might move this to a separate util class eventually

   
   public String[] help = {
		   primary + "/sg h,help,?" + secondary + " - Display this help menu.",
		   primary + "/sg ll,listlores" + secondary + " - List all registered lore books.",
		   primary + "/sg rl,registerlore <name>" + secondary +" - Register a lore book with [name].",
		   primary + "/sg gl,getlore <name>" + secondary + " - Retrieves a registered lore.",
		   notYet + "/sg rc,registerchest <lorename>" + secondary +" - Register the chest you are looking at."
   };
   
   
   @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
	   if (sender.equals(getServer().getConsoleSender())) {
		   getLogger().log(Level.WARNING, "The Signolegis plugin can only be used by players.");
	   } else {
		  
		   
		   Player player = (Player) sender;
		   
		   if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { //Future support for multiple pages
			   sender.sendMessage(help);
		   } else {
		   
			   if (args[0].equalsIgnoreCase("registerlore") || args[0].equalsIgnoreCase("rl")) {
				   if (args.length < 2) {
					   sender.sendMessage(ChatColor.RED + "You must specify a name!");
				   } else {
					   Player plr = (Player) sender;
					   ItemStack bk = plr.getItemInHand();
					   if (!bk.getType().equals(Material.WRITTEN_BOOK)) {
						   sender.sendMessage(ChatColor.RED + "The item in your hand is not a book!");
					   } else {
						   r.registerLore(getArgs(args, 1), bk, plr); //Now supports names with spaces!!!
					   }
				   }
				   
			   } else if (args[0].equalsIgnoreCase("ll") || args[0].equalsIgnoreCase("listlores")) {
				   sender.sendMessage(r.listLores());
			   } else if (args[0].equalsIgnoreCase("getlore") || args[0].equalsIgnoreCase("gl")) {
				   if (args.length < 2) {
					   sender.sendMessage(ChatColor.RED + "You must specify a name! (Names can have spaces :De)");
				   } else {
					   if (r.isLoreRegistered(getArgs(args, 1))) {
						   Player plr = (Player) sender;
						   plr.getInventory().addItem(r.getLore(getArgs(args, 1)));
						   plr.sendMessage(ChatColor.GREEN + "Given lore " + getArgs(args, 1));
					   }
					   
				   }
			   } else if (args[0].equalsIgnoreCase("registerchest") || args[0].equalsIgnoreCase("rc")) {
				   if (!(player.getTargetBlock((HashSet<Byte>)null, 100).getType().equals(Material.CHEST))) {
					   sender.sendMessage(ChatColor.RED + "You are not looking at a chest.");
				   } else {
					   if (args.length < 3) {
						   sender.sendMessage(ChatColor.RED + "More arguments needed. Usage: /sg rc <chestname> <lorename>");
					   } else {						   
						   r.registerChest(player.getTargetBlock((HashSet<Byte>)null, 100).getLocation(), player, getArgs(args, 2), args[1]);				  
					   }
				   }
			   } else if (args[0].equalsIgnoreCase("deletechest") || args[0].equalsIgnoreCase("dc")) {
				   if (!(player.getTargetBlock((HashSet<Byte>)null, 100).getType().equals(Material.CHEST))) {
					   sender.sendMessage(ChatColor.RED + "You are not looking at a chest.");
				   } else {
					   
				   }
			   } else if (args[0].equalsIgnoreCase("serialize")) {
				   getConfig().set("test", player.getItemInHand());
				   player.sendMessage("Block saved in config");
			   }
			   
			   
			   
			   
			   
			   else { //Put code to be executed on empty/unknown args here
				   sender.sendMessage(help);
			   }
			   
			   
			   
			   
		   }
	   }
	   
	   return true;
   }
   
   
}
