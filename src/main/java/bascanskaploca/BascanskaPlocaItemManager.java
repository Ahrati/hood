package bascanskaploca;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;

public class BascanskaPlocaItemManager {
    private Plugin plugin;

    public BascanskaPlocaItemManager(Plugin plugin){
        this.plugin = plugin;
        CreateBascanskaPloca();
    }

    private void CreateBascanskaPloca(){
        ItemStack item = new ItemStack(Material.POLISHED_DIORITE, 1);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Bašćanska Ploča");
        meta.setLore(Arrays.asList("Az, v ime Otca i Sina i Svetago Duha.", "Az opat Držiha pisah se o ledine,", "juže da Zvanimir kralj hrvatskij", "v dni svoje v Svetuju Luciju", "– i svedomi: župan Desimira Krbave,", "Mratin v Lice, Pribineža posal Vinodole,", "Jakov v otoce. Da iže to poreče,", "klni j Bog i dvanadeste apostola", "i četiri evanjelisti i svetaja Lucija,", "amen. Da iže sde živet, moli za nje Boga.",
                "Az opat Dobrovit zdah crekav", "siju i svojeju bratiju s devetiju", "v dni kneza Kosmata obladajućago", "vsu Krajinu. I beše v ti", "dni Mikula v Otočci s", "svetuju Luciju v jedino."));
        item.setItemMeta(meta);

        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, "bascanska_ploca"), item);
        shapedRecipe.shape("DDD", "DDD", "DDD");
        shapedRecipe.setIngredient('D', Material.DIORITE);
        Bukkit.getServer().addRecipe(shapedRecipe);
    }
}
