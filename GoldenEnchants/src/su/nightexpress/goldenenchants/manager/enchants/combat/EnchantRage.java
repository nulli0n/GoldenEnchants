package su.nightexpress.goldenenchants.manager.enchants.combat;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.ICombatEnchantPotionTemplate;

public class EnchantRage extends ICombatEnchantPotionTemplate {
	
	public EnchantRage(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(PotionEffectType.INCREASE_DAMAGE, plugin, cfg);
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
		
		victim = damager;
		super.use(weapon, damager, victim, e, lvl);
	}
}
