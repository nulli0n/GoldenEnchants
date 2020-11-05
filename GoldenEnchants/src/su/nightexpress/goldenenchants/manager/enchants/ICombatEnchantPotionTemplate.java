package su.nightexpress.goldenenchants.manager.enchants;

import org.bukkit.Particle;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import su.fogus.engine.config.api.JYML;
import su.fogus.engine.utils.LocUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public abstract class ICombatEnchantPotionTemplate extends IEnchantPotionTemplate implements CombatEnchant {

	protected String optParticleName;
	
	public ICombatEnchantPotionTemplate(
			@NotNull PotionEffectType effectType,
			@NotNull GoldenEnchants plugin,
			@NotNull JYML cfg) {
		super(effectType, plugin, cfg);
		
		String path = "settings.";
		this.optParticleName = cfg.getString(path + "particle-effect", Particle.SMOKE_NORMAL.name());
	}
	
	@Override
	public final boolean canEnchant(@NotNull ItemStack item) {
		return this.isSword(item);
	}
	
	@Override
	@NotNull
	public final EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
	}
	
	@Override
	public void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		int bTime = this.getEffectDuration(lvl);
		int bLvl = this.getEffectLevel(lvl);
		
		PotionEffect pet = new PotionEffect(this.effectType, bTime, bLvl);
		this.addPotionEffect(victim, pet, true);
		LocUT.playEffect(victim.getEyeLocation(), this.optParticleName, 0.2f, 0.15f, 0.2f, 0.1f, 40);
	}
}
