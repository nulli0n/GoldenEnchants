package su.nightexpress.goldenenchants.manager.enchants.combat;

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
import su.nexmedia.engine.utils.eval.Evaluator;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantVampire extends IEnchantChanceTemplate implements CombatEnchant {
	
	private String enchantParticle;
	private String healMod;
	
	public EnchantVampire(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		String path = "settings.";
		this.enchantParticle = cfg.getString(path + "enchant-particle-effect", Particle.HEART.name());
		this.healMod = cfg.getString(path + "heal-modifier", "%damage% * 0.2");
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

	@Override
	public void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		AttributeInstance ai = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (ai == null) return;
		
		String expression = this.healMod
				.replace("%level%", String.valueOf(lvl))
				.replace("%damage%", NumberUT.format(e.getDamage()));
		
		double healMod = Evaluator.eval(expression, 1);
		double healMax = NumberUT.round(ai.getValue());
		
		damager.setHealth(Math.min(healMax, damager.getHealth() + healMod));
		
		EffectUT.playEffect(damager.getEyeLocation(), this.enchantParticle, 0.2f, 0.15f, 0.2f, 0.15f, 5);
	}
}
