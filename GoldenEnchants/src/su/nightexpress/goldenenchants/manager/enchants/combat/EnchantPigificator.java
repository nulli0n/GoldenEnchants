package su.nightexpress.goldenenchants.manager.enchants.combat;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.MsgUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.EnchantRegister;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantPigificator extends IEnchantChanceTemplate implements CombatEnchant {

	private String sound;
	private String effect;
	
	public EnchantPigificator(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.sound = cfg.getString("settings.effect-sound", Sound.ENTITY_PIG_AMBIENT.name());
		this.effect = cfg.getString("settings.effect-particle", Particle.HEART.name());
	}

	@Override
	public void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl) {
		
		if (!(victim instanceof PigZombie)) return;
		if (!this.checkTriggerChance(lvl)) return;
		
		e.setCancelled(true);
		
		EffectUT.playEffect(victim.getLocation(), this.effect, 0.25, 0.25, 0.25, 0.1f, 30);
		MsgUT.sound(victim.getLocation(), this.sound);
		
		victim.getWorld().spawn(victim.getLocation(), Pig.class);
		victim.remove();
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isSword(item);
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return en == EnchantRegister.THUNDER;
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
