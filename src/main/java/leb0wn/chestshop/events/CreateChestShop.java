package leb0wn.chestshop.events;

import leb0wn.chestshop.ChestShop;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static leb0wn.chestshop.utils.NumberUtils.tryParseInt;

public class CreateChestShop {
    public static void createShop(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Chest chest = null;
        Sign blockState = (Sign) block.getState();
        WallSign wallSignData = (WallSign) blockState.getBlockData();

        // Sign must have 4 lines
        List<String> lines = Arrays.asList(blockState.getLines());
        if (lines.size() != 4)
            return;

        if (lines.get(0).equals("[SHOP]")) {
            Integer itemAmount = tryParseInt(lines.get(1)),
                    boughtPrice = tryParseInt(lines.get(2)),
                    sellPrice = tryParseInt(lines.get(3));

            if (itemAmount != null) {
                if (itemAmount < 1) {
                    event.getPlayer().sendMessage(ChatColor.RED + "The minimum amount of items must be at least 1!");
                    return;
                }
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "The item amount must be a number!");
                return;
            }

            if (boughtPrice != null) {
                if (boughtPrice < 1 && boughtPrice != -1) {
                    event.getPlayer().sendMessage(ChatColor.RED + "The buy price should be set at a minimum of 1!");
                    return;
                }
            } else {
                boughtPrice = -1;
            }

            if (sellPrice != null) {
                if (sellPrice < 1) {
                    event.getPlayer().sendMessage(ChatColor.RED + "The sell price should be set at a minimum of 1!");
                    return;
                }
            } else {
                sellPrice = -1;
            }

            if ((boughtPrice == -1) && (sellPrice == -1)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You need to set the buy and or sell price to create a shop!");
                return;
            }

            ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
            if (heldItem.getType().equals(Material.AIR)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You have to hold the item you wish to sell!");
                return;
            }
            BlockFace face = null;

            if (event.getClickedBlock().getLocation().add(-1, 0, 0).getBlock().getType().equals(Material.CHEST)) {
                chest = (Chest) event.getClickedBlock().getLocation().add(-1, 0, 0).getBlock().getState();
                face = BlockFace.EAST;
            } else if (event.getClickedBlock().getLocation().add(0, 0, -1).getBlock().getType().equals(Material.CHEST)) {
                chest = (Chest) event.getClickedBlock().getLocation().add(0, 0, -1).getBlock().getState();
                face = BlockFace.SOUTH;
            } else if (event.getClickedBlock().getLocation().add(1, 0, 0).getBlock().getType().equals(Material.CHEST)) {
                chest = (Chest) event.getClickedBlock().getLocation().add(1, 0, 0).getBlock().getState();
                face = BlockFace.WEST;
            } else if (event.getClickedBlock().getLocation().add(0, 0, 1).getBlock().getType().equals(Material.CHEST)) {
                chest = (Chest) event.getClickedBlock().getLocation().add(0, 0, 1).getBlock().getState();
                face = BlockFace.NORTH;
            }
            org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();
            if (chestData.getFacing() != face){
//            if (chest == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "The sign needs to be in front of the chest!");
                return;
            }
            chest.setCustomName(ChatColor.YELLOW + "ChestShop Chest");
            chest.update();

            ShopSystem.dropChestShopItem(event.getPlayer().getWorld(), chest.getLocation(), heldItem, itemAmount);


            event.getPlayer().sendMessage(ChatColor.GREEN + "You have successfully created a shop!");
            blockState.setLine(0, ChatColor.YELLOW + "SHOP");

            if (heldItem.getItemMeta().hasDisplayName()) {
                blockState.setLine(1, String.format("%1$s x%2$d", heldItem.getItemMeta().getDisplayName(), itemAmount));
            } else {
                blockState.setLine(1, String.format("%1$s x%2$d", heldItem.getType().name(), itemAmount));
            }
            if (boughtPrice >= 0) {
                blockState.setLine(2, String.format("B: %1$d", boughtPrice));
                if (sellPrice >= 0) {
                    blockState.setLine(2, String.format("B %1$d : %2$d S", boughtPrice, sellPrice));
                }
            } else {
                blockState.setLine(2, String.format("S: %1$d", sellPrice));
            }
            blockState.setLine(3, ChatColor.BOLD + event.getPlayer().getDisplayName());
            blockState.update();

            Location chestLoc = chest.getLocation();
            FileConfiguration config = ChestShop.getPlugin(ChestShop.class).getConfig();
            ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d.item", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()), heldItem);
            ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d.price", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()), boughtPrice);
            ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d.sellPrice", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()), sellPrice);
            ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d.amount", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()), itemAmount);
            ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d.bought", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()), boughtPrice >= 0);
            ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d.sell", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()), sellPrice >= 0);
            ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d.owner", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()), event.getPlayer().getUniqueId().toString());
        }
//        else {
//            ShopSystem.buy(event, block, wallSignData);
//        }
    }
}
