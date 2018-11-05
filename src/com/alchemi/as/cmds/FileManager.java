package com.alchemi.as.cmds;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.alchemi.as.AuctionStorm;

public class FileManager {

    private final AuctionStorm plugin;

    private FileConfiguration messagesConfig = null;
    private File messagesConfigFile = null;

    public FileManager(AuctionStorm plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getConfig(){
        return plugin.getConfig();
    }

    /*
     * messages.yml
     */

    public AuctionStorm getMain(){ return plugin;}

    public void saveDefaultMessagesConfig() {
        if(messagesConfigFile == null) {
            messagesConfigFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if(!messagesConfigFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public FileConfiguration getMessagesConfig() {
        if(messagesConfig == null) {
            reloadMessagesConfig();
        }
        return messagesConfig;
    }

    void reloadMessagesConfig() {
        if(messagesConfigFile == null) {
            messagesConfigFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);

        // Look for defaults in the jar
        try {
            
        	Reader defConfigStream = new InputStreamReader(plugin.getResource("messages.yml"));
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                messagesConfig.setDefaults(defConfig);
            }
        } catch(Exception e) {
            System.out.println("[MarioParty] System could not reload configuration!");
            e.printStackTrace();
        }
    }

    public void saveMessagesConfig() {
        if(messagesConfig == null || messagesConfigFile == null) {
            return;
        }
        try {
            getMessagesConfig().save(messagesConfigFile);
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + messagesConfigFile, ex);
        }
    }
    
    public void updateConfig(String file) {
        HashMap<String, Object> newConfig = getConfigVals(file);
        FileConfiguration c;
        if(file.equals("config.yml")) {
            c = plugin.getConfig();
        } else {
            c = getMessagesConfig();
        }
        for(String var : c.getKeys(false)) {
            newConfig.remove(var);
        }
        if(newConfig.size() != 0) {
            for(String key : newConfig.keySet()) {
                c.set(key, newConfig.get(key));
            }
            try {
                c.save(new File(plugin.getDataFolder(), file));
            } catch(IOException ignored) {
            }
        }
    }
    
    private HashMap<String, Object> getConfigVals(String file) {
        HashMap<String, Object> var = new HashMap<>();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringFromInputStream(AuctionStorm.class.getResourceAsStream("/" + file)));
        } catch(InvalidConfigurationException ignored) {
        }
        for(String key : config.getKeys(false)) {
            var.put(key, config.get(key));
        }
        return var;
    }
    
    @SuppressWarnings("resource")
    private String stringFromInputStream(InputStream in) {
        return new Scanner(in).useDelimiter("\\A").next();
    }
}
