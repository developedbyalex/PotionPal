package me.quartz.potionpal.listeners;

import me.quartz.potionpal.PotionPal;
import me.quartz.potionpal.utils.MessageHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(checkItem(player, event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    private boolean checkItem(Player player, ItemStack itemStack) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
        ItemStack offItem = player.getInventory().getItemInOffHand();
        ItemStack mainItem = player.getInventory().getItemInMainHand();
        for (String potions : PotionPal.getInstance().getConfig().getConfigurationSection("items").getKeys(false)) {
            ConfigurationSection cs = PotionPal.getInstance().getConfig().getConfigurationSection("items." + potions);
            if(cs != null) {
                if(cs.getString("type").equalsIgnoreCase("OFFHAND")) {
                    if(itemStack.getType() == offItem.getType() && itemStack.getType() == Material.valueOf(cs.getString("item"))) {
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if(itemMeta != null && MessageHelper.format(itemMeta.getDisplayName()).equalsIgnoreCase(
                                MessageHelper.format(cs.getString("display_name")))) {
                            return true;
                        }
                    }
                } else {
                    if (itemStack.getType() == mainItem.getType() && itemStack.getType() == Material.valueOf(cs.getString("item"))) {
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemMeta != null && MessageHelper.format(itemMeta.getDisplayName()).equalsIgnoreCase(
                                MessageHelper.format(cs.getString("display_name")))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
