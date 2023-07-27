package casinochips;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public class CasinoChipCraftListener implements Listener {
    Plugin plugin;
    public CasinoChipCraftListener(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private boolean isCasinoChip(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                return displayName != null && ChatColor.stripColor(displayName).startsWith("$");
            }
        }
        return false;
    }

    @EventHandler
    public void onItemCraft(CraftItemEvent event) {
        HumanEntity player = event.getWhoClicked();
        ItemStack craftedItem = event.getCurrentItem();

        if (craftedItem != null && isCasinoChip(craftedItem)) {
            ItemMeta meta = craftedItem.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "crafterName"), PersistentDataType.STRING, player.getName());

            meta.setLore(Collections.singletonList(player.getName()));


            craftedItem.setItemMeta(meta);
        }
    }
}