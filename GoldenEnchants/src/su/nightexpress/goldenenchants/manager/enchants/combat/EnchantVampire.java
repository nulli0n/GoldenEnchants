package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.TreeMap;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.NumberUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantVampire extends IEnchantChanceTemplate implements CombatEnchant {
	
	private String enchantParticle;
	private TreeMap<Integer, Double> healMod;
	
	public EnchantVampire(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		String path = "settings.";
		this.enchantParticle = cfg.getString(path + "enchant-particle-effect", Particle.HEART.name());
		this.healMod = new TreeMap<>();
		this.loadMapValues(this.healMod, path + "damage-heal-modifier");
	}
	
	@Override
	public void use(@NotNull EntityDamageByEntityEvent e, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull ItemStack weapon, int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		AttributeInstance ai = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (ai == null) return;
		
		double healMod = e.getDamage() * this.getMapValue(this.healMod, lvl, 0.2);
		double healMax = NumberUT.round(ai.getValue());
		
		damager.setHealth(Math.min(healMax, damager.getHealth() + healMod));
		
		EffectUT.playEffect(damager.getEyeLocation(), this.enchantParticle, 0.2f, 0.15f, 0.2f, 0.15f, 5);
	}

	@Override
	@NotNull
	public String getDescription(int lvl) {
		return super.getDescription(lvl)
				.replace("%modifier%", NumberUT.format(this.getMapValue(this.healMod, lvl, 0.2) * 100D));
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isSword(item);
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
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
