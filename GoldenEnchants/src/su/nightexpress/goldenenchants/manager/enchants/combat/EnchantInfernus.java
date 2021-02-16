package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;

public class EnchantInfernus extends IEnchantChanceTemplate {

	private TreeMap<Integer, Double> fireTicks;
	
	public EnchantInfernus(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.fireTicks = new TreeMap<>();
		this.loadMapValues(this.fireTicks, "settings.fire-ticks");
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return item.getType() == Material.TRIDENT;
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TRIDENT;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInfernusTridentLaunch(ProjectileLaunchEvent e) {
		Entity entity = e.getEntity();
		if (!(entity instanceof Trident)) return;
		
		Trident trident = (Trident) entity;
		ItemStack item = trident.getItem();
		
		int lvl = item.getEnchantmentLevel(this);
		if (lvl <= 0) return;
		
		if (!this.checkTriggerChance(lvl)) return;
		trident.setFireTicks(Integer.MAX_VALUE);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInfernusDamageApply(EntityDamageByEntityEvent e) {
		Entity entity = e.getDamager();
		if (!(entity instanceof Trident)) return;
		
		Trident trident = (Trident) entity;
		ItemStack item = trident.getItem();
		
		int lvl = item.getEnchantmentLevel(this);
		if (lvl <= 0 || trident.getFireTicks() <= 0) return;
		
		double ticks = this.getMapValue(this.fireTicks, lvl, 60);
		e.getEntity().setFireTicks((int) ticks);
	}
}
