package io.github.willywonka125.SignolegisRPG.handlers;

import io.github.willywonka125.SignolegisRPG.Register;
import io.github.willywonka125.SignolegisRPG.Signolegis;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ChestHandler implements Listener {
	
	private Register reg = new Register(this);
	private Signolegis si = null;
	
	public ChestHandler(Signolegis instance) {
		si = instance;
	}
	
	
	
	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		if (e.getInventory().getHolder() instanceof Chest || e.getInventory().getHolder() instanceof DoubleChest) {
			Chest c = (Chest) e.getInventory().getHolder();
			si.getLogger().info("Opened chest location: " + reg.serializeLoc(c.getLocation()));
			if (reg.isChestRegistered(reg.serializeLoc(c.getLocation()))) {
				if (!(e.getInventory().contains(reg.getLore(reg.getChestLore(c.getLocation()), null)))) {
					e.getInventory().addItem(reg.getLore(reg.getChestLore(c.getLocation()), null));
				}
			}
		}
	}
	
}
