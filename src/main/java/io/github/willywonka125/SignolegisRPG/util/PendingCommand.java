package io.github.willywonka125.SignolegisRPG.util;

import java.util.UUID;

public class PendingCommand {
	
	private final UUID playerUUID;
	private final long timestamp;
	private final Runnable command;
	private final String description;
	
	/**
	 * 
	 * @param uuid UUID of player running the command.
	 * @param time Timestamp (millis) PendingCommand was created
	 * @param cmd Command to execute, as a Runnable.
	 * @param desc Description of command to execute.
	 */
	public PendingCommand (UUID uuid, long time, Runnable cmd, String desc) {
		playerUUID = uuid;
		timestamp = time;
		command = cmd;
		description = desc;
	}
	
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public Runnable getCommand() {
		return command;
	}
	
	public String getDescription() {
		return description;
	}
	
}
