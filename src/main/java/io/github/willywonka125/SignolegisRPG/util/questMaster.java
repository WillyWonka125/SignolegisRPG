package io.github.willywonka125.SignolegisRPG.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

public class questMaster extends Trait {
	
	//Begin with a method to enter a player into create mode. Must be able to remember which step
	//the player is on. 
	
	public questMaster() {
		super("questMaster");
	}
	
	ChatColor gn = ChatColor.GREEN;
	ChatColor gy = ChatColor.GRAY;
	
	//Internal variables
	@Persist public boolean inCreate;
	@Persist public boolean createComplete = false;
	@Persist public int stepNumber = 0;
	@Persist public Player createPlayer;
	@Persist public String questName;
	
	//'External' variables, will be visible to player
	@Persist String dialogue1; //Init interact message
	@Persist String dialogue2; //Incomplete message
	@Persist String dialogue3; //Player complete quest message
	@Persist String dialogue4; //Post-quest interact message
	@Persist ArrayList<ItemStack> requirements;
	@Persist ArrayList<ItemStack> rewards;
	
	String[] tmp0 = {
			gn + "Please enter into chat the dialoge to say when the player firsts interacts with the NPC.",
			gn + "This can be altered in config.yml if you would like a longer dialoge."
	};
	
	String[] tmp2 = {
			gn + "Input recorded, please type 'quest requirements' into chat, holding an item to be added to the requirements list.",
			gn + "Amount, name, lore, etc. will be saved.",
			gn + "When finished, type 'quest complete' to advance to the next step."
	};
	
	String[] tmp4 = {
			gn + "Requirements list saved. Now, please type into chat the dialoge to say when the player interacts with the NPC, but the quest is not complete.",
			gn + "This can be altered in config.yml if you would like a longer dialoge.",
	};
	
	String[] tmp6 = {
			gn + "Input recorded. Now, please type into chat the dialoge to say when the player completes the quest.",
			gn + "This can be altered in config.yml if you would like a longer dialoge."
	};
	
	String[] tmp8 = {
			gn + "Almost done! Now, type into chat 'quest reward' to add the item in your hand to the rewards list.",
			gn + "As before, amount, name, lore, etc. will be saved.",
			gn + "When finished, type 'quest complete' to advance to the final step."
	};
	
	String[] tmp10 = {
			gn + "Last step! Please enter into chat the dialoge to say when a player interacts with the NPC after they have completed the quest.",
			gn + "This can be altered in config.yml if you would like a longer dialoge."
	};
	
	String[] tmp12 = {
			gn + "You're finished! By default, this quest is deactived. To activate it do /quest activate castleBakery." ,
			gn + "Alter any dialogues in config.yml before doing this."
	};
	
	String[] tmp14 = {
			gn + "Please type into chat the name for this quest."
	};

	@EventHandler
	public void onPunch (NPCLeftClickEvent e) {
		if (e.getClicker().hasPermission("quests.create")) {
			if (!createComplete) {
				if (inCreate == false) {
					inCreate = true;
					createPlayer = e.getClicker();
					createPlayer.sendMessage(ChatColor.RED + "Entered quest creation mode.");
					if (stepNumber == 0 ) createPlayer.sendMessage(tmp14);
				} else {
					e.getClicker().sendMessage(ChatColor.RED + "Create mode exited for " + e.getNPC().getName());
					inCreate = false;
				}
			}  else {
				e.getClicker().sendMessage(ChatColor.RED + "This NPC has already been set up.");
			}
		}
	}
	
	@EventHandler
	public void onChat (AsyncPlayerChatEvent e) {
		if (e.getPlayer().getUniqueId().equals(createPlayer.getUniqueId()) || inCreate == true) { //May have to switch to name checking
			if (stepNumber == 1) { //Set initial message
				dialogue1 = e.getMessage();
				e.setCancelled(true);
				stepNumber = 3;
				createPlayer.sendMessage(tmp2);
			} else if (stepNumber == 3) { //Add items to requirement list
				if (e.getMessage().contains("requirements")) {
					if (e.getPlayer().getItemInHand().getType().equals(Material.AIR)) {
						e.setCancelled(true);
						createPlayer.sendMessage(ChatColor.RED + "You are not holding anything!");
					} else {
						e.getPlayer().sendMessage(gn + "Item added to requirement list.");
						requirements.add(e.getPlayer().getItemInHand());
						e.setCancelled(true);
					}
				} else if (e.getMessage().contains("complete")) {
					e.setCancelled(true);
					stepNumber = 5;
					createPlayer.sendMessage(tmp4);
				}
			} else if (stepNumber == 5) { //Set incomplete message
				dialogue2 = e.getMessage();
				e.setCancelled(true);
				stepNumber = 7;
				createPlayer.sendMessage(tmp6);
			} else if (stepNumber == 7) { //Set completion message
				dialogue3 = e.getMessage();
				e.setCancelled(true);
				stepNumber = 9;
				createPlayer.sendMessage(tmp8);
			} else if (stepNumber == 9) {
				if (e.getMessage().contains("rewards")) {
					if (e.getPlayer().getItemInHand().getType().equals(Material.AIR)) {
						e.setCancelled(true);
						createPlayer.sendMessage(ChatColor.RED + "You are not holding anything!");
					} else {
						e.getPlayer().sendMessage(gn + "Item added to reward list.");
						rewards.add(e.getPlayer().getItemInHand());
						e.setCancelled(true);
					}
				} else if (e.getMessage().contains("complete")) {
					e.setCancelled(true);
					stepNumber = 11;
					createPlayer.sendMessage(tmp10);
				}
			} else if (stepNumber == 11) { //Set post-complete message
				dialogue4 = e.getMessage();
				e.setCancelled(true);
				stepNumber++;
				createPlayer.sendMessage(tmp12);
				createComplete = true;
			} else if (stepNumber == 0) { //Name thingy
				questName = e.getMessage();
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Name set to: " + questName);
				createPlayer.sendMessage(tmp0);
				stepNumber = 1;
			}
		}
	}
}
