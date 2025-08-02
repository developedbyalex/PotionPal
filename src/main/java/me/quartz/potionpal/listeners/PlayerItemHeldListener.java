package me.quartz.potionpal.listeners;

import me.quartz.potionpal.PotionPal;
import me.quartz.potionpal.utils.ColorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
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

public class PlayerItemHeldListener implements Listener {

    private static List<UUID> prevent = new ArrayList<>();

    @EventHandler
    public void playerItemHeldEvent(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        checkItem(player, null, null);
    }

    @EventHandler
    public void playerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        checkItem(player, event.getMainHandItem(), event.getOffHandItem());
    }

    @EventHandler
    public void playerSwapHandItemsEvent(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(PotionPal.getInstance(), () -> {
                Player player = (Player) event.getWhoClicked();
                checkItem(player, null, null);
            }, 5L);
        }
    }

    private void checkItem(Player player, ItemStack mainItemB, ItemStack offItemB) {
        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
        ItemStack offItem = offItemB != null ? offItemB : player.getInventory().getItemInOffHand();
        ItemStack mainItem = mainItemB != null ? mainItemB : player.getInventory().getItemInMainHand();
        for (String potions : PotionPal.getInstance().getConfig().getConfigurationSection("items").getKeys(false)) {
            ConfigurationSection cs = PotionPal.getInstance().getConfig().getConfigurationSection("items." + potions);
            if(cs != null) {
                if(cs.getString("type").equalsIgnoreCase("OFFHAND")) {
                    if(offItem.getType() == Material.valueOf(cs.getString("item"))) {
                        ItemMeta itemMeta = offItem.getItemMeta();
                        if(itemMeta != null && ColorUtils.translateColors(itemMeta.getDisplayName()).equalsIgnoreCase(ColorUtils.translateColors(cs.getString("display_name")))) {
                            List<String> pe = cs.getStringList("potion_effects");
                            for(String s : pe) {
                                PotionEffectType pet = PotionEffectType.getByName(s.split(":")[0]);
                                if(pet.getName().equals(PotionEffectType.HEALTH_BOOST.getName())) {
                                    player.addPotionEffect(new PotionEffect(pet, -1, Integer.parseInt(s.split(":")[1]) -1));
                                } else player.addPotionEffect(new PotionEffect(pet, -1, Integer.parseInt(s.split(":")[1]) -1));
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    ItemStack offItem = player.getInventory().getItemInOffHand();
                                    if (offItem.getType() == Material.valueOf(cs.getString("item"))) {
                                        ItemMeta itemMeta = offItem.getItemMeta();
                                        if (itemMeta != null && ColorUtils.translateColors(itemMeta.getDisplayName()).equalsIgnoreCase(ColorUtils.translateColors(cs.getString("display_name")))) {
                                            if (cs.getBoolean("action_bar.enabled")) {
                                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorUtils.translateColors(cs.getString("action_bar.message"))));
                                            }
                                            if (cs.getBoolean("particles.enabled")) {
                                                player.spawnParticle(Particle.valueOf(cs.getString("particles.particle")), player.getLocation(), cs.getInt("particles.amount"));
                                            }
                                        } else cancel();
                                    } else cancel();
                                }
                            }.runTaskTimer(PotionPal.getInstance(), 0, 20);
                        }
                    }
                } else {
                    if (mainItem.getType() == Material.valueOf(cs.getString("item"))) {
                        ItemMeta itemMeta = mainItem.getItemMeta();
                        if (itemMeta != null && ColorUtils.translateColors(itemMeta.getDisplayName()).equalsIgnoreCase(ColorUtils.translateColors(cs.getString("display_name")))) {
                            List<String> pe = cs.getStringList("potion_effects");
                            for(String s : pe) {
                                PotionEffectType pet = PotionEffectType.getByName(s.split(":")[0]);
                                if(pet.getName().equals(PotionEffectType.HEALTH_BOOST.getName())) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, -1, Integer.parseInt(s.split(":")[1]) -1));
                                } else player.addPotionEffect(new PotionEffect(pet, -1, Integer.parseInt(s.split(":")[1]) -1));
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    ItemStack offItem = player.getInventory().getItemInMainHand();
                                    if (offItem.getType() == Material.valueOf(cs.getString("item"))) {
                                        ItemMeta itemMeta = offItem.getItemMeta();
                                        if (itemMeta != null && ColorUtils.translateColors(itemMeta.getDisplayName()).equalsIgnoreCase(ColorUtils.translateColors(cs.getString("display_name")))) {
                                            if (cs.getBoolean("action_bar.enabled")) {
                                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorUtils.translateColors(cs.getString("action_bar.message"))));
                                            }                                                player.spawnParticle(Particle.valueOf(cs.getString("particles.particle")), player.getLocation(), cs.getInt("particles.amount"));

                                            if (cs.getBoolean("particles.enabled")) {
                                                player.spawnParticle(Particle.valueOf(cs.getString("particles.particle")), player.getLocation(), cs.getInt("particles.amount"));
                                            }
                                        } else cancel();
                                    } else cancel();
                                }
                            }.runTaskTimer(PotionPal.getInstance(), 0, 20);
                        }
                    }
                }
            }
        }
    }
}
