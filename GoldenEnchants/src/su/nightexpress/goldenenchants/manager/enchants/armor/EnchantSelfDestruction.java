package su.nightexpress.goldenenchants.manager.enchants.armor;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.DeathEnchant;

public class EnchantSelfDestruction extends IEnchantChanceTemplate implements DeathEnchant {
	
	private TreeMap<Integer, Double> explosionSize;
	
	public EnchantSelfDestruction(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.explosionSize = new TreeMap<>();
		this.loadMapValues(this.explosionSize, "settings.explosion-size");
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_CHESTPLATES.contains(item.getType());
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_TORSO;
	}
	
	@Override
	public boolean isCursed() {
		return false;
	}
	
	@Override
	public boolean isTreasure() {
		return false;
	}

	public final double getExplosionSize(int lvl) {
		Map.Entry<Integer, Double> e = this.explosionSize.floorEntry(lvl);
		return e != null ? e.getValue() : 2D + lvl;
	}
	
	@Override
	public void use(@NotNull LivingEntity dead, @NotNull EntityDeathEvent e, int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		double size = this.getExplosionSize(lvl);
		dead.getWorld().createExplosion(dead.getLocation(), (float) size, false, false);
	}
}
