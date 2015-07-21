package io.github.willywonka125.SignolegisRPG.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import io.github.willywonka125.SignolegisRPG.Register;
import io.github.willywonka125.SignolegisRPG.Signolegis;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration; 

public class dataFile { //Contains methods to retrieve and alter data
	
	static Signolegis si = null;
	static Register reg = null;
	
	public dataFile (Signolegis instance) {
		si = instance;
	}
	
	public dataFile (Register instance) {
		reg = instance;
	}


	public File datafile = null;
	public FileConfiguration data = null;
	
	public void reloadDataFile() {
	    if (datafile == null) {
	    	datafile = new File(si.getDataFolder(), "data.yml");
	    }
	    data = YamlConfiguration.loadConfiguration(datafile);
	 
	    // Look for defaults in the jar
	    Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(si.getResource("data.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			si.getLogger().log(Level.SEVERE, "Error while reloading data.yml", e);
		}
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        data.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getData() {
	    if (data == null) {
	        reloadDataFile();
	    }
	    return data;
	}
	
	public void saveDataFile() {
	    if (data == null || datafile == null) {
	        return;
	    }
	    try {
	        getData().save(datafile);
	    } catch (IOException ex) {
	        si.getLogger().log(Level.SEVERE, "Could not save config to " + datafile, ex);
	    }
	}
	
	public void saveDefaultData() {
	    if (datafile == null) {
	        datafile = new File(si.getDataFolder(), "data.yml");
	    }
	    if (!datafile.exists()) {            
	         si.saveResource("data.yml", false);
	     }
	}
	
}
