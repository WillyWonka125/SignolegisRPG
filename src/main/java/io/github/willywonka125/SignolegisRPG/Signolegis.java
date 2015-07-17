package io.github.willywonka125.SignolegisRPG;


import java.util.HashSet;
import java.util.logging.Level;

import io.github.willywonka125.SignolegisRPG.handlers.ChestHandler;
import io.github.willywonka125.SignolegisRPG.util.dataFile;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Signolegis extends JavaPlugin  {
	
	private ChestHandler ch = null;
	private dataFile df = null;
	private Register r = null;
	
	
	ChatColor primary = ChatColor.AQUA;
	ChatColor secondary = ChatColor.GREEN;
	ChatColor notYet = ChatColor.DARK_RED;

public void onEnable() {
		ch = new ChestHandler(this);
		df = new dataFile(this);
		r = new Register(this);
	
	   df.saveDefaultData();
	   
	   getLogger().info("SignolegisRPG is starting");
	   getServer().getPluginManager().registerEvents(ch, this);
	   this.saveDefaultConfig();
   }
   
   public void onDisable() {
	   this.saveConfig(); //I think this makes the plugin unable to accept config changes made while running
	   df.saveDataFile();
	   getLogger().info("Config changes made while the server is running will not be accepted.");
	   getLogger().info("However, you may do /sg reload to bypass this.");
   }

   
   public String[] help = {
		   primary + "/sg h,help,?" + secondary + " - Display this help menu.",
		   primary + "/sg ll,listlores" + secondary + " - List all registered lore books.",
		   primary + "/sg rl,registerlore <name>" + secondary +" - Register a lore book with [name].",
		   primary + "/sg gl,getlore <name>" + secondary + " - Retrieves a registered lore.",
		   notYet + "/sg rc,registerchest <lorename>" + secondary +" - Register the chest you are looking at."
   };
   
   
   public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
	   if (sender.equals(getServer().getConsoleSender())) {
		   getLogger().log(Level.WARNING, "The Signolegis plugin can only be used by players.");
	   } else {
		   
			final StringBuilder bldr = new StringBuilder();
			for (int i = 1; i < args.length; i++)
			{
				if (i != 1)
				{
					bldr.append(" ");
				}
				bldr.append(args[i]);
			}
				
			final StringBuilder bldr2 = new StringBuilder();
			for (int i = 2; i < args.length; i++)
			{
				if (i != 2)
				{
					bldr.append(" ");
				}
				bldr.append(args[i]);
			}
		   
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
						   r.registerLore(bldr.toString(), bk, plr); //Now supports names with spaces!!!
					   }
				   }
				   
			   } else if (args[0].equalsIgnoreCase("ll") || args[0].equalsIgnoreCase("listlores")) {
				   sender.sendMessage(r.listLores());
			   } else if (args[0].equalsIgnoreCase("getlore") || args[0].equalsIgnoreCase("gl")) {
				   if (args.length < 2) {
					   sender.sendMessage(ChatColor.RED + "You must specify a name!");
				   } else {
					   if (r.isLoreRegistered(bldr.toString())) {
						   Player plr = (Player) sender;
						   plr.getInventory().addItem(r.getLore(bldr.toString(), plr));
					   }
					   
				   }
			   } else if (args[0].equalsIgnoreCase("registerchest") || args[0].equalsIgnoreCase("rc")) {
				   if (!(player.getTargetBlock((HashSet<Byte>)null, 100).getType().equals(Material.CHEST))) {
					   sender.sendMessage(ChatColor.RED + "You are not looking at a chest.");
				   } else {
					   r.registerChest(player.getTargetBlock((HashSet<Byte>)null, 100).getLocation(), player, bldr2.toString(), args[1]);
				   }
			   }
			   
			   
			   
			   
			   else { //Put code to be executed on empty/unknown args here
				   sender.sendMessage(help);
			   }
			   
			   
			   
			   
		   }
	   }
	   
	   return true;
   }
   
   
}
