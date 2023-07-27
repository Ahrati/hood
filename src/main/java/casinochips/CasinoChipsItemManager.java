package casinochips;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CasinoChipsItemManager {
    public static void init(){
        createCasinoChip();
    }

    private static void createCasinoChip(){
        ItemStack item = new ItemStack(Material.WHITE_TERRACOTTA,8);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "$1 Casino Chip");
        item.setItemMeta(meta);

        ShapedRecipe shapedRecipe = new ShapedRecipe(NamespacedKey.minecraft("casino_chip_1"), item);
        shapedRecipe.shape("TTT","TCT","TTT");
        shapedRecipe.setIngredient('T', Material.WHITE_TERRACOTTA);
        shapedRecipe.setIngredient('C', Material.COPPER_BLOCK);
        Bukkit.getServer().addRecipe(shapedRecipe);
    }


}
