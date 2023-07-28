package casinochips;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CasinoChipsItemManager {
    private Plugin plugin;

    public CasinoChipsItemManager(Plugin plugin) {
        this.plugin = plugin;
        createCasinoChip(1, Material.WHITE_TERRACOTTA, 8);
        createCasinoChip(5, Material.RED_TERRACOTTA, 8);
        createCasinoChip(10, Material.LIME_TERRACOTTA, 8);
        createCasinoChip(50, Material.LIGHT_BLUE_TERRACOTTA, 8);
        createCasinoChip(100, Material.YELLOW_TERRACOTTA, 8);
        createCasinoChip(500, Material.BLACK_TERRACOTTA, 8);
    }

    private void createCasinoChip(int denomination, Material color, int amount) {
        ItemStack item = new ItemStack(color, amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "$" + denomination + " Casino Chip");
        item.setItemMeta(meta);

        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, "casino_chip_" + denomination), item);
        shapedRecipe.shape("TTT", "TCT", "TTT");
        shapedRecipe.setIngredient('T', color);
        shapedRecipe.setIngredient('C', Material.COPPER_BLOCK);
        Bukkit.getServer().addRecipe(shapedRecipe);
    }
}
