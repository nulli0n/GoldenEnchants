package su.nightexpress.goldenenchants.manager.enchants;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.NumberUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.api.PassiveEnchant;

public abstract class IEnchantPotionTemplate extends IEnchantChanceTemplate {

	protected PotionEffectType effectType;
	protected TreeMap<Integer, Double> effectDuration;
	protected TreeMap<Integer, Double> effectLevel;
	protected final boolean isParticles;
	
	public IEnchantPotionTemplate(
			@NotNull PotionEffectType effectType,
			@NotNull GoldenEnchants plugin,
			@NotNull JYML cfg) {
		super(plugin, cfg);
		this.effectType = effectType;
		this.effectDuration = new TreeMap<>();
		this.effectLevel = new TreeMap<>();
		this.isParticles = !(this instanceof PassiveEnchant);
		
		this.loadMapValues(this.effectDuration, "settings.effect-duration");
		this.loadMapValues(this.effectLevel, "settings.effect-level");
	}
	
	@Override
	@NotNull
	public String getDescription(int lvl) {
		return super.getDescription(lvl)
				.replace("%potion-level%", NumberUT.toRoman(this.getEffectLevel(lvl)))
				.replace("%potion-duration%", NumberUT.format((double)this.getEffectDuration(lvl) / 20D))
				.replace("%potion-effect%", plugin.lang().getPotionType(this.getPotionEffectType()));
	}
	
	@NotNull
	public final PotionEffectType getPotionEffectType() {
		return this.effectType;
	}
	
	public final int getEffectDuration(int lvl) {
		Map.Entry<Integer, Double> e = this.effectDuration.floorEntry(lvl);
		return (int) ((e != null ? e.getValue() : 2 * (lvl + 1)) * 20);
	}
	
	public final int getEffectLevel(int lvl) {
		Map.Entry<Integer, Double> e = this.effectLevel.floorEntry(lvl);
		return (int) (e != null ? e.getValue() : lvl);
	}
	
	public final void addEffect(@NotNull LivingEntity target, int lvl) {
		int bTime = this.getEffectDuration(lvl);
		int bLvl = Math.max(0, this.getEffectLevel(lvl) - 1);
		
		PotionEffect pet = new PotionEffect(this.effectType, bTime, bLvl, false, this.isParticles);
		this.addPotionEffect(target, pet, true);
	}
	
}