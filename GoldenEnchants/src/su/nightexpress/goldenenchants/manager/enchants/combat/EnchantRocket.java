package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.MsgUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;

public class EnchantRocket extends IEnchantChanceTemplate implements CombatEnchant {
	
	private TreeMap<Integer, Double> fireworkPower;
	
	public EnchantRocket(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.fireworkPower = new TreeMap<>();
		
		this.loadMapValues(this.fireworkPower, "firework-power");
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
	
	public final double getFireworkPower(int lvl) {
		Map.Entry<Integer, Double> e = this.fireworkPower.floorEntry(lvl);
		return e != null ? e.getValue() : 1.5D * lvl;
	}

	@Override
	public void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager,
			@NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		if (victim.isInsideVehicle()) {
			victim.leaveVehicle();
		}
		
		Firework f = Rnd.spawnRandomFirework(victim.getLocation());
		FireworkMeta fmeta = f.getFireworkMeta();
		fmeta.setPower((int) this.getFireworkPower(lvl));
		f.setFireworkMeta(fmeta);
		f.addPassenger(victim);
		
		Sound sound = Sound.ENTITY_FIREWORK_ROCKET_LAUNCH;
		MsgUT.sound(victim.getLocation(), sound.name());
	}
}
