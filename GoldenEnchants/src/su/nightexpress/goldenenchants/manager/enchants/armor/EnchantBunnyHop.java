package su.nightexpress.goldenenchants.manager.enchants.armor;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.fogus.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantPotionTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.PassiveEnchant;

public class EnchantBunnyHop extends IEnchantPotionTemplate implements PassiveEnchant {
	
	public EnchantBunnyHop(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(PotionEffectType.JUMP, plugin, cfg);
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_BOOTS.contains(item.getType());
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_FEET;
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
	public void use(@NotNull LivingEntity user, int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		this.addEffect(user, lvl);
	}
}
