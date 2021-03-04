package su.nightexpress.goldenenchants.manager.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.IListener;
import su.nexmedia.engine.utils.EntityUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.BowEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.DeathEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.InteractEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.LocationEnchant;

public class EnchantHandlerListener extends IListener<GoldenEnchants> {

	private EnchantManager enchantManager;
	
	public EnchantHandlerListener(@NotNull EnchantManager enchantManager) {
		super(enchantManager.plugin);
		this.enchantManager = enchantManager;
	}
	
	// ---------------------------------------------------------------
	// Combat Attacking Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantCombatMeleeApply(EntityDamageByEntityEvent e) {
		if (e.getCause() == DamageCause.THORNS) return;
		
		Entity eVictim = e.getEntity();
		if (!(eVictim instanceof LivingEntity)) return;
		
		Entity eDamager = e.getDamager();
		if (!(eDamager instanceof LivingEntity)) return;
		
		LivingEntity victim = (LivingEntity) eVictim;
		LivingEntity damager = (LivingEntity) eDamager;
		
		EntityEquipment equip = damager.getEquipment();
		if (equip == null) return;
		
		ItemStack wpn = equip.getItemInMainHand();
		if (ItemUT.isAir(wpn) || wpn.getType() == Material.ENCHANTED_BOOK) return;
		
		EnchantManager.getItemGoldenEnchants(wpn, CombatEnchant.class).forEach((combatEnchant, level) -> {
			if (combatEnchant instanceof BowEnchant) return;
			combatEnchant.use(wpn, damager, victim, e, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Armor Defensive Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantCombatArmorApply(EntityDamageByEntityEvent e) {
		// Prevent armor enchants to have effect if damage is from Thorns.
		if (e.getCause() == DamageCause.THORNS) return;
		
		Entity eVictim = e.getEntity();
		if (!(eVictim instanceof LivingEntity)) return;
		
		Entity eDamager = e.getDamager();
		if (eDamager instanceof Projectile) {
			Projectile pj = (Projectile) eDamager;
			if (pj.getShooter() instanceof Entity) {
				eDamager = (Entity) pj.getShooter();
			}
		}
		if (!(eDamager instanceof LivingEntity) || eDamager.equals(eVictim)) return;
		
		LivingEntity victim = (LivingEntity) eVictim;
		LivingEntity damager = (LivingEntity) eDamager;
		
		EntityEquipment equipDamager = damager.getEquipment();
		if (equipDamager == null) return;
		
		ItemStack wpn = equipDamager.getItemInMainHand();
		
		for (ItemStack armor : EntityUT.getArmor(victim)) {
			if (ItemUT.isAir(armor)) continue;
			
			EnchantManager.getItemGoldenEnchants(armor, CombatEnchant.class).forEach((combatEnchant, level) -> {
				combatEnchant.use(wpn, damager, victim, e, level);
			});
		}
	}
	
	// ---------------------------------------------------------------
	// Bow Shooting Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantBowShootApply(EntityShootBowEvent e) {
		if (!(e.getProjectile() instanceof Projectile)) return;
		
		LivingEntity shooter = e.getEntity();
		if (shooter.getEquipment() == null) return;

		ItemStack bow = e.getBow();
		if (bow == null || ItemUT.isAir(bow) || bow.getType() == Material.ENCHANTED_BOOK) return;
		
		Projectile pj = (Projectile) e.getProjectile();
		this.enchantManager.setArrowWeapon(pj, bow);
		
		EnchantManager.getItemGoldenEnchants(bow, BowEnchant.class).forEach((bowEnchant, level) -> {
			bowEnchant.use(bow, shooter, e, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Bow Damage Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantBowDamageApply(EntityDamageByEntityEvent e) {
		Entity eVictim = e.getEntity();
		if (!(eVictim instanceof LivingEntity)) return;
		
		Entity eDamager = e.getDamager();
		if (!(eDamager instanceof Projectile)) return;
		
		Projectile projectile = (Projectile) eDamager;
		
		ProjectileSource src = projectile.getShooter();
		if (!(src instanceof LivingEntity)) return;
		
		ItemStack wpn = this.enchantManager.getArrowWeapon(projectile);
		if (wpn == null || wpn.getType() == Material.AIR || wpn.getType() == Material.ENCHANTED_BOOK) return;
		
		LivingEntity victim = (LivingEntity) eVictim;
		LivingEntity damager = (LivingEntity) src;
		
		EnchantManager.getItemGoldenEnchants(wpn, CombatEnchant.class).forEach((combatEnchant, level) -> {
			if (!(combatEnchant instanceof BowEnchant)) return;
			combatEnchant.use(wpn, damager, victim, e, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Bow Hit Land Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantBowLocationApply(ProjectileHitEvent e) {
		Block block = e.getHitBlock();
		if (block == null) return;
		
		Projectile projectile = e.getEntity();
		
		ItemStack wpn = this.enchantManager.getArrowWeapon(projectile);
		if (wpn == null || ItemUT.isAir(wpn) || wpn.getType() == Material.ENCHANTED_BOOK) return;
		
		EnchantManager.getItemGoldenEnchants(wpn, LocationEnchant.class).forEach((locEnchant, level) -> {
			if (!(locEnchant instanceof BowEnchant)) return;
			locEnchant.use(wpn, projectile, block.getLocation(), level);
		});
	}
	
	// ---------------------------------------------------------------
	// Interaction Related Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantInteractApply(PlayerInteractEvent e) {
		if (e.useInteractedBlock() == Result.DENY) return;
		if (e.useItemInHand() == Result.DENY) return;
		
		ItemStack item = e.getItem();
		if (item == null || ItemUT.isAir(item) || item.getType() == Material.ENCHANTED_BOOK) return;
		
		Player player = e.getPlayer();
		EnchantManager.getItemGoldenEnchants(item, InteractEnchant.class).forEach((interEnchant, level) -> {
			interEnchant.use(player, item, e, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Death Related Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantDeathApply(EntityDeathEvent e) {
		LivingEntity dead = e.getEntity();
		for (ItemStack armor : EntityUT.getArmor(dead)) {
			if (armor == null || ItemUT.isAir(armor)) continue;
			
			EnchantManager.getItemGoldenEnchants(armor, DeathEnchant.class).forEach((deathEnchant, level) -> {
				deathEnchant.use(dead, e, level);
			});
		}
		
		Player killer = dead.getKiller();
		if (killer == null) return;
		
		ItemStack wpn = killer.getInventory().getItemInMainHand();
		if (ItemUT.isAir(wpn) || wpn.getType() == Material.ENCHANTED_BOOK) return;
		
		EnchantManager.getItemGoldenEnchants(wpn, DeathEnchant.class).forEach((deathEnchant, level) -> {
			deathEnchant.use(dead, e, level);
		});
	}

	// ---------------------------------------------------------------
	// Block Break Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchantBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) return;
		
		ItemStack tool = player.getInventory().getItemInMainHand();
		if (ItemUT.isAir(tool) || tool.getType() == Material.ENCHANTED_BOOK) return;
		
		EnchantManager.getItemGoldenEnchants(tool, BlockEnchant.class).forEach((blockEnchant, level) -> {
			blockEnchant.use(tool, player, e, level);
		});
	}
}
