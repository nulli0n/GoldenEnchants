package su.nightexpress.goldenenchants.manager.enchants.combat;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.ICombatEnchantPotionTemplate;

public class EnchantVenom extends ICombatEnchantPotionTemplate {
	
	public EnchantVenom(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(PotionEffectType.POISON, plugin, cfg);
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
}
