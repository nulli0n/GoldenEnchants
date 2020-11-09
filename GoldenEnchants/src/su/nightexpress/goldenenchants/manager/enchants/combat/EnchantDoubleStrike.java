package su.nightexpress.goldenenchants.manager.enchants.combat;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.MsgUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantDoubleStrike extends IEnchantChanceTemplate implements CombatEnchant {
	
	private String enchantParticle;
	private String enchantSound;
	
	public EnchantDoubleStrike(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		String path = "settings.";
		this.enchantParticle = cfg.getString(path + "enchant-particle-effect", Particle.EXPLOSION_NORMAL.name());
		this.enchantSound = cfg.getString(path + "enchant-sound-effect", Sound.ENTITY_GENERIC_EXPLODE.name());
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
		
		e.setDamage(e.getDamage() * 2);
		EffectUT.playEffect(victim.getEyeLocation(), this.enchantParticle, 0.2f, 0.15f, 0.2f, 0.15f, 20);
		MsgUT.sound(victim.getLocation(), this.enchantSound);
	}
}
