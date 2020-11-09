package su.nightexpress.goldenenchants.manager.enchants.combat;

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

public class EnchantExpHunter extends IEnchantChanceTemplate implements DeathEnchant {
	
	private TreeMap<Integer, Double> expMod;
	
	public EnchantExpHunter(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.expMod = new TreeMap<>();
		
		this.loadMapValues(this.expMod, "settings.exp-modifier");
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isSword(item);
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
	}
	
	@Override
	public boolean isCursed() {
		return false;
	}
	
	@Override
	public boolean isTreasure() {
		return false;
	}

	public final double getExpModifier(int lvl) {
		Map.Entry<Integer, Double> e = this.expMod.floorEntry(lvl);
		return e != null ? e.getValue() : (1D + lvl / 10D);
	}
	
	@Override
	public void use(@NotNull LivingEntity dead, @NotNull EntityDeathEvent e, int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		double mod = this.getExpModifier(lvl);
		double exp = e.getDroppedExp() * mod;
		
		e.setDroppedExp((int) Math.ceil(exp));
	}
}
