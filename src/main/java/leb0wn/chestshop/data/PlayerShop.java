package leb0wn.chestshop.data;

import leb0wn.chestshop.ChestShop;
import leb0wn.chestshop.utils.InventoryUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerShop {
    private JavaPlugin _plugin = null;
    private FileConfiguration _config = null;

    // Config properties
    public String key;
    public ItemStack item;
    public int stackSize;
    public int buyPrice;
    public int sellPrice;
    public boolean canBuy;
    public boolean canSell;
    public String owner;

    // Calculated properties
    public Location location;
    public Chest chest;

    public PlayerShop() {
        _plugin = ChestShop.getPlugin(ChestShop.class);
        _config = _plugin.getConfig();
    }

    public static String createKey(Location location) {
        return String.format("%1$s_%2$d_%3$d_%4$d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    public static PlayerShop getShopByLocation(Location location) {
        Plugin plugin = ChestShop.getPlugin(ChestShop.class);
        FileConfiguration config = plugin.getConfig();
        Logger logger = plugin.getLogger();

        if (location == null) {
            //logger.info("location is null");
            return null;
        }

        String shopKey = createKey(location);
        //logger.info("getShopByLocation(" + location + "): shopKey = " + shopKey);

        if (!config.contains(shopKey)) {
            //logger.info("Shop not found with key '" + shopKey + "'");
            return null;
        }

        PlayerShop shop = new PlayerShop();
        shop.key = shopKey;
        shop.item = config.getItemStack(shop.key + ".item");

        //logger.info("shop.item = " + shop.item + ", config.get(item) = " + config.get(shop.key + ".item"));

        shop.stackSize = config.getInt(shop.key + ".amount");
        shop.buyPrice = config.getInt(shop.key + ".price");
        shop.sellPrice = config.getInt(shop.key + ".sellPrice");
        shop.canBuy = config.getBoolean(shop.key + ".bought");
        shop.canSell = config.getBoolean(shop.key + ".sell");
        shop.owner = config.getString(shop.key + ".owner");
        shop.location = location;
        shop.chest = (Chest)shop.location.getBlock().getState();

        //logger.info("Shop loaded from config!");

        return shop;
    }

    public void save(Location chestLocation, String owner, ItemStack itemToSell, int amount, int buyPrice, int sellPrice, boolean canBuy, boolean canSell) {
        this.key = chestLocation.toString();
        _config.set(this.key + ".owner", owner);
        _config.set(this.key + ".item", itemToSell);
        _config.set(this.key + ".amount", amount);
        _config.set(this.key + ".price", buyPrice);
        _config.set(this.key + ".sellPrice", sellPrice);
        _config.set(this.key + ".bought", buyPrice >= 0);
        _config.set(this.key + ".sell", sellPrice >= 0);
    }

    public boolean canAccept(ItemStack itemToSell, int amount) {
        return accepts(itemToSell) && InventoryUtils.hasInventorySpace(this.chest.getInventory(), itemToSell.getType(), amount);
    }

    public boolean accepts(ItemStack item) {
        return this.item.getType().equals(item.getType());
    }
}
