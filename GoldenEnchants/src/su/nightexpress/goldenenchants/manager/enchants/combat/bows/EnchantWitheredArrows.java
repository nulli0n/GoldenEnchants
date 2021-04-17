package su.nightexpress.goldenenchants.manager.enchants.combat.bows;

import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantPotionTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BowEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;
import su.nightexpress.goldenenchants.manager.tasks.ArrowTrailsTask;

public class EnchantWitheredArrows extends IEnchantPotionTemplate implements BowEnchant, CombatEnchant {
	
	private String arrowTrail;
	
	private static final String ARROW_META = "GOLDEN_ENCHANTS_WITHERED_ARROW_META";
	
	public EnchantWitheredArrows(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(PotionEffectType.WITHER, plugin, cfg);
		
		this.arrowTrail = cfg.getString("settings.arrow-trail", Particle.SMOKE_NORMAL.name());
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isBow(item);
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BOW;
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
	public void use(@NotNull EntityDamageByEntityEvent e, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull ItemStack weapon, int lvl) {
		
		if (!e.getDamager().hasMetadata(ARROW_META)) return;
		
		this.addEffect(victim, lvl);
	}

	@Override
	public void use(@NotNull EntityShootBowEvent e, @NotNull LivingEntity shooter,
			@NotNull ItemStack bow, int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		Entity p = e.getProjectile();
		if (!(p instanceof Projectile)) return;
		
		ArrowTrailsTask.add((Projectile) e.getProjectile(), this.arrowTrail);
		p.setMetadata(ARROW_META, new FixedMetadataValue(plugin, "true"));
	}
}
