package me.Drkmaster83.EndlessEnchant;

import java.util.List;

class Kit {
   private String name;
   private String format;
   private String suffix;
   private List<EndlessEnchantment> endEnchants;

   public Kit(String name, String format, List<EndlessEnchantment> endEnchants) {
      this.name = name;
      if (format.toCharArray()[format.length() - 2] == '&') {
         this.suffix = "§" + format.substring(format.length() - 1);
         this.format = format.substring(0, format.length() - 2);
      } else {
         this.format = format;
         this.suffix = "";
      }

      this.endEnchants = endEnchants;
   }

   public List<EndlessEnchantment> getEndlessEnchantments() {
      return this.endEnchants;
   }

   public String getName() {
      return this.name;
   }

   public String getFormat() {
      return this.format;
   }

   public String getSuffix() {
      return this.suffix;
   }
}
