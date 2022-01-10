package leb0wn.chestshop.events;

import leb0wn.chestshop.ChestShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Objects;
import java.util.UUID;

public class DeleteShop implements Listener {

    @EventHandler
    public void deleteShop(BlockBreakEvent event){
        Block block = event.getBlock();
        if(block.getType().equals(Material.CHEST)){
            if(!(ChestShop.getInstance().getConfig().contains(String.format("%1$s_%2$d_%3$d_%4$d", block.getLocation().getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ())))){
                return;
            }
            Chest chest = (Chest) block.getState();
            String owner = ChestShop.getInstance().getConfig().getString(String.format("%1$s_%2$d_%3$d_%4$d.owner", block.getLocation().getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()));
            if(!Objects.equals(owner, event.getPlayer().getUniqueId().toString())){
                event.getPlayer().sendMessage(ChatColor.RED + "Only the owner can destroy this shop!");
                event.setCancelled(true);
                return;
            }
            for(Entity i: event.getBlock().getWorld().getEntities()){
                if(block.getLocation().add(0.5,1,0.5).distance(i.getLocation())<=0.2){
                    if(i.getScoreboardTags().contains("ChestShopItemTag")) {
                        chest.setCustomName(null);
                        chest.update();
                        chest.getBlock().breakNaturally();
                        i.remove();
                        event.getPlayer().sendMessage(ChatColor.BLUE + "You deleted a shop!");
                        ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d", block.getLocation().getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()), null);
                        ChestShop.getInstance().saveConfig();
                        return;
                    }
                }
            }
        } else if (block.getBlockData() instanceof WallSign){
            Sign sign = (Sign) block.getState();
            WallSign data = (WallSign) sign.getBlockData();
            Chest chest = null;
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
            org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();
            if (data.getFacing() != chestData.getFacing()){
                return;
            }
            if(!(ChestShop.getInstance().getConfig().contains(String.format("%1$s_%2$d_%3$d_%4$d", chest.getLocation().getWorld().getName(), chest.getLocation().getBlockX(), chest.getLocation().getBlockY(), chest.getLocation().getBlockZ())))){
                return;
            }
            String owner = ChestShop.getInstance().getConfig().getString(String.format("%1$s_%2$d_%3$d_%4$d.owner", chest.getLocation().getWorld().getName(), chest.getLocation().getBlockX(), chest.getLocation().getBlockY(), chest.getLocation().getBlockZ()));
            if(!Objects.equals(owner, event.getPlayer().getUniqueId().toString())){
                event.getPlayer().sendMessage(ChatColor.RED + "Only the owner can destroy this shop!");
                event.setCancelled(true);
                return;
            }
            for(Entity i: event.getBlock().getWorld().getEntities()){
                if(chest.getLocation().add(0.5,1,0.5).distance(i.getLocation())<=0.2){
                    if(i.getScoreboardTags().contains("ChestShopItemTag")) {
                        chest.setCustomName(null);
                        chest.update();
                        chest.getBlock().breakNaturally();
                        i.remove();
                        event.getPlayer().sendMessage(ChatColor.BLUE + "You deleted a shop!");
                        ChestShop.getInstance().getConfig().set(String.format("%1$s_%2$d_%3$d_%4$d", chest.getLocation().getWorld().getName(), chest.getLocation().getBlockX(), chest.getLocation().getBlockY(), chest.getLocation().getBlockZ()), null);
                        ChestShop.getInstance().saveConfig();
                        return;
                    }
                }
            }
        }
    }
}
