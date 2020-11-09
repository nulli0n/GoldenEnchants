package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.MsgUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantCutter extends IEnchantChanceTemplate implements CombatEnchant {
	
	protected TreeMap<Integer, Double> damageMod;
	
	public EnchantCutter(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.damageMod = new TreeMap<>();
		
		this.loadMapValues(this.damageMod, "settings.item-damage-modifier");
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

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isSword(item);
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
	}

	public final double getDamageModifier(int lvl) {
		Map.Entry<Integer, Double> e = this.damageMod.floorEntry(lvl);
		return e != null ? e.getValue() : 1.25;
	}
	
	@Override
	public void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		EntityEquipment equipment = victim.getEquipment();
		if (equipment == null) return;
		
		ItemStack[] armor = equipment.getArmorContents();
		if (armor.length == 0) return;
		
		int get = Rnd.get(armor.length);
		ItemStack cut = armor[get];
		
		if (cut == null || cut.getType() == Material.AIR) return;
		
		ItemMeta meta = cut.getItemMeta();
		if (!(meta instanceof Damageable)) return;
		
		Damageable dm = (Damageable) meta;
		dm.setDamage((int) (Math.max(1, dm.getDamage()) * this.getDamageModifier(lvl)));
		
		armor[get] = null;
		equipment.setArmorContents(armor);
		
		Item drop = victim.getWorld().dropItemNaturally(victim.getLocation(), cut);
		drop.setPickupDelay(40);
		drop.getVelocity().multiply(3D);
		
		EffectUT.playEffect(victim.getEyeLocation(), "ITEM_CRACK:" + cut.getType().name(), 0.2f, 0.15f, 0.2f, 0.15f, 40);
		MsgUT.sound(victim.getLocation(), Sound.BLOCK_ANVIL_BREAK.name());
	}
}
