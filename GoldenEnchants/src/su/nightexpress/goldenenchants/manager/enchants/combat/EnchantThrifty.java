package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.DeathEnchant;

public class EnchantThrifty extends IEnchantChanceTemplate implements DeathEnchant {

	private Set<String> entityBlacklist;
	private Set<String> spawnReasonBlacklist;
	
	private static final String META_SETTING_SPAWN_REASON = "GOLDEN_ENCHANTS_THRIFTY_SETTING_SPAWN_REASON";
	
	public EnchantThrifty(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		cfg.addMissing("settings.spawn-reason-blacklist", Arrays.asList("SPAWNER"));
		cfg.saveChanges();
		
		this.entityBlacklist = cfg.getStringSet("settings.entity-blacklist").stream()
				.map(String::toUpperCase).collect(Collectors.toSet());
		
		this.spawnReasonBlacklist = cfg.getStringSet("settings.spawn-reason-blacklist").stream()
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
	public void use(@NotNull EntityDeathEvent e, @NotNull LivingEntity dead, int lvl) {
		if (this.entityBlacklist.contains(dead.getType().name())) return;
		if (dead.hasMetadata(META_SETTING_SPAWN_REASON)) return;
		if (!this.checkTriggerChance(lvl)) return;
		
		Material material = Material.getMaterial(dead.getType().name() + "_SPAWN_EGG");
		if (material == null) {
			if (dead.getType() == EntityType.MUSHROOM_COW) {
				material = Material.MOOSHROOM_SPAWN_EGG;
			}
			else return;
		}
		
		ItemStack egg = new ItemStack(material);
		e.getDrops().add(egg);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSettingCreatureSpawnReason(CreatureSpawnEvent e) {
		if (!this.spawnReasonBlacklist.contains(e.getSpawnReason().name())) return;
		
		e.getEntity().setMetadata(META_SETTING_SPAWN_REASON, new FixedMetadataValue(plugin, true));
	}
}
