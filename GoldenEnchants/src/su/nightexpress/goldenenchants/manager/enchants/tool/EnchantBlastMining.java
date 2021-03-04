package su.nightexpress.goldenenchants.manager.enchants.tool;

import java.util.TreeMap;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.NumberUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.EnchantRegister;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantBlastMining extends IEnchantChanceTemplate implements BlockEnchant {

	private TreeMap<Integer, Double> explosionPower;
	
	public EnchantBlastMining(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.explosionPower = new TreeMap<>();
		this.loadMapValues(this.explosionPower, "settings.explosion-power");
	}

	@Override
	public void use(@NotNull ItemStack tool, @NotNull Player p, @NotNull BlockBreakEvent e, int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		float power = (float) this.getMapValue(this.explosionPower, lvl, 3D);
		
		Block block = e.getBlock();
		block.getWorld().createExplosion(block.getLocation(), power, false, true, p);
	}

	@Override
	@NotNull
	public String getDescription(int lvl) {
		return super.getDescription(lvl)
				.replace("%power%", NumberUT.format(this.getMapValue(this.explosionPower, lvl, 0)));
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_PICKAXES.contains(item.getType());
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return en == Enchantment.SILK_TOUCH || en == EnchantRegister.DIVINE_TOUCH
				|| en == EnchantRegister.TUNNEL || en == EnchantRegister.SMELTER;
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
