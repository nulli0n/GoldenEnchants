package su.nightexpress.goldenenchants.manager.enchants.armor;

import javax.annotation.Nullable;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.MoveEnchant;

public class EnchantFlameWalker extends IEnchantChanceTemplate implements MoveEnchant {

	public EnchantFlameWalker(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
	}

	@Override
	public void use(@NotNull PlayerMoveEvent e, @NotNull LivingEntity user, int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		plugin.getNMSHandler().handleFlameWalker(user, user.getLocation(), lvl);
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
}
