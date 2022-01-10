package leb0wn.chestshop.utils;

import leb0wn.chestshop.ChestShop;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class InventoryUtils {
    public static boolean hasItems(Inventory inv, ItemStack item, int amount) {
        Logger log = (Logger) ChestShop.getPlugin(ChestShop.class).getLogger();
        //log.info("hasItems(inv: " + inv.getSize() + ", item: " + item.getType() + ", amount: " + amount + ")");

        int amountFound = 0;
        for (ItemStack invItem: inv.getContents()) {
            if (invItem == null) continue;
            if (invItem.getType().equals(item.getType())) {
                amountFound += invItem.getAmount();
                //log.info("Found " + amountFound + " " + item.getType() + " (" + (amount - amountFound) + " left to find)");
                if (amountFound >= amount) {
                    //log.info("Found all " + amount + " " + item.getType());
                    return true;
                }
            }
        }

        //log.info("Did not find all " + amount + " " + item.getType());
        return false;
    }

    public static boolean takeItems(Inventory inv, Material material, int amount) {
        ItemStack item = new ItemStack(material);
        if (!hasItems(inv, item, amount))
            return false;

        int slot = inv.first(material);
        int amountLeftToTake = amount;
        while (slot != -1) {
            ItemStack invItem = inv.getItem(slot);
            int invItemAmount = invItem.getAmount();
            if (invItemAmount >= amountLeftToTake) {
                // Take all from this stack
                invItem.setAmount(invItemAmount - amountLeftToTake);
                amountLeftToTake = 0;
                break;
            } else {
                // Take, but we need more
                invItem.setAmount(0);
                amountLeftToTake -= invItemAmount;
                slot = inv.first(material);
            }
        }

        if (amountLeftToTake > 0) {
            throw new ArithmeticException("Player doesn't really have that much. We still need " + amountLeftToTake + " more " + material);
        }

        return true;
    }

    public static boolean giveItems(Inventory inv, Material material, int amount) {
//        int stackSize = item.getMaxStackSize();
//        HashMap<Integer, ? extends ItemStack> existingItems = inv.all(item);
//        int amountAvailableInExistingStacks = 0;
//
//        for (ItemStack existingStack: existingItems.values()) {
//            amountAvailableInExistingStacks += stackSize - existingStack.getAmount();
//        }
//
//        if (amountAvailableInExistingStacks < amount)
//            return false;

        if (!hasInventorySpace(inv, material, amount))
            return false;

        inv.addItem(new ItemStack(material, amount));
        return true;
    }

    public static boolean hasInventorySpace(Inventory inv, Material material, int amount){
        int spaceAvailable = 0;

        for (ItemStack invItem: inv.getContents()) {
            if (invItem == null) {
                spaceAvailable += material.getMaxStackSize();
                continue;
            }

            if (invItem.getType().equals(material)) {
                spaceAvailable += material.getMaxStackSize() - invItem.getAmount();
            }
        }
        return spaceAvailable >= amount;
    }

    public static void exchangeItems(Inventory from, Inventory to, Material item, int amount) {
        takeItems(from, item, amount);
        giveItems(to, item, amount);
    }

}
