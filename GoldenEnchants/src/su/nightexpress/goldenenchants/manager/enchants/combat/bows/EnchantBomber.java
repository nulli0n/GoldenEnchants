package su.nightexpress.goldenenchants.manager.enchants.combat.bows;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.fogus.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.EnchantRegister;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BowEnchant;

public class EnchantBomber extends IEnchantChanceTemplate implements BowEnchant {
	
	public EnchantBomber(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isBow(item);
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return en == EnchantRegister.ENDER_BOW || en == EnchantRegister.GHAST;
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
	public void use(@NotNull ItemStack bow, @NotNull LivingEntity shooter,
			@NotNull EntityShootBowEvent e, int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		if (!(e.getProjectile() instanceof Projectile)) return;
		
		Projectile pj = (Projectile) e.getProjectile();
		Vector orig = pj.getVelocity();
		
		TNTPrimed joke = (TNTPrimed)pj.getWorld().spawn(pj.getLocation(), TNTPrimed.class);
		joke.setVelocity(orig.multiply(e.getForce() * 2.0));
		e.setProjectile(joke);
	}
}
