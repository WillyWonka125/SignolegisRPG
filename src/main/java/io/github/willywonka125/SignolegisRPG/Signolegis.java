package io.github.willywonka125.SignolegisRPG;


import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import io.github.willywonka125.SignolegisRPG.handlers.ChestHandler;
import io.github.willywonka125.SignolegisRPG.util.PendingCommand;
import io.github.willywonka125.SignolegisRPG.util.DataFile;
import io.github.willywonka125.SignolegisRPG.util.GeneralUtil;
import io.github.willywonka125.SignolegisRPG.util.QuestNPC;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Signolegis extends JavaPlugin  {
	
	private ChestHandler ch = null;
	private DataFile df = null;
	private Register r = null;
	private Quests q = null;
	private GeneralUtil gu = null;
	
	// Theme!
	ChatColor primaryColor = ChatColor.AQUA;
	ChatColor secondaryColor = ChatColor.GREEN;
	ChatColor notYetColor = ChatColor.DARK_RED;
	ChatColor errorColor = ChatColor.RED;
	
	// Pending Commands
	private final HashMap<UUID, PendingCommand> pendingCommands = new HashMap<>();
	private static final long CONFIRM_TIMEOUT_MS = 30_000;

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
		df = new DataFile(this);
		df.saveDefaultData();
		
		//Setup config.yml sections if they are not already present.
		if (!getConfig().isConfigurationSection("menus")) { getConfig().createSection("menus"); };
		if (!getConfig().isConfigurationSection("test")) { getConfig().createSection("test"); };
	
		// Costructors
		r = new Register(this, df);
		q = new Quests(this);
		gu = new GeneralUtil(this);
		ch = new ChestHandler(this);
	
		//Register Commands
		getCommand("quests").setExecutor(q);
		getLogger().info("SignolegisRPG is starting");
		getServer().getPluginManager().registerEvents(ch, this);
		getServer().getPluginManager().registerEvents(gu, this);
		//getServer().getPluginManager().registerEvents(im, this);
	   
	   //Citizens Setup
	   net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(QuestNPC.class).withName("QuestNPC"));
	   
	   getLogger().info("SignolegisRPG has started.");
   }

   public void onDisable() {
	   this.saveConfig(); //I think this makes the plugin unable to accept config changes made while running
	   df.saveDataFile();
	   getLogger().info("Config changes made while the server is running will not be accepted.");
	   getLogger().info("However, you may do /sg reload to bypass this.");
   }
   
   /**
    * Stitches together String[] elements, beginning with index num, and returns them as a space-separated string. 
    * Useful for multi-word names.
    * @param args The String[] object to combine.
    * @param num Starting index 
    * @return Space-separated, combined args.
    */
   public String getArgs(String[] args, int num){ 
	    StringBuilder sb = new StringBuilder(); //We make a String Builder
	    for(int i = num; i < args.length; i++) { //We get all the arguments with a for loop
	        sb.append(args[i]).append(" "); //We add the argument and the space
	    }
	    return sb.toString().trim(); //We return the message
	} 

   
   public String[] help = {
		   primaryColor + "/sg h,help,?" + secondaryColor + " - Display this help menu.",
		   primaryColor + "/sg ll,listlores" + secondaryColor + " - List all registered lore books.",
		   primaryColor + "/sg rl,registerlore <name>" + secondaryColor +" - Register a lore book with [name].",
		   primaryColor + "/sg gl,getlore <name>" + secondaryColor + " - Retrieves a registered lore.",
		   primaryColor + "/sg dl,deletelore <name>" + secondaryColor + " - Deletes a registered lore.",
		   primaryColor + "/sg rc,registerchest <chestname>" + secondaryColor +" - Register the chest you are looking at.",
		   primaryColor + "/sg dc,deletechest [name]" + secondaryColor + " - Delete (unregister) the chest you are looking at, or chest [name]."
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
           
           // Utilizes PendingCommand
           case "deletelore", "dl" -> {
    		   if (args.length < 2) {
                   player.sendMessage(ChatColor.RED + "You must specify a name! (Names can have spaces :D)");
                   return true;
               }
    		   
    		   String loreName = getArgs(args, 1);
    		   if (!r.isLoreRegistered(loreName)) {
    			   player.sendMessage(ChatColor.RED + "No lore registered with that name.");
                   return true;
    		   }

    		   Runnable action = () -> {
    			   player.sendMessage(r.deleteLore(loreName));
    		   };
    		   PendingCommand pcmd = pendingCommands.put(player.getUniqueId(), 
    				   new PendingCommand(player.getUniqueId(), System.currentTimeMillis(), action, "delete lore " + loreName));
    		   if (pcmd != null) {
    			   player.sendMessage(primaryColor + "Previous action " + secondaryColor + pcmd.getDescription() + primaryColor + " was replaced.");
    		   }
    		   player.sendMessage(ChatColor.RED + "You are about to delete a lore. This cannot be undone.");
    		   player.sendMessage(ChatColor.RED + "To confirm, run /sg confirm.");
    		   return true;
           }

           case "registerchest", "rc" -> {
               if (args.length < 2) {
                   player.sendMessage(ChatColor.RED + "More arguments needed. Usage: /sg rc <chestname with spaces>");
                   return true;
               }
               Block target = player.getTargetBlock(null, 100);
               player.sendMessage(r.registerChest(target, player, getArgs(args, 1)));
           }

           //Utilizes PendingCommand
           case "deletechest", "dc" -> {
        	   Runnable action;
        	   String description;
        	   
        	   //Check if a name was specified
        	   if (args.length > 1) {
        		   String name = getArgs(args, 1);
        		   List<String> keys = r.findKeysByName(name);
        		   if (keys.isEmpty()) {
        			   player.sendMessage(ChatColor.RED + "There are no chests registered with name " + name + ".");
        			   return true;
        		   } else if (keys.size() > 1) {
        			   String string = ChatColor.RED + "There are multiple chests registered with name " + name + ":\n";
        			   string += String.join("; ", keys);
        			   player.sendMessage(string);
        			   player.sendMessage(ChatColor.RED + "Look at the chest you want to delete, and run /sg dc");
        			   return true;
        		   } else {
        			   action = () -> {
        				   player.sendMessage(r.deleteChest(name, player));
        			   };
        			   description = "delete chest "+ name + " at " + keys.get(0);
        		   }
        	   } else {
        		   //No name given, instead try to delete chest player is looking at.
        		   Block target = player.getTargetBlock(null, 100);
        		   if (target.getType() != Material.CHEST && target.getType() != Material.TRAPPED_CHEST) {
        			   player.sendMessage(ChatColor.RED + "You are not looking at a chest.");
        			   return true;
        		   }
        		   if (!r.isChestRegistered(target)) {
        			   player.sendMessage(ChatColor.RED + "This chest is not registered.");
        			   return true;
        		   }
        		   // When we get to this point, we know the chest is registered.
        		   String registeredChestName = r.getChestName(target);
        		   action = () -> {
        			   player.sendMessage(r.deleteChest(target, player));
        		   };
        		   description = "delete chest " + registeredChestName;
        	   }
    		   PendingCommand pcmd = pendingCommands.put(player.getUniqueId(), 
    				   new PendingCommand(player.getUniqueId(), System.currentTimeMillis(), action, description));
    		   if (pcmd != null) {
    			   player.sendMessage(primaryColor + "Previous action " + secondaryColor + pcmd.getDescription() + primaryColor + " was replaced.");
    		   }
    		   player.sendMessage(ChatColor.RED + "You are about to delete a chest. This cannot be undone.");
    		   player.sendMessage(ChatColor.RED + "To confirm, run /sg confirm.");
    		   return true;

           }
           
           case "listchests", "lc" -> {
        	   player.sendMessage(r.listChests());
           }

           case "serialize" -> {
               getConfig().set("test", player.getInventory().getItemInMainHand());
               saveConfig();
               player.sendMessage("Block saved in config");
           }
           
           case "confirm" -> {
        	   
        	   //Get the PendingCommand and remove it in one line!.
       	    	PendingCommand pcmd = pendingCommands.remove(player.getUniqueId());
        	   
        	   //Check if a PendingCommand can be found for the player's UUID
        	   if (pcmd == null) {
        		   player.sendMessage(ChatColor.DARK_RED + "You have no pending commands.");
        		   return true;
        	   }
   
        	   //Check timestamp first
        	   if (System.currentTimeMillis() - pcmd.getTimestamp() > CONFIRM_TIMEOUT_MS) {
        		   player.sendMessage(ChatColor.DARK_RED + "Command expired. Please try again.");
        		   return true;
        	   }
        	   
        	   player.sendMessage(primaryColor + "Confirmed: " + secondaryColor + pcmd.getDescription());
        	   pcmd.getCommand().run();
        	   return true;
           }
           
           case "testcmd" -> {
        	   player.sendMessage(pendingCommands.toString());
        	   return true;
           }

           default -> player.sendMessage(help); // Future support for multiple pages
       }

       return true;
   }
   
   //Getters!
   public DataFile getDataFile() { return df; }
   public Register getRegister() { return r; }
   
}
