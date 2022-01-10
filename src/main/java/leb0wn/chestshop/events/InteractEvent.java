package leb0wn.chestshop.events;

import leb0wn.chestshop.data.PlayerShop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static leb0wn.chestshop.utils.ShopUtils.getChestBehindSign;
import static leb0wn.chestshop.utils.ShopUtils.getShopFromConfig;

public class InteractEvent implements Listener {
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Sign) || !block.getType().equals(Material.OAK_WALL_SIGN) || ((Sign) block.getState()).getLines().length != 4) return;

        Sign sign = (Sign) block.getState();
        WallSign data = (WallSign) block.getBlockData();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && sign.getLine(0).equals("[SHOP]")) {
            // Create a new shop based on the sign data
            CreateChestShop.createShop(event);

        } else {
            // Try to get shop
            Chest chest = getChestBehindSign(sign);
            if (chest == null) return;
            PlayerShop shop = PlayerShop.getShopByLocation(chest.getLocation());
            if (shop == null) return;

            if (event.getPlayer().getUniqueId().toString().equals(shop.owner)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't buy or sell things from your own store!");
                return;
            }

            // Clicked on an existing shop
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                // Left-click = SELL
                ShopSystem.sell(event.getPlayer(), shop);
                event.setCancelled(true);

            } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                // Right-click = BUY
                ShopSystem.buy(event, block, data);
                event.setCancelled(true);

            }
        }
    }
}
