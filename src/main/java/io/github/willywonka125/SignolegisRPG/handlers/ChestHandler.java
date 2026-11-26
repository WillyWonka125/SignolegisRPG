package io.github.willywonka125.SignolegisRPG.handlers;

import io.github.willywonka125.SignolegisRPG.Register;
import io.github.willywonka125.SignolegisRPG.Signolegis;
import io.github.willywonka125.SignolegisRPG.util.DataFile;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
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
	 * @param instance Signolegis main class.
	 */
	public ChestHandler(Signolegis instance) {
		si = instance;
		df = si.getDataFile();
		reg = si.getRegister();
	}
	
	
	
	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		if (e.getInventory().getHolder() instanceof Chest || e.getInventory().getHolder() instanceof DoubleChest) {
			InventoryHolder holder = e.getInventory().getHolder();
			Location loc = null;
			if (holder instanceof Chest) {
				loc = ((Chest) holder).getLocation();
			} else if (holder instanceof DoubleChest) {
				loc = ((DoubleChest) holder).getLocation();
			}
			if (loc == null) { return; } // Not sure why this would be the case but fail silently if so.
			si.getLogger().info("Opened chest location: " + reg.serializeLoc(loc));
			if (reg.isChestRegistered(holder)) {
				e.getPlayer().sendMessage(ChatColor.BLUE + "You have opened a registered chest!");
				if (!(e.getInventory().contains(reg.getChestLore(loc)))) {
					if (reg.getChestLore(loc) != null){
						e.getInventory().setItem(9, reg.getChestLore(loc));
					} else if (reg.loadChestInventory(reg.serializeLoc(loc)) != null) {
						restoreChestInventory(e.getInventory(), reg.serializeLoc(loc));
					} else {
						e.getPlayer().sendMessage(ChatColor.RED + "No lore or inventory found for this chest.");
					}
				} 
			}
		}
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		e.getPlayer().sendMessage("Block broken at " + e.getBlock().getLocation());
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
