package io.github.willywonka125.SignolegisRPG.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import io.github.willywonka125.SignolegisRPG.Signolegis;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration; 
import org.bukkit.configuration.file.YamlConfigurationOptions;

public class dataFile { //Contains methods to retrieve and alter data
	
	private Signolegis si;
	
	public void registerPlugin (Signolegis plugin) {
		si = plugin;
	}

	public File datafile;
	public FileConfiguration data;
	
	public FileConfiguration getData() {
		return data;
	}
	
	public String[] getLoreList() {
		
		String[] none = {
				ChatColor.RED + "No lore books are registered!"
		};
		
		try {
			if (!(getLoreSection() == null)) {
				return none;
			} else {
				return (String[]) getLoreSection().getKeys(false).toArray();
			}
		} catch (NullPointerException e) {
			si.getLogger().log(Level.SEVERE, "Null returned from data.yml, will now attempt to initialize file", e);
			initDataFile();
			return none;
		}
	}
	
	public ConfigurationSection getLoreSection() {
		ConfigurationSection tmp = null;
		try {
			tmp = data.getConfigurationSection("lores");
		} catch (NullPointerException e) {
			si.getLogger().log(Level.SEVERE, "Signolegis was unable to access data.yml", e);
			si.getLogger().log(Level.SEVERE, "Report this to https://github.com/WillyWonka125/SignolegisRPG/issues, or update to the latest build.");
		}
		return tmp;
	}
	
	public void initDataFile() { //More can and will be added here
		data.createSection("lores");
		data.createSection("chests");
		try {
			data.save(datafile);
			data = YamlConfiguration.loadConfiguration(datafile); //We'll put this in the try...
		} catch (IOException e) {
			si.getLogger().log(Level.SEVERE, "Unable to save data file", e);
		}
	}
	
	public void getDataFile() throws IOException {
		if (!si.getDataFolder().exists()) si.getDataFolder().mkdir();
		datafile = new File (si.getDataFolder(), "data.yml");
		if (!datafile.exists()) datafile.createNewFile();
		data = YamlConfiguration.loadConfiguration(datafile);
	}
	
}
