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
import org.bukkit.block.Block;
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
	
	// Theme!
	ChatColor primaryColor = ChatColor.AQUA;
	ChatColor secondaryColor = ChatColor.GREEN;
	ChatColor notYetColor = ChatColor.DARK_RED;
	ChatColor errorColor = ChatColor.RED;

public void onEnable() {
	
		//Check first for Citizen
		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}
		
		//Create config.yml if it nodes not already exist.
		saveDefaultConfig();
		
		//Setup the DataFile next, so constructors can access it.
		df = new dataFile(this);
		df.saveDefaultData();
		
		//Setup config.yml sections if they are not already present.
		if (!getConfig().isConfigurationSection("menus")) { getConfig().createSection("menus"); };
		if (!getConfig().isConfigurationSection("test")) { getConfig().createSection("test"); };
	
		// Costructors
		ch = new ChestHandler(this);
		r = new Register(this, df);
		q = new Quests(this);
		gu = new generalUtil(this);
	
		//Register Commands
		getCommand("quests").setExecutor(q);
		getLogger().info("SignolegisRPG is starting");
		getServer().getPluginManager().registerEvents(ch, this);
		getServer().getPluginManager().registerEvents(gu, this);
		//getServer().getPluginManager().registerEvents(im, this);
	   
	   //Citizens Setup
	   net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(questMaster.class).withName("questmaster"));
	   
	   getLogger().info("SignolegisRPG has started.");
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
		   primaryColor + "/sg h,help,?" + secondaryColor + " - Display this help menu.",
		   primaryColor + "/sg ll,listlores" + secondaryColor + " - List all registered lore books.",
		   primaryColor + "/sg rl,registerlore <name>" + secondaryColor +" - Register a lore book with [name].",
		   primaryColor + "/sg gl,getlore <name>" + secondaryColor + " - Retrieves a registered lore.",
		   notYetColor + "/sg rc,registerchest <lorename>" + secondaryColor +" - Register the chest you are looking at."
   };
   
   
   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
       if (!(sender instanceof Player player)) {
           sender.sendMessage("The Signolegis plugin can only be used by players.");
           return true;
       }

       // Empty args, "help", "?" and unknown subcommands all fall through to default
       String sub = args.length > 0 ? args[0].toLowerCase() : "";

       switch (sub) {
           case "registerlore", "rl" -> {
               if (args.length < 2) {
                   player.sendMessage(ChatColor.RED + "You must specify a name!");
                   return true;
               }
               ItemStack book = player.getInventory().getItemInMainHand();
               if (book.getType() != Material.WRITTEN_BOOK) {
                   player.sendMessage(ChatColor.RED + "The item in your hand is not a book!");
                   return true;
               }
               r.registerLore(getArgs(args, 1), book, player); // Supports names with spaces
           }

           case "listlores", "ll" -> player.sendMessage(r.listLores());

           case "getlore", "gl" -> {
               if (args.length < 2) {
                   player.sendMessage(ChatColor.RED + "You must specify a name! (Names can have spaces :D)");
                   return true;
               }
               String loreName = getArgs(args, 1);
               if (!r.isLoreRegistered(loreName)) {
                   player.sendMessage(ChatColor.RED + "No lore registered with that name.");
                   return true;
               }
               player.getInventory().addItem(r.getLore(loreName));
               player.sendMessage(ChatColor.GREEN + "Given lore " + loreName);
           }

           case "registerchest", "rc" -> {
               if (args.length < 3) {
                   player.sendMessage(ChatColor.RED + "More arguments needed. Usage: /sg rc <chestname> <lorename>");
                   return true;
               }
               Block target = player.getTargetBlock(null, 100);
               if (target.getType() != Material.CHEST) {
                   player.sendMessage(ChatColor.RED + "You are not looking at a chest.");
                   return true;
               }
               r.registerChest(target.getLocation(), player, getArgs(args, 2), args[1]);
           }

           case "deletechest", "dc" -> {
               Block target = player.getTargetBlock(null, 100);
               if (target.getType() != Material.CHEST) {
                   player.sendMessage(ChatColor.RED + "You are not looking at a chest.");
                   return true;
               }
               // TODO: delete the chest
           }

           case "serialize" -> {
               getConfig().set("test", player.getInventory().getItemInMainHand());
               saveConfig();
               player.sendMessage("Block saved in config");
           }

           default -> player.sendMessage(help); // Future support for multiple pages
       }

       return true;
   }
   
   
}
