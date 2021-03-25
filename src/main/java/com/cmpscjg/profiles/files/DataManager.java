package com.cmpscjg.profiles.files;

import com.cmpscjg.profiles.Profiles;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DataManager {

    private Profiles plugin;
    private FileConfiguration dataConfig = null;
    private File dataFile = null;

    public DataManager(Profiles plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.dataFile == null)
            this.dataFile = new File(this.plugin.getDataFolder(), "data.yml");

        this.dataConfig = YamlConfiguration.loadConfiguration(this.dataFile);

        InputStream fileStream = this.plugin.getResource("data.yml");
        if (fileStream != null) {
            YamlConfiguration defaultdataConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(fileStream));
            this.dataConfig.setDefaults(defaultdataConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.dataConfig == null)
            reloadConfig();
        return this.dataConfig;
    }

    public void saveConfig() {
        if (this.dataConfig == null || this.dataFile == null)
            return;

        try {
            this.getConfig().save(this.dataFile);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to save config to " + this.dataFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (this.dataFile == null)
            this.dataFile = new File(this.plugin.getDataFolder(), "data.yml");

        if (!this.dataFile.exists()) {
            this.plugin.saveResource("data.yml", false);
        }
    }
}
