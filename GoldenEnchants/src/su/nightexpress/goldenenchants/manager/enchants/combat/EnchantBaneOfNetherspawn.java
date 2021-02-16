package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.Set;
import java.util.TreeMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.core.Version;
import su.nexmedia.engine.utils.EffectUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantBaneOfNetherspawn extends IEnchantChanceTemplate implements CombatEnchant {

	private String effect;
	private boolean damageModifier;
	private TreeMap<Integer, Double> damageFormula;
	
	private final Set<EntityType> entityTypes;
	
	public EnchantBaneOfNetherspawn(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.effect = cfg.getString("settings.particle-effect", "");
		this.damageModifier = cfg.getBoolean("settings.damage.as-modifier");
		this.damageFormula = new TreeMap<>();
		this.loadMapValues(this.damageFormula, "settings.damage.formula");
		
		this.entityTypes = Sets.newHashSet(EntityType.BLAZE, EntityType.MAGMA_CUBE,
				EntityType.WITHER_SKELETON, EntityType.GHAST, EntityType.WITHER);
		
		if (Version.CURRENT.isHigher(Version.V1_15_R1)) {
			this.entityTypes.add(EntityType.PIGLIN);
			this.entityTypes.add(EntityType.PIGLIN_BRUTE);
			this.entityTypes.add(EntityType.ZOGLIN);
			this.entityTypes.add(EntityType.HOGLIN);
			this.entityTypes.add(EntityType.STRIDER);
		}
		else {
			this.entityTypes.add(EntityType.valueOf("PIG_ZOMBIE"));
		}
	}

	@Override
	public void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl) {
		
		if (!this.entityTypes.contains(victim.getType())) return;
		if (!this.checkTriggerChance(lvl)) return;
		
		double damageHas = e.getDamage();
		double damageAdd = this.getMapValue(this.damageFormula, lvl, 0D);
		e.setDamage(this.damageModifier ? damageHas * damageAdd : damageHas + damageAdd);
		EffectUT.playEffect(victim.getEyeLocation(), this.effect, 0.25, 0.25, 0.25, 0.1f, 30);
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
