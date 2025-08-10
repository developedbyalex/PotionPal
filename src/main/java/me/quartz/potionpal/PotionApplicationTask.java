package me.quartz.potionpal;

import me.quartz.potionpal.utils.MessageHelper;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class PotionApplicationTask {
	private int potionDuration;
	private int taskId = -1;
	private int particlesMaxCounter, actionbarMaxCounter;
	private int particlesCounter, actionbarCounter;
	private final Set<String> warnedAbout = new HashSet<>();
	public PotionApplicationTask() {
		this.reload();
	}

	public void reload(){
		this.potionDuration =
				PotionPal.getInstance().getConfig().getInt(
						"potion-duration", 1);
		this.particlesMaxCounter =
				PotionPal.getInstance().getConfig().getInt(
						"particles-counter", 1);
		this.actionbarMaxCounter =
				PotionPal.getInstance().getConfig().getInt(
						"actionbar-counter", 1);
		if(this.taskId != -1){
			Bukkit.getServer().getScheduler().cancelTask(this.taskId);
		}
		this.taskId =
				Bukkit.getServer().getScheduler().runTaskTimer(PotionPal.getInstance(), this::tick, this.potionDuration, this.potionDuration).getTaskId();
	}

	public void tick(){
		for(Player player : Bukkit.getOnlinePlayers()){
			this.checkItem(player);
		}
		if(this.particlesCounter == particlesMaxCounter){
			this.particlesCounter = 0;
		}
		if(this.actionbarCounter == actionbarMaxCounter){
			this.actionbarCounter = 0;
		}
		this.actionbarCounter++;
		this.particlesCounter++;
	}

	private void checkItem(Player player) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
		ItemStack offItem = player.getInventory().getItemInOffHand();
		ItemStack mainItem = player.getInventory().getItemInMainHand();
		ConfigurationSection itemsSec =
				PotionPal.getInstance().getConfig().getConfigurationSection("items");
		if(itemsSec == null) return;
		for (String potions : itemsSec.getKeys(false)) {
			ConfigurationSection cs =
					itemsSec.getConfigurationSection(potions);
			if(cs != null) {
				if("OFFHAND".equalsIgnoreCase(cs.getString("type"))) {
					handle(player, cs, offItem);
				} else {
					handle(player, cs, mainItem);
				}
			}
		}
	}
	private void handle(Player player,
	                    ConfigurationSection cs,
	                    ItemStack item){
		if(item.getType() == Material.valueOf(cs.getString(
				"item"))) {
			ItemMeta itemMeta = item.getItemMeta();
			if(itemMeta != null && MessageHelper.format(itemMeta.getDisplayName()).equalsIgnoreCase(
					MessageHelper.format(cs.getString("display_name")))) {
				List<String>
						pe = cs.getStringList("potion_effects");
				for(String s : pe) {
					String[] effectArr = s.split(":");
					String potionType = effectArr[0];
					String ampStr = effectArr[1];
					PotionEffectType pet =
							PotionEffectType.getByName(potionType);
					if(pet == null){
						if(warnedAbout.add(potionType)){
							PotionPal.getInstance().getLogger().log(
									Level.WARNING, "{0} is " +
									               "not" +
									               " a " +
									               "valid potion type, please change it in config of PotionPal.", potionType);
						}
						continue;
					}
					int amp =
							Integer.parseInt(ampStr) -1;
					int activeTicks =
							player.getActivePotionEffects().stream().filter((eff) -> eff.getType() == pet && eff.getAmplifier() == amp).findFirst().map(PotionEffect::getDuration).orElse(1);
					player.addPotionEffect(new PotionEffect(pet, activeTicks + potionDuration, amp));
				}
				if(this.particlesCounter == particlesMaxCounter){
					if (cs.getBoolean("particles.enabled")) {
						player.spawnParticle(
								Particle.valueOf(cs.getString("particles.particle")), player.getLocation(), cs.getInt("particles.amount"));
					}
				}
				if(this.actionbarCounter == actionbarMaxCounter){
					if (cs.getBoolean("action_bar.enabled")) {
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
								MessageHelper.format(cs.getString("action_bar.message"))));
					}
				}
			}
		}
	}
}
