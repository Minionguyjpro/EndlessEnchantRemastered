package me.Drkmaster83.EndlessEnchant;

public enum EndlessEnchantment {
   PROTECTION_ENVIRONMENTAL("ARMOR", "b", 0),
   PROTECTION_FIRE("ARMOR", "b", 1),
   PROTECTION_FALL("ARMOR", "b", 2),
   PROTECTION_EXPLOSIONS("ARMOR", "b", 3),
   PROTECTION_PROJECTILE("ARMOR", "b", 4),
   OXYGEN("HELM", "b", 5),
   WATER_WORKER("HELM", "b", 6),
   THORNS("ARMOR", "b", 7),
   DEPTH_STRIDER("BOOTS", "b", 8),
   FROST_WALKER("BOOTS", "b", 9),
   BINDING_CURSE("ARMOR", "b", 10),
   DAMAGE_ALL("WEAPON", "c", 16),
   DAMAGE_UNDEAD("WEAPON", "c", 17),
   DAMAGE_ARTHROPODS("WEAPON", "c", 18),
   KNOCKBACK("WEAPON", "c", 19),
   FIRE_ASPECT("WEAPON", "c", 20),
   LOOT_BONUS_MOBS("WEAPON", "c", 21),
   SWEEPING_EDGE("WEAPON", "c", 22),
   LOYALTY("WEAPON", "c", 65);
   IMPALING("WEAPON", "c", 66);
   RIPTIDE("WEAPON", "c", 67);
   CHANNELING("WEAPON", "c", 68);
   DIG_SPEED("TOOL", "d", 32),
   SILK_TOUCH("TOOL", "d", 33),
   LOOT_BONUS_BLOCKS("TOOL", "d", 35),
   ARROW_DAMAGE("RANGED_WEAPON", "4", 48),
   ARROW_KNOCKBACK("RANGED_WEAPON", "4", 49),
   ARROW_FIRE("RANGED_WEAPON", "4", 50),
   ARROW_INFINITE("RANGED_WEAPON", "4", 51),
   LUCK("FISHING", "3", 61),
   LURE("FISHING", "3", 62),
   DURABILITY("ALL", "a", 34),
   MENDING("ALL", "a", 70),
   VANISHING_CURSE("ALL", "a", 71);

   private String category;
   private String categoryColor;
   private short id;

   private EndlessEnchantment(String category, String categoryColor, int id) {
      this.category = category;
      this.categoryColor = categoryColor;
      this.id = (short)id;
   }

   public String getCategory() {
      return this.category;
   }

   public String getCategoryColor() {
      return this.categoryColor;
   }

   public int getID() {
      return this.id;
   }

   public static EndlessEnchantment getByName(String baseName) {
      EndlessEnchantment[] var4;
      int var3 = (var4 = values()).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         EndlessEnchantment e = var4[var2];
         if (e.name().equals(baseName.toUpperCase())) {
            return e;
         }
      }

      return null;
   }
}
