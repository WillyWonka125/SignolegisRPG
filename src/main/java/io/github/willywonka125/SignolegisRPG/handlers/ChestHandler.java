package io.github.willywonka125.SignolegisRPG.handlers;



import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestHandler implements Listener {

	@EventHandler
	public void onEvent(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();
		String name = inv.getName();
		
		if (name.contains("LoreChest")) {
			String lore = name.substring(name.indexOf("-"), name.length()); //Get the name of the lorebook
			//inv.addItem(arg0);
			
		}
		
		
	}
	
}
