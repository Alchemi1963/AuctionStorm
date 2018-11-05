package NL.martijnpu.MariopartyPlugin;

import NL.martijnpu.MariopartyPlugin.enums.Category;
import NL.martijnpu.MariopartyPlugin.gameHandler.WorldData;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class FileManager {

    private final main plugin;

    private FileConfiguration messagesConfig = null;
    private File messagesConfigFile = null;

    public FileManager(main plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getConfig(){
        return plugin.getConfig();
    }

    /*
     * messages.yml
     */

    public main getMain(){ return plugin;}

    void saveDefaultMessagesConfig() {
        if(messagesConfigFile == null) {
            messagesConfigFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if(!messagesConfigFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    FileConfiguration getMessagesConfig() {
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

    void saveMessagesConfig() {
        if(messagesConfig == null || messagesConfigFile == null) {
            return;
        }
        try {
            getMessagesConfig().save(messagesConfigFile);
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + messagesConfigFile, ex);
        }
    }
}
