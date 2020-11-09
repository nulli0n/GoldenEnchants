package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.DeathEnchant;

public class EnchantThrifty extends IEnchantChanceTemplate implements DeathEnchant {

	private Set<String> entityBlacklist;
	
	public EnchantThrifty(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.entityBlacklist = cfg.getStringSet("settings.entity-blacklist").stream()
				.map(String::toUpperCase).collect(Collectors.toSet());
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isSword(item);
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

	@Override
	public void use(@NotNull LivingEntity dead, @NotNull EntityDeathEvent e, int lvl) {
		if (this.entityBlacklist.contains(dead.getType().name())) return;
		if (!this.checkTriggerChance(lvl)) return;
		
		Material material = Material.getMaterial(dead.getType().name() + "_SPAWN_EGG");
		if (material == null) return;
		
		ItemStack egg = new ItemStack(material);
		e.getDrops().add(egg);
	}
}
