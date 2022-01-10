package leb0wn.chestshop.events;

import leb0wn.chestshop.ChestShop;
import leb0wn.chestshop.data.PlayerShop;
import leb0wn.chestshop.utils.InventoryUtils;
import static leb0wn.chestshop.utils.ShopUtils.getChestBehindSign;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ShopSystem {


    // BUY
    public static void buy(PlayerInteractEvent event, Block block, WallSign data) {
        Chest chest = null;
        Inventory playerInventory = event.getPlayer().getInventory();

        if(data.getFacing().equals(BlockFace.EAST)){
            chest = (Chest) block.getLocation().add(-1, 0, 0).getBlock().getState();
        } else if (data.getFacing().equals(BlockFace.SOUTH)) {
            chest = (Chest) block.getLocation().add(0, 0, -1).getBlock().getState();
        } else if (data.getFacing().equals(BlockFace.WEST)) {
            chest = (Chest) block.getLocation().add(1, 0, 0).getBlock().getState();
        } else if (data.getFacing().equals(BlockFace.NORTH)) {
            chest = (Chest) block.getLocation().add(0, 0, 1).getBlock().getState();
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "Chest is null!");
            return;
        }
        Inventory chestInventory = chest.getInventory();
        Location chestLoc = chest.getLocation();

        // Check if shop exists
        if(!ChestShop.getInstance().getConfig().contains(String.format("%1$s_%2$d_%3$d_%4$d.item", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()))){
            Bukkit.broadcastMessage(ChatColor.RED + "Shop not in config!");
            return;
        }

        String owner = ChestShop.getInstance().getConfig().getString(String.format("%1$s_%2$d_%3$d_%4$d.owner", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()));
        ItemStack itemToBuy = ChestShop.getInstance().getConfig().getItemStack(String.format("%1$s_%2$d_%3$d_%4$d.item", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()));
        int amount = ChestShop.getInstance().getConfig().getInt(String.format("%1$s_%2$d_%3$d_%4$d.amount", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()));
        int boughtPrice = ChestShop.getInstance().getConfig().getInt(String.format("%1$s_%2$d_%3$d_%4$d.price", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()));

        if(event.getPlayer().getUniqueId().toString().equals(owner)){
            event.getPlayer().sendMessage(ChatColor.RED + "You can't buy things from your own store!");
            return;
        }

        // Check if chest has room for Emeralds
        if (!InventoryUtils.hasInventorySpace(chestInventory, Material.EMERALD, boughtPrice)) {
            event.getPlayer().sendMessage(ChatColor.RED + "The shop has no place to store the emeralds!");
            return;
        }

        // Check if chest has the required number of items we want to buy
        if(!InventoryUtils.hasItems(chestInventory, itemToBuy, amount)) {
            event.getPlayer().sendMessage(ChatColor.RED + "The shop is out of stock (material: " + itemToBuy.getType() + ")");
            return;
        }

        // Check if player has enough inventory space for the items we want to buy
        if (!InventoryUtils.hasInventorySpace(playerInventory, itemToBuy.getType(), amount)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You don't have enough space in your inventory");
            return;
        }

        // Check if player has enough emeralds
        if (!InventoryUtils.hasItems(playerInventory, new ItemStack(Material.EMERALD), boughtPrice)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You don't have enough emeralds!");
            return;
        }

        // Exchange emeralds
        InventoryUtils.exchangeItems(playerInventory, chestInventory, Material.EMERALD, boughtPrice);

        // Exchange items
        InventoryUtils.exchangeItems(chestInventory, playerInventory, itemToBuy.getType(), amount);

        // Done
        event.getPlayer().sendMessage(String.format("You just bought §6%1$s §7for §6%2$d emerald(s)§7!", itemToBuy.getType().name() + " x" + amount, boughtPrice));
    }

    // SELL
    public static void sell(Player player, PlayerShop shop) {
        // Left-click           = sell 1
        // Shift + Left-click   = sell everything in your hand
        //int sellAmount = (player.isSneaking() ? itemToSell.getAmount() : 1);
        int sellAmount = shop.stackSize;

        // Check if shop chest has enough room
        if (!InventoryUtils.hasInventorySpace(shop.chest.getInventory(), shop.item.getType(), shop.stackSize)) {
            // Chest has not enough room available to store the amount of items you want to sell
            player.sendMessage(ChatColor.RED + "Chest has not enough room available to store the amount of items you want to sell");
            return;
        }

        // Check if shop chest has enough emeralds to return to the player
        if (!InventoryUtils.hasItems(shop.chest.getInventory(), new ItemStack(Material.EMERALD), shop.sellPrice)) {
            player.sendMessage(ChatColor.RED + "Shop has not enough emeralds available to return");
            return;
        }

        // Check if player has enough room for emeralds... who doesn't ?!
        if (!InventoryUtils.hasInventorySpace(player.getInventory(), Material.EMERALD, shop.sellPrice)) {
            player.sendMessage(ChatColor.RED + "You don't have enough room for the emeralds");
            return;
        }

        // Trade
        InventoryUtils.exchangeItems(player.getInventory(), shop.chest.getInventory(), shop.item.getType(), sellAmount);
        InventoryUtils.exchangeItems(shop.chest.getInventory(), player.getInventory(), Material.EMERALD, shop.sellPrice);
    }

    public static void dropChestShopItem(World world, Location chestLoc, ItemStack heldItem, Integer itemAmount){
        Item i = world.dropItem(chestLoc.add(0.5, 1, 0.5), heldItem);
        i.setVelocity(new Vector(0.0, 0.1, 0.0));
        i.setPickupDelay(Integer.MAX_VALUE);
        i.addScoreboardTag("ChestShopItemTag");
        i.setInvulnerable(true);
        i.setCustomNameVisible(true);
        i.setTicksLived(Integer.MAX_VALUE);
        String itemName = null;
        if(heldItem.getItemMeta().hasDisplayName()){
            itemName = heldItem.getItemMeta().getDisplayName();
        } else {
            itemName = heldItem.getType().name();
        }
        i.setCustomName(String.format("§6%1$s §7x§6%2$s", itemName, itemAmount.toString()));
    }
}
