package io.github.willywonka125.SignolegisRPG.util;

import java.util.UUID;

import org.bukkit.command.Command;

public class PendingCommand {
	
	private UUID playerUUID;
	private long timestamp;
	private String commandString;
	
	public PendingCommand (UUID uuid, long time, String cmd) {
		playerUUID = uuid;
		timestamp = time;
		commandString = cmd;
	}
	
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getCommand() {
		return commandString;
	}
	
}
