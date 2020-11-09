package su.nightexpress.goldenenchants.manager.enchants.tool;

import java.util.TreeMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.LocUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.PassiveEnchant;

public class EnchantTelekinesis extends IEnchantChanceTemplate implements PassiveEnchant {

	private TreeMap<Integer, Double> radHorizon;
	private TreeMap<Integer, Double> radVert;
	private TreeMap<Integer, Double> power;
	
	public EnchantTelekinesis(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.radHorizon = new TreeMap<>();
		this.radVert = new TreeMap<>();
		this.power = new TreeMap<>();
		
		this.loadMapValues(this.radHorizon, "settings.radius.horizontal");
		this.loadMapValues(this.radVert, "settings.radius.vertical");
		this.loadMapValues(this.power, "settings.power");
	}

	@Override
	public void use(@NotNull LivingEntity user, int lvl) {
		if (!(user instanceof Player)) return;
		if (((Player)user).getInventory().firstEmpty() == -1) return;
		if (!this.checkTriggerChance(lvl)) return;
		
		double radH = this.getMapValue(this.radHorizon, lvl, 3D);
		double radV = this.getMapValue(this.radVert, lvl, 1.5);
		double power = this.getMapValue(this.power, lvl, 1.35);
		
		user.getNearbyEntities(radH, radV, radH).stream().filter(entity -> entity instanceof Item)
		.forEach(e -> {
			Item item = (Item) e;
			Vector dir = LocUT.getDirectionTo(item.getLocation(), user.getLocation());
			item.setVelocity(dir.multiply(power));
		});
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ItemUT.isTool(item) || ItemUT.isWeapon(item);
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
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
