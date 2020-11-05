package su.nightexpress.goldenenchants.manager.enchants.tool;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.fogus.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.InteractEnchant;

@Deprecated
public class EnchantFarmer extends IEnchantChanceTemplate implements InteractEnchant {

	public EnchantFarmer(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
	}

	@Override
	public void use(@NotNull Player player, @NotNull ItemStack item, @NotNull PlayerInteractEvent e, int lvl) {
		Block block = e.getClickedBlock();
		if (block == null || block.getType() == Material.AIR) return;
		
		
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_HOES.contains(item.getType());
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TOOL;
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
