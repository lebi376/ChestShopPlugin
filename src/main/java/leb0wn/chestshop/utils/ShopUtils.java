package leb0wn.chestshop.utils;

import leb0wn.chestshop.data.PlayerShop;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Chest;
import org.bukkit.Material;
import org.bukkit.block.data.Directional;

public class ShopUtils {
    public static Sign getAsShopSign(Block block) {
        if (block == null || !block.getType().equals(Material.OAK_WALL_SIGN) || !(block.getState() instanceof Sign))
            return null;

        Sign sign = (Sign)block.getState();
        if (
                sign.getLines().length == 4
                        && (sign.getLine(0).equals("[SHOP]") || sign.getLine(0).equals("SHOP"))
                        && sign.getBlockData() instanceof Directional
                        && getChestBehindSign(sign) != null)

            return sign;

        return null;
    }

    public static Chest getChestBehindSign(Sign sign) {
        if (sign == null || !(sign.getBlockData() instanceof Directional))
            return null;

        Directional signDirection = (Directional)sign.getBlockData();
        return (Chest)sign.getBlock().getRelative(signDirection.getFacing().getOppositeFace()).getState();
    }

    public static PlayerShop getShopFromConfig(Location chestLocation) {
        return new PlayerShop().getShopByLocation(chestLocation);
    }
}
