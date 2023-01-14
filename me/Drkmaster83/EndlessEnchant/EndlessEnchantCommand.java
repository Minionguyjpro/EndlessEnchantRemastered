package me.Drkmaster83.EndlessEnchant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EndlessEnchantCommand implements CommandExecutor, TabCompleter {
   private EndlessEnchant ee;
   private final String invalidLevel;
   private final String invalidEnchant;
   private final String noPermission;

   public EndlessEnchantCommand(EndlessEnchant ee) {
      this.ee = ee;
      this.invalidLevel = ee.getPrefix() + "§4A level §6(0-32767)§4 is required to enchant an item.";
      this.invalidEnchant = ee.getPrefix() + "§4That is not a valid enchantment name.";
      this.noPermission = ee.getPrefix() + "§4You do not have access to that command.";
   }

   public void performAction(Player player, EndlessEnchantCommand.EnchantAction act, String[] args) {
      ItemStack item = player.getInventory().getItemInMainHand();
      if (args.length != 0) {
         String message = this.ee.getPrefix() + "§6The enchantment";
         if (act != EndlessEnchantCommand.EnchantAction.ADD && act != EndlessEnchantCommand.EnchantAction.IMPLICIT_ADD) {
            if (args.length < 2) {
               return;
            }

            if (this.equalsAny(args[1], "Glow", "Glowing")) {
               this.ee.removeGlow(item);
               player.sendMessage(this.ee.getPrefix() + "§cGlow and all enchants §6have been removed from your item in hand.");
               return;
            }

            if (this.equalsAny(args[1], "*", "All")) {
               if (item.getType() != Material.ENCHANTED_BOOK) {
                  this.ee.removeGlow(item);
               } else {
                  item.setType(Material.BOOK);
               }

               player.sendMessage(this.ee.getPrefix() + "§cAll enchants §6have been removed from your item in hand.");
               return;
            }

            if (!this.ee.isValidEnch(args[1])) {
               player.sendMessage(this.invalidEnchant);
               return;
            }

            String formal = this.ee.getBaseName(args[1]);
            if (EndlessEnchantment.getByName(formal) == null) {
               Kit k = this.ee.getKit(formal);
               if (!player.hasPermission("EndlessEnchant.Kits." + k.getName()) && !player.hasPermission("EndlessEnchant.Kits.*")) {
                  player.sendMessage(this.ee.getPrefix() + "§4Sorry, but you do not have the permissions for that kit!");
                  return;
               }

               message = message + "s §c";
               int t = k.getEndlessEnchantments().size();

               for(Iterator var19 = k.getEndlessEnchantments().iterator(); var19.hasNext(); --t) {
                  EndlessEnchantment e = (EndlessEnchantment)var19.next();
                  if (t == 1) {
                     message = message + "and " + this.ee.getLowerName(e.name()) + " §6have been removed from your item in hand.";
                  }

                  if (t > 1) {
                     message = message + this.ee.getLowerName(e.name()) + ", ";
                  }

                  this.ee.removeEnchantment(item, Enchantment.getByName(e.name()));
               }
            } else {
               this.ee.removeEnchantment(item, Enchantment.getByName(formal));
               message = message + " §c" + this.ee.getLowerName(formal) + " §6has been removed from your item.";
            }
         } else {
            boolean endless = player.hasPermission("EndlessEnchant.Endless");
            int offset = act == EndlessEnchantCommand.EnchantAction.ADD ? 1 : 0;
            if (args.length == 1 + offset) {
               if (this.equalsAny(args[0 + offset], "Glow", "Glowing")) {
                  this.ee.addGlow(item);
                  player.sendMessage(this.ee.getPrefix() + "§cGlow §6has been applied to your item in hand.");
                  return;
               }

               player.sendMessage(this.invalidLevel);
               return;
            }

            if (args.length > 1 + offset) {
               String enchName = args[0 + offset];
               if (this.equalsAny(enchName, "Glow", "Glowing")) {
                  this.ee.addGlow(item);
                  player.sendMessage(this.ee.getPrefix() + "§cGlow §6has been applied to your item in hand.");
                  return;
               }

               if (!this.ee.isValidEnch(enchName)) {
                  player.sendMessage(this.invalidEnchant);
                  return;
               }

               String formal = this.ee.getBaseName(enchName);
               int level = this.ee.getNumber(args[1 + offset]);
               if (EndlessEnchantment.getByName(formal) == null) {
                  Kit k = this.ee.getKit(formal);
                  if (!player.hasPermission("EndlessEnchant.ee.Kits." + k.getName()) && !player.hasPermission("EndlessEnchant.Kits.*")) {
                     player.sendMessage(this.ee.getPrefix() + "§4Sorry, but you do not have the permissions for that kit!");
                     return;
                  }

                  if (level < 0) {
                     player.sendMessage(this.ee.getPrefix() + "§4Level cannot be negative, setting level to 1.");
                     level = 1;
                  }

                  if (level > 32767) {
                     player.sendMessage(this.ee.getPrefix() + "§4Level too high, setting level to " + 32767 + ".");
                     level = 32767;
                  }

                  message = message + "s §c";
                  int t = k.getEndlessEnchantments().size();

                  for(Iterator var14 = k.getEndlessEnchantments().iterator(); var14.hasNext(); --t) {
                     EndlessEnchantment e = (EndlessEnchantment)var14.next();
                     if (t == 1) {
                        message = message + "and " + this.ee.getLowerName(e.name()) + " §6have been applied to your item in hand" + (!endless && level > this.ee.getHighest() ? ", but due to a limitation, you are only allowed up to level " + this.ee.getHighest() : "") + ".";
                     }

                     if (t > 1) {
                        message = message + this.ee.getLowerName(e.name()) + ", ";
                     }

                     this.ee.addEnchantment(item, Enchantment.getByName(e.name()), level, endless);
                  }
               } else {
                  if (level < 0) {
                     player.sendMessage(this.ee.getPrefix() + "§4Level cannot be negative, setting level to 1.");
                     level = 1;
                  }

                  if (level > 32767) {
                     player.sendMessage(this.ee.getPrefix() + "§4Level too high, setting level to " + 32767 + ".");
                     level = 32767;
                  }

                  message = message + " §c" + this.ee.getLowerName(formal) + " §6has been applied to your item in hand" + (!endless && level > this.ee.getHighest() ? ", but due to a limitation, you are only allowed up to level " + this.ee.getHighest() : "") + ".";
                  this.ee.addEnchantment(item, Enchantment.getByName(formal), level, endless);
               }
            }
         }

         player.sendMessage(message);
      }
   }

   public void parseCommand(Player player, String[] args) {
      boolean ePerm = player.hasPermission("EndlessEnchant.Enchant") || player.hasPermission("EndlessEnchant.Enchant.*");
      if (!ePerm) {
         player.sendMessage(this.noPermission);
      } else if (args.length >= 1) {
         ItemStack item = player.getInventory().getItemInMainHand();
         if (item != null && item.getType() != Material.AIR) {
            boolean add = this.equalsAny(args[0], "Add", "Enchant");
            boolean remove = this.equalsAny(args[0], "Remove", "Disenchant");
            boolean implicitAdd = !add && !remove;
            if (implicitAdd) {
               this.performAction(player, EndlessEnchantCommand.EnchantAction.IMPLICIT_ADD, args);
            } else if (args.length == 1) {
               player.sendMessage(this.ee.getPrefix() + "§4An enchantment name " + (add ? "and a level §6(0-32767)§4 " : "") + "is required to " + (remove ? "dis" : "") + "enchant an item.");
            } else {
               if (args.length >= 2) {
                  if (remove) {
                     this.performAction(player, EndlessEnchantCommand.EnchantAction.REMOVE, args);
                  } else {
                     if (!add) {
                        return;
                     }

                     this.performAction(player, EndlessEnchantCommand.EnchantAction.ADD, args);
                  }
               }

            }
         } else {
            player.sendMessage(this.ee.getPrefix() + "§4You must have an item in your hand to begin enchanting.");
         }
      }
   }

   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
      if (!cmd.getName().equalsIgnoreCase("EndlessEnchant")) {
         return false;
      } else if (!(sender instanceof Player)) {
         sender.getServer().getConsoleSender().sendMessage("§cYou are unable to use this command.");
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length == 0 || args.length >= 1 && args[0].equalsIgnoreCase("Help")) {
            if (!player.hasPermission("EndlessEnchant.Help")) {
               player.sendMessage(this.noPermission);
               return true;
            } else {
               player.sendMessage("§8==================" + this.ee.getPrefix().replace(" ", "") + "§8==================");
               player.sendMessage("§4§4To see the usage of /EE, type §6/EE Usage§4.");
               player.sendMessage("§4To see a list of Enchantments §c(Non-Aliased)§4, type §b/EE Enchantments§4.");
               player.sendMessage("§4To get the aliases of a certain enchantment, type §2/EE Alias <Enchantment>§4.");
               player.sendMessage("§4To see a list of Enchantment Kits, type §c/EE Kits§4.");
               player.sendMessage("§8=====================================================");
               return true;
            }
         } else {
            if (this.equalsAny(args[0], "Usage", "?", "/")) {
               if (!player.hasPermission("EndlessEnchant.Usage")) {
                  player.sendMessage(this.noPermission);
                  return true;
               }

               player.sendMessage(this.ee.getPrefix() + "§4Usage:§6 /EE §3[Add/Remove] §a[Enchantment] §2{Level} (not needed if removing)§6.");
            } else {
               String temp;
               EndlessEnchantment e;
               if (this.equalsAny(args[0], "Enchants", "Enchantment", "Enchantments", "List")) {
                  if (!player.hasPermission("EndlessEnchant.Enchantments")) {
                     player.sendMessage(this.noPermission);
                     return true;
                  }

                  temp = "";
                  String lastColor = "";
                  int index = EndlessEnchantment.values().length;
                  EndlessEnchantment[] var12;
                  int var11 = (var12 = EndlessEnchantment.values()).length;

                  for(int var10 = 0; var10 < var11; ++var10) {
                     e = var12[var10];
                     if (!e.getCategoryColor().equals(lastColor)) {
                        lastColor = e.getCategoryColor();
                        temp = temp + "§" + lastColor;
                     }

                     if (index == 1) {
                        temp = temp + e.name() + ".";
                        break;
                     }

                     temp = temp + e.name() + ", ";
                     if (index == 2) {
                        temp = temp + "AND ";
                     }

                     --index;
                  }

                  player.sendMessage(this.ee.getPrefix() + temp);
               } else if (this.equalsAny(args[0], "Alias", "Aliases")) {
                  if (!player.hasPermission("EndlessEnchant.Aliases")) {
                     player.sendMessage(this.noPermission);
                     return true;
                  }

                  if (args.length == 1) {
                     player.sendMessage(this.ee.getPrefix() + "§4An enchantment name to get the alias of is required.");
                     return true;
                  }

                  if (args.length > 1) {
                     if (this.ee.isValidEnch(args[1])) {
                        temp = this.ee.getBaseName(args[1]);
                        player.sendMessage(this.ee.getPrefix() + "§3" + temp + " Aliases: " + (((List)this.ee.getEnchMap().get(temp)).size() == 0 ? "[None]" : this.list("AND", (List)this.ee.getEnchMap().get(temp))) + ".");
                     } else {
                        player.sendMessage(this.ee.getPrefix() + "§4That enchantment name is not valid.");
                     }
                  }
               } else if (args[0].equalsIgnoreCase("Kits")) {
                  if (!player.hasPermission("EndlessEnchant.Kits") && !player.hasPermission("EndlessEnchant.Kits.List")) {
                     player.sendMessage(this.noPermission);
                     return true;
                  }

                  if (this.ee.getKits().size() == 0) {
                     player.sendMessage(this.ee.getPrefix() + "§4There are no defined enchantment kits; verify that you've configured them properly!");
                     return true;
                  }

                  player.sendMessage("§8==================" + this.ee.getPrefix().trim() + "§8==================");
                  Iterator var13 = this.ee.getKits().iterator();

                  while(var13.hasNext()) {
                     Kit k = (Kit)var13.next();
                     List<String> enchants = new ArrayList();
                     Iterator var16 = k.getEndlessEnchantments().iterator();

                     while(var16.hasNext()) {
                        e = (EndlessEnchantment)var16.next();
                        enchants.add(e.name());
                     }

                     player.sendMessage(k.getFormat().replaceAll("(?i)(&([0-9A-FK-OR]))", "§$2") + ": " + k.getSuffix() + this.list("AND", enchants) + ".");
                  }

                  player.sendMessage("§c§lPlease note: These kit names are enchantment names!");
                  player.sendMessage("§8=====================================================");
               } else {
                  this.parseCommand(player, args);
               }
            }

            return true;
         }
      }
   }

   public boolean equalsAny(String base, String... comps) {
      String[] var6 = comps;
      int var5 = comps.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         String comp = var6[var4];
         if (comp.equalsIgnoreCase(base)) {
            return true;
         }
      }

      return false;
   }

   public String list(String conjunction, List<String> args) {
      String plural = "";
      if (args.size() == 0) {
         return "";
      } else {
         List<String> temp = new ArrayList();
         Iterator var6 = args.iterator();

         while(var6.hasNext()) {
            String s = (String)var6.next();
            if (!s.trim().equals("")) {
               temp.add(s);
            }
         }

         if (temp.size() == 1) {
            plural = plural + (String)temp.get(0);
         } else if (temp.size() == 2) {
            plural = plural + (String)temp.get(0) + " " + conjunction + " " + (String)temp.get(1);
         } else if (temp.size() > 2) {
            for(int i = 0; i < temp.size(); ++i) {
               if (i == temp.size() - 2) {
                  plural = plural + (String)temp.get(i) + ", " + conjunction + " " + (String)temp.get(i + 1);
                  break;
               }

               plural = plural + (String)temp.get(i) + ", ";
            }
         }

         return plural;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
      if (!cmd.getName().equalsIgnoreCase("EndlessEnchant")) {
         return Arrays.asList("");
      } else if (!(sender instanceof Player)) {
         return Arrays.asList("");
      } else {
         ArrayList tab;
         if (args.length == 1) {
            tab = new ArrayList(Arrays.asList("Add", "Alias", "Enchantments", "Help", "Kits", "Remove", "Usage"));

            for(int i = 0; i < tab.size(); ++i) {
               if (!((String)tab.get(i)).toUpperCase().startsWith(args[0].toUpperCase())) {
                  tab.remove(i);
                  --i;
               }
            }

            Collections.sort(tab);
            return tab;
         } else if (args.length == 2) {
            if (this.equalsAny(args[0], "Enchants", "Enchantment", "Enchantments", "List", "Help", "Usage", "/", "?", "Kits")) {
               return Arrays.asList("");
            } else {
               tab = new ArrayList();
               Iterator var7 = this.ee.getEnchMap().keySet().iterator();

               while(var7.hasNext()) {
                  String s = (String)var7.next();
                  if (s.toUpperCase().startsWith(args[1].toUpperCase())) {
                     tab.add(s);
                  }
               }

               Collections.sort(tab);
               return tab;
            }
         } else if (args.length == 3) {
            return this.equalsAny(args[0], "Enchants", "Enchantment", "Enchantments", "List", "Help", "Usage", "/", "?", "Kits", "Remove", "Disenchant") ? Arrays.asList("") : Arrays.asList("" + this.ee.getHighest());
         } else {
            return Arrays.asList("");
         }
      }
   }

   static enum EnchantAction {
      ADD,
      IMPLICIT_ADD,
      REMOVE;
   }
}
