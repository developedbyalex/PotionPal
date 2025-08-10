package me.quartz.potionpal.commands;

import me.quartz.potionpal.PotionPal;
import me.quartz.potionpal.utils.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PotionPalCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.hasPermission("potionpal.list") || commandSender.hasPermission("potionpal.give") || commandSender.hasPermission("potionpal.reload")) {
            if(strings.length > 0){
                if(strings[0].equalsIgnoreCase("reload")) {
                    if(commandSender.hasPermission("potionpal.reload")) {
                        PotionPal.getInstance().reloadConfig();
                        PotionPal.getInstance().getTask().reload();
                        commandSender.sendMessage(ChatColor.GREEN + "Config has been reloaded.");
                    } else commandSender.sendMessage(ChatColor.RED + "No permissions!");
                } else if(strings[0].equalsIgnoreCase("list")) {
                    if(commandSender.hasPermission("potionpal.list")) {
                        commandSender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------");
                        if(!PotionPal.getInstance().getConfig().getConfigurationSection("items").getKeys(false).isEmpty()) {
                            for (String potions : PotionPal.getInstance().getConfig().getConfigurationSection("items").getKeys(false)) {
                                ConfigurationSection cs = PotionPal.getInstance().getConfig().getConfigurationSection("items." + potions);
                                commandSender.sendMessage(potions + ChatColor.GRAY + " - " + ChatColor.YELLOW + cs.getString("item"));
                            }
                        } else commandSender.sendMessage(ChatColor.RED + "No potions found!");
                        commandSender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------");
                    } else commandSender.sendMessage(ChatColor.RED + "No permissions!");
                } else if(strings[0].equalsIgnoreCase("give")) {
                    if(commandSender.hasPermission("potionpal.give")) {
                        if(strings.length > 2) {
                            Player player = Bukkit.getPlayer(strings[1]);
                            if(player != null) {
                                ConfigurationSection cs = PotionPal.getInstance().getConfig().getConfigurationSection("items." + strings[2].toLowerCase());
                                if(cs != null) {
                                    ItemStack itemStack = new ItemStack(Material.valueOf(cs.getString("item")));
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    itemMeta.setDisplayName(
                                            MessageHelper.format(cs.getString("display_name")));
                                    itemMeta.setLore(cs.getStringList("lore").stream().map(
                                            MessageHelper::format).collect(Collectors.toList()));
                                    itemStack.setItemMeta(itemMeta);
                                    player.getInventory().addItem(itemStack);

                                    commandSender.sendMessage(
                                            MessageHelper.format(PotionPal.getInstance().getConfig().getString("prefix")) + ChatColor.GREEN + "Item has been given!");
                                    player.sendMessage(
                                            MessageHelper.format(PotionPal.getInstance().getConfig().getString("prefix")) + ChatColor.GREEN + "You received " + MessageHelper.format(cs.getString("display_name")) + ChatColor.GREEN + "!");
                                } else commandSender.sendMessage(
                                        MessageHelper.format(PotionPal.getInstance().getConfig().getString("prefix")) + ChatColor.RED + "Item not found!");
                            } else commandSender.sendMessage(
                                    MessageHelper.format(PotionPal.getInstance().getConfig().getString("prefix")) + ChatColor.RED + "Player not found!");
                        } else commandSender.sendMessage(ChatColor.RED + "Usage: /potionpal give <player> <item>");
                    } else commandSender.sendMessage(ChatColor.RED + "No permissions!");
                } else {
                    commandSender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------");
                    commandSender.sendMessage(ChatColor.YELLOW + "/potionpal list");
                    commandSender.sendMessage(ChatColor.YELLOW + "/potionpal give <player> <item>");
                    commandSender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------");
                }
            } else {
                commandSender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------");
                commandSender.sendMessage(ChatColor.YELLOW + "/potionpal list");
                commandSender.sendMessage(ChatColor.YELLOW + "/potionpal give <player> <item>");
                commandSender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------");
            }
        } else commandSender.sendMessage(ChatColor.RED + "No permissions!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        List<String> list = new ArrayList<>();
        if(cmd.getName().equalsIgnoreCase("potionpal")){
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length == 1) {
                    if(player.hasPermission("potionpal.list")) list.add("list");
                    if(player.hasPermission("potionpal.give")) list.add("give");
                    if(player.hasPermission("potionpal.reload")) list.add("help");

                    if(player.hasPermission("")) {
                        list.add("reload");
                    }
                } else if (args.length == 2 && args[0].equalsIgnoreCase("give") && player.hasPermission("potionpal.give")) {
                    list = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                } else if (args.length == 3 && args[0].equalsIgnoreCase("give") && player.hasPermission("potionpal.give")) {
                    list = new ArrayList<>(Objects.requireNonNull(PotionPal.getInstance().getConfig().getConfigurationSection("items")).getKeys(false));
                }
            }
        }
        return list;
    }
}
