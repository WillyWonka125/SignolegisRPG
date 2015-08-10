package io.github.willywonka125.SignolegisRPG.util;

import io.github.willywonka125.SignolegisRPG.Signolegis;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class generalUtil implements Listener {
	
	Signolegis si = null;
	
	public generalUtil(Signolegis signolegis) {
		si = signolegis;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.getMessage().contains("staff")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "Asking for staff gets you nowhere.");
		} if (e.getMessage().contains("MineChat")) {
			e.setMessage("I'm using a chat app. Hate me.");
		}
	}
	
	
}
