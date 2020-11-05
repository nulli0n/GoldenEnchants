package su.nightexpress.goldenenchants.manager.enchants.tool;

import java.util.TreeMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.fogus.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantLuckyMiner extends IEnchantChanceTemplate implements BlockEnchant {

	private TreeMap<Integer, Double> expModifier;
	
	public EnchantLuckyMiner(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.expModifier = new TreeMap<>();
		this.loadMapValues(this.expModifier, "settings.exp-modifier");
	}

	@Override
	public void use(@NotNull ItemStack tool, @NotNull Player p, @NotNull BlockBreakEvent e,	int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		double expMod = this.getMapValue(this.expModifier, lvl, 1D);
		e.setExpToDrop((int) ((double) e.getExpToDrop() * expMod));
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_PICKAXES.contains(item.getType());
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
