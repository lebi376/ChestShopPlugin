package leb0wn.chestshop;

import leb0wn.chestshop.data.PlayerShopData;
import leb0wn.chestshop.events.DeleteShop;
import leb0wn.chestshop.events.InteractEvent;
import leb0wn.chestshop.events.ShopProtect;
import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestShop extends JavaPlugin {

    private static ChestShop instance;
    private static PlayerShopData _userData;

    public static ChestShop getInstance() {
        return instance;
    }
    public static PlayerShopData getUserData() { return _userData.}

    @Override
    public void onEnable() {
        // Initialize
        getDataFolder().mkdir();
        // commands aanmaken
        // custom items
        instance = this;
        //

        // Ready
        getLogger().info(ChatColor.GREEN + "Chestshop >> Started Succesfully!");
        getServer().getPluginManager().registerEvents(new InteractEvent(), this);
        getServer().getPluginManager().registerEvents(new DeleteShop(), this);
        getServer().getPluginManager().registerEvents(new ShopProtect(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getInstance().saveConfig();
    }
}
