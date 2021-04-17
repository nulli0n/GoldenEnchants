package su.nightexpress.goldenenchants.manager.enchants.armor;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantPotionTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantHardened extends IEnchantPotionTemplate implements CombatEnchant {

	public EnchantHardened(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(PotionEffectType.DAMAGE_RESISTANCE, plugin, cfg);
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_CHESTPLATES.contains(item.getType());
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_TORSO;
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
		
		if (!this.checkTriggerChance(lvl)) return;
		this.addEffect(victim, lvl);
	}
}
