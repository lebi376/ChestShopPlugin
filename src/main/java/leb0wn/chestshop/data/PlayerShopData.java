package leb0wn.chestshop.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerShopData {
    private final Plugin _plugin;
    private final File _file;
    private final String _fileName;
    private final Logger _logger;
    private FileConfiguration _dataConfig = null;

    public PlayerShopData(Plugin plugin, String dataFile) {
        _plugin = plugin;
        _logger = _plugin.getLogger();
        _fileName = dataFile;
        _file = new File(_plugin.getDataFolder(), _fileName);

        saveDefaultConfig();
    }

    public void reloadConfig() {
        _dataConfig = YamlConfiguration.loadConfiguration(_file);

        InputStream defaultStream = _plugin.getResource(_fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            _dataConfig.setDefaults(defaultConfig);
        }

        _logger.info("Loaded configuration from " + _fileName);
    }

    public FileConfiguration getConfig() {
        if (_dataConfig == null)
            reloadConfig();

        return _dataConfig;
    }

    public void saveConfig() throws IOException {
        if (_dataConfig == null || _file == null) {
            return;
        }

        try {
            getConfig().save(_file);

        } catch (IOException e) {
            _logger.log(Level.SEVERE, "Could not save config to " + _file, e);
        }
    }

    public void saveDefaultConfig() {
        if (!_file.exists()) {
            _plugin.saveResource(_fileName, false);
        }
    }
}