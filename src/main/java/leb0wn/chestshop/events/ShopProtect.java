package leb0wn.chestshop.events;

import leb0wn.chestshop.ChestShop;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class ShopProtect implements Listener {
    @EventHandler
    private void onChestRightClick(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(event.getClickedBlock().getType().equals(Material.CHEST)){
                if(ChestShop.getInstance().getConfig().contains(String.format("%1$s_%2$d_%3$d_%4$d", event.getClickedBlock().getWorld().getName(), event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockY(), event.getClickedBlock().getLocation().getBlockZ()))){
                    Location chestLoc = event.getClickedBlock().getLocation();
                    String owner;
                    owner = (String) ChestShop.getInstance().getConfig().get(String.format("%1$s_%2$d_%3$d_%4$d.owner", chestLoc.getWorld().getName(), chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ()), event.getPlayer().getUniqueId().toString());
                    if(!(event.getPlayer().getUniqueId().equals(UUID.fromString(owner)))){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlock().getLocation().add(0, -1, 0).getBlock();
        if(block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            if(chest.getCustomName()==null){
                return;
            }
            if(chest.getCustomName().startsWith(ChatColor.YELLOW + "ChestShop")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDoubleChestCreate(BlockPlaceEvent event){
        for(BlockFace face: BlockFace.values()){
            Block block = event.getBlock().getRelative(face);
            if(block.getType()==Material.CHEST){
                Chest chest = (Chest) block.getState();
                if(chest.getCustomName() == null){
                    return;
                }
                else if(chest.getCustomName().startsWith(ChatColor.YELLOW + "ChestShop")){
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't place a chest next to this shop!");
                    event.setCancelled(true);
                }
            }
        }
    }

//    @EventHandler
//    public void onBlockFormTo(BlockFormEvent event){
//        Block block = event.getBlock()/*.getLocation().add(0, -1, 0).getBlock()*/;
//        if(block.getType() == Material.CHEST){
//            Chest chest = (Chest) block.getState();
//            if(chest.getCustomName()==null){
//                return;
//            }
//            if(chest.getCustomName().startsWith(ChatColor.YELLOW + "ChestShop")){
//                event.setCancelled(true);
//            }
//        }
//    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event){
        if(event.getCaught().getScoreboardTags().contains("ChestShopItemTag")){
            event.getHook().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
        Block block = event.getBlock().getLocation().add(0, -1, 0).getBlock();
        Block block2 = event.getBlock().getLocation().getBlock();
        if(block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            if(chest.getCustomName() == null){
                return;
            }
            if(chest.getCustomName().startsWith(ChatColor.YELLOW + "ChestShop")){
                event.setCancelled(true);
            }
        } else if (block2.getType() == Material.CHEST){
            Chest chest = (Chest) block2.getState();
            if(chest.getCustomName()==null){
                return;
            }
            if (chest.getCustomName().startsWith(ChatColor.YELLOW + "ChestShop")){
                event.setCancelled(true);
            }
        }
    }
}
