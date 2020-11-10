package su.nightexpress.goldenenchants.manager.enchants.armor;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.PassiveEnchant;

public class EnchantSaturation extends IEnchantChanceTemplate implements PassiveEnchant {
	
	private TreeMap<Integer, Double> saturationAmount;
	
	public EnchantSaturation(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.saturationAmount = new TreeMap<>();
		this.loadMapValues(this.saturationAmount, "settings.saturation-amount");
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isArmor(item);
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR;
	}
	
	@Override
	public boolean isCursed() {
		return false;
	}
	
	@Override
	public boolean isTreasure() {
		return false;
	}
	
	public final double getSaturationAmount(int lvl) {
		Map.Entry<Integer, Double> e = this.saturationAmount.floorEntry(lvl);
		return e != null ? e.getValue() : lvl;
	}
	
	@Override
	public void use(LivingEntity user, int lvl) {
		if (!(user instanceof Player)) return;
		if (!this.checkTriggerChance(lvl)) return;
		
		
		Player target = (Player) user;
		int amount = (int) this.getSaturationAmount(lvl);
		
		target.setFoodLevel(Math.min(20, target.getFoodLevel() + amount));
	}
}
