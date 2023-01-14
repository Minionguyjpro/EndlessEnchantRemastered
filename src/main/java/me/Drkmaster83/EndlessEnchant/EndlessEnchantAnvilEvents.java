package me.Drkmaster83.EndlessEnchant;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class EndlessEnchantAnvilEvents implements Listener {
   private EndlessEnchant pl;

   public EndlessEnchantAnvilEvents(EndlessEnchant pl) {
      this.pl = pl;
   }

   @EventHandler
   public void onAnvil(InventoryClickEvent e) {
      if (this.pl.getConfig().getBoolean("anvil")) {
         if (e.getInventory().getType() == InventoryType.ANVIL) {
            AnvilInventory ai = (AnvilInventory)e.getInventory();
            if (ai.getItem(1) != null && ai.getItem(0) != null && ai.getItem(1).getType() == Material.ENCHANTED_BOOK) {
               if (e.getCurrentItem().isSimilar(ai.getItem(0)) || e.getCurrentItem() != ai.getItem(1)) {
                  EnchantmentStorageMeta meta = (EnchantmentStorageMeta)e.getInventory().getItem(1).getItemMeta();
                  e.getCurrentItem().addUnsafeEnchantments(meta.getStoredEnchants());
               }

            }
         }
      }
   }
}
