package me.Drkmaster83.EndlessEnchant;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;

public class YMLConfig extends YamlConfiguration {
   private EndlessEnchant pl;
   private File file;

   public YMLConfig(EndlessEnchant pl, String name) {
      this.pl = pl;
      if (!name.endsWith(".yml")) {
         name = name + ".yml";
      }

      this.file = new File(pl.getDataFolder(), name);
   }

   public boolean reload() {
      boolean existed = this.file.exists();
      if (!this.file.exists()) {
         if (this.pl.getResource(this.file.getName()) != null) {
            this.pl.saveResource(this.file.getName(), true);
         } else {
            try {
               this.file.createNewFile();
            } catch (IOException var4) {
               var4.printStackTrace();
            }
         }
      }

      try {
         this.load(this.file);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return existed;
   }

   public void save() {
      try {
         this.save(this.file);
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }
}
