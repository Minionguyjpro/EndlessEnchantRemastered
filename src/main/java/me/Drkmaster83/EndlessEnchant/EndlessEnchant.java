package me.Drkmaster83.EndlessEnchant;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class EndlessEnchant extends JavaPlugin implements Listener {
   private YMLConfig enchants;
   private YMLConfig config;
   private HashMap<String, List<String>> enchantNames;
   private ArrayList<Kit> kits;
   private String serverVersion;
   private final String EEPrefix = "§f[§b§lEndless§c§lEnchant§f] ";
   private int highestLevel;
   private boolean glowLore;

   public void onEnable() {
      PluginDescriptionFile pdf = this.getDescription();
      this.serverVersion = this.getServer().getClass().getPackage().getName().substring(this.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);
      this.cacheEnchantmentAndKitNames();
      this.cacheKits();
      this.getServer().getPluginManager().registerEvents(new EndlessEnchantAnvilEvents(this), this);
      EndlessEnchantCommand eec = new EndlessEnchantCommand(this);
      this.getCommand("EndlessEnchant").setExecutor(eec);
      this.getCommand("EndlessEnchant").setTabCompleter(eec);
      this.getLogger().info(pdf.getName() + " v" + pdf.getVersion() + " has been enabled!");
   }

   public void onDisable() {
      PluginDescriptionFile pdf = this.getDescription();
      this.getLogger().info(pdf.getName() + " v" + pdf.getVersion() + " has been disabled!");
   }

   private void loadConfigurations() {
      if (this.enchants == null) {
         this.enchants = new YMLConfig(this, "enchants.yml");
      }

      if (this.config == null) {
         this.config = new YMLConfig(this, "config.yml");
      }

      if (!this.config.reload()) {
         this.getLogger().warning("Config.yml file was not found, creating one for you...");
      }

      if (!this.enchants.reload()) {
         this.getLogger().warning("Enchants.yml file was not found, creating one for you...");
      }

   }

   private void verifyFiles() {
      Reader enchantsJar = null;
      InputStreamReader configJar = null;

      try {
         enchantsJar = new InputStreamReader(this.getResource("enchants.yml"), "UTF8");
         configJar = new InputStreamReader(this.getResource("config.yml"), "UTF8");
      } catch (UnsupportedEncodingException var7) {
         var7.printStackTrace();
      }

      YamlConfiguration cachedEnchants = YamlConfiguration.loadConfiguration(enchantsJar);
      YamlConfiguration cachedConfig = YamlConfiguration.loadConfiguration(configJar);
      if (this.enchants.getConfigurationSection("Aliases") == null) {
         this.enchants.createSection("Aliases");
      }

      Iterator var6 = cachedEnchants.getConfigurationSection("Aliases").getKeys(false).iterator();

      while(var6.hasNext()) {
         String s = (String)var6.next();
         if (!this.enchants.getConfigurationSection("Aliases").contains(s)) {
            this.enchants.set("Aliases." + s, cachedEnchants.getStringList("Aliases." + s));
         }
      }

      if (this.enchants.getConfigurationSection("Kits") == null) {
         this.enchants.createSection("Kits");
      }

      if (this.enchants.getConfigurationSection("KitAliases") == null) {
         this.enchants.createSection("KitAliases");
      }

      this.enchants.save();
      if (this.config.get("highestLevelLimit") == null) {
         this.config.set("highestLevelLimit", cachedConfig.get("highestLevelLimit"));
      }

      this.highestLevel = this.config.getInt("highestLevelLimit", 5);
      if (this.config.get("glowInLore") == null) {
         this.config.set("glowInLore", cachedConfig.get("glowInLore"));
      }

      this.glowLore = this.config.getBoolean("glowInLore", true);
      this.config.save();
   }

   private void cacheEnchantmentAndKitNames() {
      this.loadConfigurations();
      this.verifyFiles();
      this.enchantNames = new HashMap();
      Iterator var2 = this.enchants.getConfigurationSection("Kits").getKeys(false).iterator();

      String enchName;
      ArrayList aliasList;
      String alias;
      Iterator var5;
      while(var2.hasNext()) {
         enchName = (String)var2.next();
         enchName = enchName.replaceAll("(?i)&[0-9A-FK-OR]", "");
         aliasList = new ArrayList();
         var5 = this.enchants.getStringList("KitAliases." + enchName).iterator();

         while(var5.hasNext()) {
            alias = (String)var5.next();
            aliasList.add(alias.toUpperCase());
         }

         this.enchantNames.put(enchName.toUpperCase(), aliasList);
      }

      var2 = this.enchants.getConfigurationSection("Aliases").getKeys(false).iterator();

      while(true) {
         while(var2.hasNext()) {
            enchName = (String)var2.next();
            if (Enchantment.getByName(enchName.toUpperCase()) == null) {
               this.getLogger().warning("Base enchantment name from enchants.yml \"" + enchName.toUpperCase() + "\" doesn't exist in this version of Minecraft - will continue without it...");
            } else if (EndlessEnchantment.getByName(enchName) == null) {
               this.getLogger().warning("Base enchantment name from enchants.yml \"" + enchName.toUpperCase() + "\" doesn't exist in code - will continue without it...");
            } else {
               aliasList = new ArrayList();
               var5 = this.enchants.getStringList("Aliases." + enchName).iterator();

               while(var5.hasNext()) {
                  alias = (String)var5.next();
                  aliasList.add(alias.toUpperCase());
               }

               this.enchantNames.put(enchName.replaceAll("(?i)&[0-9A-FK-OR]", "").toUpperCase(), aliasList);
            }
         }

         return;
      }
   }

   private void cacheKits() {
      this.kits = new ArrayList();

      String kitName;
      ArrayList kitEnchantments;
      label40:
      for(Iterator var2 = this.enchants.getConfigurationSection("Kits").getKeys(false).iterator(); var2.hasNext(); this.kits.add(new Kit(kitName.toUpperCase().replaceAll("&[0-9A-FK-OR]", ""), kitName.toUpperCase(), kitEnchantments))) {
         kitName = (String)var2.next();
         kitEnchantments = new ArrayList();
         Iterator var5 = this.enchants.getStringList("Kits." + kitName).iterator();

         while(true) {
            while(true) {
               if (!var5.hasNext()) {
                  continue label40;
               }

               String enchant = (String)var5.next();
               if (enchant.equals("*")) {
                  EndlessEnchantment[] var9;
                  int var8 = (var9 = EndlessEnchantment.values()).length;

                  for(int var7 = 0; var7 < var8; ++var7) {
                     EndlessEnchantment e = var9[var7];
                     kitEnchantments.add(e);
                  }
               } else if (Enchantment.getByName(enchant.toUpperCase()) == null) {
                  this.getLogger().warning("Enchantment \"" + enchant + "\" in the Kits section (Kit name: \"" + kitName + "\") of the enchants.yml is not a valid game enchantment.");
               } else if (EndlessEnchantment.getByName(enchant) == null) {
                  this.getLogger().warning("Enchantment \"" + enchant + "\" in the Kits section (Kit name: \"" + kitName + "\") of the enchants.yml is not a valid enchantment.");
               } else {
                  kitEnchantments.add(EndlessEnchantment.getByName(enchant));
               }
            }
         }
      }

   }

   public String getBaseName(String alias) {
      if (this.enchantNames.keySet().contains(alias.toUpperCase())) {
         return alias.toUpperCase();
      } else {
         Iterator var3 = this.enchantNames.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<String, List<String>> entry = (Entry)var3.next();
            if (((List)entry.getValue()).contains(alias.toUpperCase())) {
               return ((String)entry.getKey()).toUpperCase();
            }
         }

         return null;
      }
   }

   public String getLowerName(String formal) {
      return formal.toLowerCase().replace("_", " ");
   }

   public boolean isValidEnch(String possibleAlias) {
      return this.getBaseName(possibleAlias) != null;
   }

   public Kit getKit(String possibleAlias) {
      Iterator var3 = this.kits.iterator();

      while(var3.hasNext()) {
         Kit k = (Kit)var3.next();
         if (k.getName().equalsIgnoreCase(possibleAlias)) {
            return k;
         }

         if (((List)this.enchantNames.get(k.getName())).contains(possibleAlias.toUpperCase())) {
            return k;
         }
      }

      return null;
   }

   public EndlessEnchant.GlowTagType getGlowTagType(ItemStack i) {
      if (i != null && i.getType() != Material.AIR) {
         if (this.glowLore) {
            if (!i.hasItemMeta()) {
               return null;
            }

            ItemMeta im = i.getItemMeta();
            if (im.hasLore() && im.getLore().equals(Arrays.asList("§7Glow I"))) {
               return EndlessEnchant.GlowTagType.LORE;
            }
         }

         try {
            Object nms1Stack = this.getField(i, "handle");
            Object tagComp = nms1Stack.getClass().getMethod("getTag").invoke(nms1Stack);
            if (tagComp == null) {
               tagComp = Class.forName("net.minecraft.server." + this.serverVersion + ".NBTTagCompound").newInstance();
               nms1Stack.getClass().getMethod("setTag", tagComp.getClass()).invoke(nms1Stack, tagComp);
            }

            if ((Boolean)tagComp.getClass().getMethod("getBoolean", String.class).invoke(tagComp, "glowEffect")) {
               return EndlessEnchant.GlowTagType.NBT;
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }

         return null;
      } else {
         return null;
      }
   }

   public boolean hasGlowTag(ItemStack i) {
      return this.getGlowTagType(i) != null;
   }

   public void addGlow(ItemStack item) {
      if (item != null) {
         if (!item.hasItemMeta() || !item.getItemMeta().hasEnchants() || item.getItemMeta().getEnchants().size() < 1) {
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
            ItemMeta i = item.getItemMeta();
            i.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            if (this.glowLore) {
               i.setLore(Arrays.asList("§7Glow I"));
            }

            item.setItemMeta(i);
            if (!this.glowLore) {
               try {
                  Object nmsStack = this.getField(item, "handle");
                  Object tagComp = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
                  if (tagComp == null) {
                     tagComp = Class.forName("net.minecraft.server." + this.serverVersion + ".NBTTagCompound").newInstance();
                  }

                  tagComp.getClass().getMethod("setBoolean", String.class, Boolean.TYPE).invoke(tagComp, "glowEffect", true);
                  nmsStack.getClass().getMethod("setTag", tagComp.getClass()).invoke(nmsStack, tagComp);
                  item = (ItemStack)item.getClass().getMethod("asBukkitCopy", nmsStack.getClass()).invoke((Object)null, nmsStack);
               } catch (Exception var5) {
                  var5.printStackTrace();
               }
            }

         }
      }
   }

   public void removeGlow(ItemStack item) {
      if (this.hasGlowTag(item)) {
         ItemMeta im = item.getItemMeta();
         im.removeItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         if (this.getGlowTagType(item) == EndlessEnchant.GlowTagType.LORE) {
            im.setLore(new ArrayList());
         }

         item.setItemMeta(im);
      }

      try {
         Object nmsStack = this.getField(item, "handle");
         Object tagComp = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
         if (tagComp == null) {
            tagComp = Class.forName("net.minecraft.server." + this.serverVersion + ".NBTTagCompound").newInstance();
         }

         tagComp.getClass().getMethod("remove", String.class).invoke(tagComp, "ench");
         if (this.hasGlowTag(item) && this.getGlowTagType(item) == EndlessEnchant.GlowTagType.NBT) {
            tagComp.getClass().getMethod("remove", String.class).invoke(tagComp, "glowEffect");
         }

         nmsStack.getClass().getMethod("setTag", tagComp.getClass()).invoke(nmsStack, tagComp);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   private Object getField(Object obj, String name) {
      try {
         Field field = obj.getClass().getDeclaredField(name);
         field.setAccessible(true);
         return field.get(obj);
      } catch (Exception var4) {
         throw new RuntimeException("Unable to retrieve field content.", var4);
      }
   }

   public void addEnchantment(ItemStack item, Enchantment enchantment, String level, boolean endless) {
      this.addEnchantment(item, enchantment, this.getNumber(level), endless);
   }

   public void addEnchantment(ItemStack item, Enchantment enchantment, int level, boolean endless) {
      if (!endless && level > this.highestLevel) {
         level = this.highestLevel;
      }

      if (this.hasGlowTag(item)) {
         this.removeGlow(item);
      }

      if (item.getType() != Material.BOOK && item.getType() != Material.ENCHANTED_BOOK && item.getType() != Material.WRITABLE_BOOK) {
         item.addUnsafeEnchantment(enchantment, level);
      } else {
         if (item.getType() == Material.BOOK || item.getType() == Material.WRITABLE_BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
         }

         EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
         meta.addStoredEnchant(enchantment, level, true);
         item.setItemMeta(meta);
      }
   }

   public void removeEnchantment(ItemStack item, Enchantment enchantment) {
      if (item.getType() != Material.ENCHANTED_BOOK) {
         item.removeEnchantment(enchantment);
      } else {
         EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
         if (meta.getEnchants().size() <= 1) {
            item.setType(Material.BOOK);
         } else {
            meta.removeStoredEnchant(enchantment);
            item.setItemMeta(meta);
         }

      }
   }

   public int getNumber(String num) {
      try {
         int i = Integer.parseInt(num);
         return i;
      } catch (Exception var3) {
         return 0;
      }
   }

   public HashMap<String, List<String>> getEnchMap() {
      return this.enchantNames;
   }

   public int getHighest() {
      return this.highestLevel;
   }

   public List<Kit> getKits() {
      return this.kits;
   }

   public String getPrefix() {
      return "§f[§b§lEndless§c§lEnchant§f] ";
   }

   public YMLConfig getConfig() {
      return this.config;
   }

   static enum GlowTagType {
      LORE,
      NBT;
   }
}
