package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.NumberUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantCriticals extends IEnchantChanceTemplate implements CombatEnchant {

	private TreeMap<Integer, Double> damageModifier;
	
	public EnchantCriticals(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.damageModifier = new TreeMap<>();
		
		this.loadMapValues(this.damageModifier, "settings.damage-modifier");
	}
	
	private double getDamageModifier(int lvl) {
		Map.Entry<Integer, Double> e = this.damageModifier.floorEntry(lvl);
		return e != null ? e.getValue() : 1D;
	}

	@Override
	public void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl) {
		
		if (damager.getFallDistance() <= 0 || damager.isOnGround()) return;
		if (!this.checkTriggerChance(lvl)) return;
		
		e.setDamage(e.getDamage() * this.getDamageModifier(lvl));
	}

	@Override
	@NotNull
	public String getDescription(int lvl) {
		return super.getDescription(lvl)
				.replace("%damage%", NumberUT.format(this.getMapValue(this.damageModifier, lvl, 0)));
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
}
