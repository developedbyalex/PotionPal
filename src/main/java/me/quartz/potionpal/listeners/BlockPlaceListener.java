package me.quartz.potionpal.listeners;

import me.quartz.potionpal.PotionPal;
import me.quartz.potionpal.utils.ColorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(checkItem(player, event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    private boolean checkItem(Player player, ItemStack itemStack) {
        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
        ItemStack offItem = player.getInventory().getItemInOffHand();
        ItemStack mainItem = player.getInventory().getItemInMainHand();
        for (String potions : PotionPal.getInstance().getConfig().getConfigurationSection("items").getKeys(false)) {
            ConfigurationSection cs = PotionPal.getInstance().getConfig().getConfigurationSection("items." + potions);
            if(cs != null) {
                if(cs.getString("type").equalsIgnoreCase("OFFHAND")) {
                    if(itemStack.getType() == offItem.getType() && itemStack.getType() == Material.valueOf(cs.getString("item"))) {
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if(itemMeta != null && ColorUtils.translateColors(itemMeta.getDisplayName()).equalsIgnoreCase(ColorUtils.translateColors(cs.getString("display_name")))) {
                            return true;
                        }
                    }
                } else {
                    if (itemStack.getType() == mainItem.getType() && itemStack.getType() == Material.valueOf(cs.getString("item"))) {
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemMeta != null && ColorUtils.translateColors(itemMeta.getDisplayName()).equalsIgnoreCase(ColorUtils.translateColors(cs.getString("display_name")))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
