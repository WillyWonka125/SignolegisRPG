package io.github.willywonka125.SignolegisRPG.chests;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import io.github.willywonka125.SignolegisRPG.Signolegis;
import io.github.willywonka125.SignolegisRPG.util.DataFile;

public class ChestRegister {
	
	private final Signolegis si;
	private final DataFile df;
	
	private Map<String, RegisteredChest> registeredChests = new HashMap<>();

	public ChestRegister(Signolegis signolegis, DataFile datafile) {
		this.si = signolegis;
		this.df = datafile;
	}
	
	public void loadAll() {
		registeredChests.clear();
		
		ConfigurationSection section;
		try {
			section = df.getData().getConfigurationSection("chests");

		} catch (Exception e) {
			si.getLogger().log(Level.SEVERE, "Failed to read chests from config.", e);
			return;
		}
		if (section == null || section.getKeys(false).size() < 1) { return;}
		
		Set<String> keys = section.getKeys(false);
		for (String key : keys) {
			if (section.getConfigurationSection(key) == null) {
				si.getLogger().log(Level.WARNING, "Chest with key " + key + " could not be loaded.");
				continue;
			}
			RegisteredChest loadedChest = loadChest(key, section.getConfigurationSection(key));
			if (loadedChest != null) {
				registeredChests.put(key, loadedChest);
			} //loadChest will log errors with individual chests
		}
		si.getLogger().log(Level.INFO, "Loaded " + registeredChests.size() + " chests from file.");
	}
	
	public RegisteredChest loadChest(String key, ConfigurationSection section) {
		if (!section.isSet("name")) {
			si.getLogger().log(Level.WARNING, "Chest with key " + key + " is registered without a name.");
		}
		RegisteredChest loadedChest = new RegisteredChest(key, section.getString("name", "unnamed"));
		ConfigurationSection attributesSection = section.getConfigurationSection("attributes");
		if (attributesSection != null) {
			loadedChest.setAccessMode(parseEnum(attributesSection.getString("access.mode"), AccessMode.class, loadedChest.getAccessMode(), key, "access.mode"));
			loadedChest.setInteractionMode(parseEnum(attributesSection.getString("interaction"), InteractionMode.class, loadedChest.getInteractionMode(), key, "interaction"));
			loadedChest.setContents(parseEnum(attributesSection.getString("contents"), Contents.class, loadedChest.getContents(), key, "contents"));
			loadedChest.setRefillTrigger(parseEnum(attributesSection.getString("refill.trigger"), RefillTrigger.class, loadedChest.getRefillTrigger(), key, "refill.trigger"));
			loadedChest.setTimerResetMode(parseEnum(attributesSection.getString("refill.timer_reset_mode"), TimerResetMode.class, loadedChest.getTimerResetMode(), key, "refill.timer_reset_mode"));

			loadedChest.setProtection(attributesSection.getBoolean("protected", loadedChest.isProtected()));
			loadedChest.setGroupAccessName(attributesSection.getString("access.group", loadedChest.getGroupAccessName()));
			loadedChest.setPermissionAccessName(attributesSection.getString("access.permission", loadedChest.getPermissionAccessName()));
			loadedChest.setRefillCount(attributesSection.getInt("refill.count", loadedChest.getRefillCount()));
			loadedChest.setRefillTimer(attributesSection.getInt("refill.timer", loadedChest.getRefillTimer()));
			loadedChest.setQuestId(attributesSection.getString("refill.quest"));	

			//Warn if we have to rely on a fail-safe
			if (!attributesSection.isString("access.group") && (loadedChest.getAccessMode() == AccessMode.GROUP)) {
				si.getLogger().log(Level.WARNING, "Chest with key " + key + " is set to group access mode, but no group is specified.");
			}
			if (!attributesSection.isString("access.permission") && loadedChest.getAccessMode() == AccessMode.PERMISSION) {
				si.getLogger().log(Level.WARNING, "Chest with key " + key + " is set to permission access mode, but no permission is specified.");
			}
			if (!attributesSection.isString("refill.quest") && loadedChest.getRefillTrigger() == RefillTrigger.QUEST) {
				si.getLogger().log(Level.WARNING, "Chest with key " + key + " is set to quest refill mode, but no quest is specified.");
			}
		}
		return loadedChest;
	}

	
	public RegisteredChest getChest(String key) {
		return registeredChests.get(key);
		
	}
	
	public boolean isRegistered(String key) {
		return registeredChests.containsKey(key);
	}
	
	/**
	 * Converts a text value from data.yml into an enum constant (case-insensitive).
	 * Logs a warning naming the chest and setting if the value is not recognized.
	 * @param <E> The enum type to convert to.
	 * @param raw The text read from data.yml; null if the setting is missing.
	 * @param type The enum class, e.g. InteractionMode.class.
	 * @param fallback The value to return if raw is missing or not recognized.
	 * @param key The chest's key, used in the warning message.
	 * @param setting The setting's name, used in the warning message.
	 * @return The matching enum constant, or fallback if raw is null or not recognized.
	 */
	private <E extends Enum<E>> E parseEnum(String raw, Class<E> type, E fallback, String key, String setting) {
		if (raw == null) {
			return fallback;
		} else {
			try  {
				return Enum.valueOf(type, raw.toUpperCase());
			} catch (IllegalArgumentException e) {
				si.getLogger().warning("[" + key + "] Unknown " + setting + " '" + raw + "', using " + fallback);
				return fallback;
			}
		}
	}
}
