package io.github.willywonka125.SignolegisRPG;

import java.io.IOException;
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
	
	private ChestHandler ch = new ChestHandler();
	private dataFile df = new dataFile();
	private Register r = new Register();
	
	public static Signolegis t;
	
	ChatColor primary = ChatColor.AQUA;
	ChatColor secondary = ChatColor.GREEN;
	
   public void onEnable() {
	   t = this;
	   
	   df.registerPlugin(this);
	   r.registerPlugin(this);
	   
	   try {
		   df.getDataFile();
	   } catch (IOException e) {
		   getLogger().log(Level.SEVERE, "Signolegis was unable to initialize the data file.", e);
		   getLogger().log(Level.SEVERE, "Plugin will now disable.");
	   }
	   
	   getLogger().info("SignolegisRPG is starting");
	   getServer().getPluginManager().registerEvents(ch, this);
	   this.saveDefaultConfig();
   }
   
   public void onDisable() {
	   this.saveConfig(); //I think this makes the plugin unable to accept config changes made while running
	   getLogger().info("Config changes made while the server is running will not be accepted.");
	   getLogger().info("However, you may do /sg reload to bypass this.");
   }

   
   public String[] help = {
		   primary + "/sg h,help,?" + secondary + " - Display this help menu.",
		   primary + "/sg ll,listlores" + secondary + " - List all registered lore books.",
		   primary + "/sg rl,registerlore <name>" + secondary +" - Register a lore book with [name].",
		   primary + "/sg gl,getlore <name>" + secondary + " - Retrieves a registered lore.",
		   primary + "/sg rc,registerchest <lorename>" + secondary +" - Register the chest you are looking at."
   };
   
   
   public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
	   if (sender.equals(getServer().getConsoleSender())) {
		   getLogger().log(Level.WARNING, "The Signolegis plugin can only be used by players.");
	   } else {
		   
		   if (args.length == 0) {
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
						   r.registerLore(args[1], bk, plr);
					   }
				   }
				   
			   } else if (args[0].equalsIgnoreCase("ll") || args[0].equalsIgnoreCase("listlores")) {
				   sender.sendMessage(df.getLoreList());
			   } else if (args[0].equalsIgnoreCase("getlore") || args[0].equalsIgnoreCase("gl")) {
				   if (args.length < 2) {
					   sender.sendMessage(ChatColor.RED + "You must specify a name!");
				   } else {
					   if (r.isLoreRegistered(args[1])) {
						   Player plr = (Player) sender;
						   plr.getInventory().addItem(r.getLore(args[1], plr));
					   }
					   
				   }
			   }
			   
			   
			   
			   
			   else { //Put code to be executed on empty args here
				   sender.sendMessage(help);
			   }
			   
			   
			   
			   
		   }
	   }
	   
	   return true;
   }
   
   
}
