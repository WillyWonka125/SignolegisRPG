package io.github.willywonka125.SignolegisRPG.handlers;

import io.github.willywonka125.SignolegisRPG.Register;
import io.github.willywonka125.SignolegisRPG.Signolegis;
import io.github.willywonka125.SignolegisRPG.util.DataFile;

import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ChestHandler implements Listener {
	
	private Register reg;
	private DataFile df;
	private Signolegis si;
	/**
	 * EventHandler for chest opening and breaking.
	 * @param instance Signolegis plugin main class.
	 */
	public ChestHandler(Signolegis instance) {
		si = instance;
		df = si.getDataFile();
		reg = si.getRegister();
	}
	
	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		//Get InventoryHolder and check if this is a registered chest.
		InventoryHolder holder = e.getInventory().getHolder();
		String key = reg.findRegisteredKey(holder); //Returns null if no chest is registered, so this handles non-chest inventories implicitly.
		if (key != null) {
			// Serve the player the saved chest inventory.
			restoreChestInventory(holder.getInventory(), key);
		}
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		e.getPlayer().sendMessage("Block broken at " + e.getBlock().getLocation());
		
		// If this is a chest...
//		if (e.getBlock().getState() instanceof Chest || e.getBlock().getState() instanceof DoubleChest) {
//			//And if it's a registered chest...
//			//if ()
//		}
		
		return;
	}
	
	
	private void restoreChestInventory(Inventory target, String key) {
	    Map<Integer, ItemStack> chestInvMap = reg.loadChestInventory(key);
	    target.clear(); // replace behavior: remove whatever players left behind

	    for (Map.Entry<Integer, ItemStack> entry : chestInvMap.entrySet()) {
	        int slot = entry.getKey();
	        if (slot >= 0 && slot < target.getSize()) {
	            target.setItem(slot, entry.getValue().clone());
	        }
	    }
	}
	
}
