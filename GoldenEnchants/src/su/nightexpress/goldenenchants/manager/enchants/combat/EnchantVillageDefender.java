package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.TreeMap;

import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pillager;
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

public class EnchantVillageDefender extends IEnchantChanceTemplate implements CombatEnchant {

	private boolean dmgAsModifier;
	private TreeMap<Integer, Double> dmgAddict;
	private String effectParticle;
	
	public EnchantVillageDefender(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.dmgAddict = new TreeMap<>();
		this.dmgAsModifier = cfg.getBoolean("settings.damage-add.as-multiplier");
		this.loadMapValues(this.dmgAddict, "settings.damage-add.formula");
		this.effectParticle = cfg.getString("settings.effect-particle", Particle.VILLAGER_ANGRY.name());
	}

	@Override
	public void use(@NotNull EntityDamageByEntityEvent e, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull ItemStack weapon, int lvl) {
		
		if (!(victim instanceof Pillager)) return;
		if (!this.checkTriggerChance(lvl)) return;
		
		double damageAdd = this.getMapValue(this.dmgAddict, lvl, 0);
		double damageHas = e.getDamage();
		
		e.setDamage(this.dmgAsModifier ? (damageHas * damageAdd) : (damageHas + damageAdd));
		EffectUT.playEffect(victim.getEyeLocation(), this.effectParticle, 0.15, 0.15, 0.15, 0.13f, 3);
	}

	@Override
	@NotNull
	public String getDescription(int lvl) {
		return super.getDescription(lvl)
				.replace("%damage%", NumberUT.format(this.getMapValue(this.dmgAddict, lvl, 0)));
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
