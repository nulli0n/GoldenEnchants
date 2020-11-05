package su.nightexpress.goldenenchants.manager.enchants.combat.bows;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.fogus.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BowEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.LocationEnchant;
import su.nightexpress.goldenenchants.manager.tasks.ArrowTrailsTask;

public class EnchantExplosiveArrows extends IEnchantChanceTemplate implements LocationEnchant, BowEnchant, CombatEnchant {
	
	private String arrowTrail;
	private TreeMap<Integer, Double> explosionSize;
	
	private static final String ARROW_META = "GOLDEN_ENCHANTS_EXPLOSIVE_ARROW_META";
	
	public EnchantExplosiveArrows(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.explosionSize = new TreeMap<>();
		this.arrowTrail = cfg.getString("settings.arrow-trail", Particle.SMOKE_NORMAL.name());
		
		this.loadMapValues(this.explosionSize, "settings.explosion-size");
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
	
	public final double getExplosionSize(int lvl) {
		Map.Entry<Integer, Double> e = this.explosionSize.floorEntry(lvl);
		return e != null ? e.getValue() : 2D + (double) lvl;
	}
	
	@Override
	public void use(@NotNull ItemStack wpn, @Nullable Entity e, @NotNull Location loc, int lvl) {
		if (e == null || !e.hasMetadata(ARROW_META)) return;
		
		World w = e.getWorld();
		w.createExplosion(loc, (float) this.getExplosionSize(lvl), true, false);
	}

	@Override
	public void use(@NotNull ItemStack bow, @NotNull LivingEntity shooter, @NotNull EntityShootBowEvent e, int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		Entity pj = e.getProjectile();
		if (!(pj instanceof Projectile)) return;
		
		ArrowTrailsTask.add((Projectile) pj, this.arrowTrail);
		pj.setMetadata(ARROW_META, new FixedMetadataValue(plugin, "true"));
	}

	@Override
	public void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl) {
		if (!e.getDamager().hasMetadata(ARROW_META)) return;
		
		World w = victim.getWorld();
		w.createExplosion(victim.getLocation(), (float) this.getExplosionSize(lvl), true, false);
	}
}
