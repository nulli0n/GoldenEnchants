package su.nightexpress.goldenenchants.manager.enchants.combat;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.fogus.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.ICombatEnchantPotionTemplate;

public class EnchantBlindness extends ICombatEnchantPotionTemplate {
	
	public EnchantBlindness(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(PotionEffectType.BLINDNESS, plugin, cfg);
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
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}
}
