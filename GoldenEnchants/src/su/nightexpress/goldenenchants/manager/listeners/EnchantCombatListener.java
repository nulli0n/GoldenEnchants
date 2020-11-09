package su.nightexpress.goldenenchants.manager.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.IListener;
import su.nexmedia.engine.utils.EntityUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.manager.enchants.api.BowEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.CombatEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.DeathEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.LocationEnchant;

public class EnchantCombatListener extends IListener<GoldenEnchants> {

	private EnchantManager enchantManager;
	
	public EnchantCombatListener(@NotNull EnchantManager enchantManager) {
		super(enchantManager.plugin);
		this.enchantManager = enchantManager;
	}
	
	// ---------------------------------------------------------------
	// Combat Attacking Enchants
	// ---------------------------------------------------------------
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantCombatMeleeApply(EntityDamageByEntityEvent e) {
		Entity e1 = e.getEntity();
		if (!(e1 instanceof LivingEntity)) return;
		
		Entity e2 = e.getDamager();
		if (!(e2 instanceof LivingEntity)) return;
		
		LivingEntity victim = (LivingEntity) e1;
		LivingEntity damager = (LivingEntity) e2;
		
		EntityEquipment equip = damager.getEquipment();
		if (equip == null) return;
		
		ItemStack wpn = equip.getItemInMainHand();
		if (ItemUT.isAir(wpn)) return;
		
		ItemMeta meta = wpn.getItemMeta();
		if (meta == null) return;
		
		meta.getEnchants().forEach((en, lvl) -> {
			if (lvl < 1) return;
			if (!(en instanceof CombatEnchant)) return;
			if (en instanceof BowEnchant) return;
			
			CombatEnchant combatEnchant = (CombatEnchant) en;
			combatEnchant.use(wpn, damager, victim, e, lvl);
		});
	}
	
	// ---------------------------------------------------------------
	// Armor Defensive Enchants
	// ---------------------------------------------------------------
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantCombatArmorApply(EntityDamageByEntityEvent e) {
		// Prevent armor enchants to have effect if damage is from Thorns.
		if (e.getCause() == DamageCause.THORNS) return;
		
		Entity e1 = e.getEntity();
		if (!(e1 instanceof LivingEntity)) return;
		
		Entity e2 = e.getDamager();
		if (e2 instanceof Projectile) {
			Projectile pj = (Projectile) e2;
			if (pj.getShooter() instanceof Entity) {
				e2 = (Entity) pj.getShooter();
			}
		}
		if (!(e2 instanceof LivingEntity) || e2.equals(e1)) return;
		
		LivingEntity victim = (LivingEntity) e1;
		LivingEntity damager = (LivingEntity) e2;
		
		EntityEquipment equipDamager = damager.getEquipment();
		if (equipDamager == null) return;
		
		ItemStack wpn = equipDamager.getItemInMainHand();
		
		for (ItemStack armor : EntityUT.getArmor(victim)) {
			if (armor == null) continue;
			
			ItemMeta meta = armor.getItemMeta();
			if (meta == null) continue;
			
			meta.getEnchants().forEach((en, lvl) -> {
				if (lvl < 1) return;
				if (!(en instanceof CombatEnchant)) return;
				
				CombatEnchant combatEnchant = (CombatEnchant) en;
				combatEnchant.use(wpn, damager, victim, e, lvl);
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
		if (bow == null || bow.getType() == Material.AIR) return;
		
		ItemMeta meta = bow.getItemMeta();
		if (meta == null) return;
		
		Projectile pj = (Projectile) e.getProjectile();
		this.enchantManager.setArrowWeapon(pj, bow);
		
		meta.getEnchants().forEach((en, lvl) -> {
			if (lvl < 1) return;
			if (!(en instanceof BowEnchant)) return;
			
			BowEnchant bowEnchant = (BowEnchant) en;
			bowEnchant.use(bow, shooter, e, lvl);
		});
	}
	
	// ---------------------------------------------------------------
	// Bow Damage Enchants
	// ---------------------------------------------------------------
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantBowDamageApply(EntityDamageByEntityEvent e) {
		Entity e1 = e.getEntity();
		if (!(e1 instanceof LivingEntity)) return;
		
		Entity e2 = e.getDamager();
		if (!(e2 instanceof Projectile)) return;
		
		Projectile pj = (Projectile) e2;
		
		ProjectileSource src = pj.getShooter();
		if (!(src instanceof LivingEntity)) return;
		
		ItemStack wpn = this.enchantManager.getArrowWeapon(pj);
		if (wpn == null || wpn.getType() == Material.AIR) return;
		
		LivingEntity victim = (LivingEntity) e1;
		LivingEntity damager = (LivingEntity) src;
		
		ItemMeta meta = wpn.getItemMeta();
		if (meta == null) return;
		
		meta.getEnchants().forEach((en, lvl) -> {
			if (lvl < 1) return;
			if (!(en instanceof CombatEnchant)) return;
			if (!(en instanceof BowEnchant)) return;
			
			CombatEnchant combatEnchant = (CombatEnchant) en;
			combatEnchant.use(wpn, damager, victim, e, lvl);
		});
	}
	
	// ---------------------------------------------------------------
	// Bow Hit Land Enchants
	// ---------------------------------------------------------------
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantBowLocationApply(ProjectileHitEvent e) {
		Block b = e.getHitBlock();
		if (b == null) return;
		
		Projectile pj = e.getEntity();
		
		ItemStack wpn = this.enchantManager.getArrowWeapon(pj);
		if (wpn == null) return;
		
		ItemMeta meta = wpn.getItemMeta();
		if (meta == null) return;
		
		meta.getEnchants().forEach((en, lvl) -> {
			if (lvl < 1) return;
			if (!(en instanceof LocationEnchant)) return;
			if (!(en instanceof BowEnchant)) return;
			
			LocationEnchant locEnchant = (LocationEnchant) en;
			locEnchant.use(wpn, pj, b.getLocation(), lvl);
		});
	}
	
	// ---------------------------------------------------------------
	// Death Related Enchants
	// ---------------------------------------------------------------
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantDeathApply(EntityDeathEvent e) {
		LivingEntity dead = e.getEntity();
		Player killer = dead.getKiller();
		
		// Armor enchans are always applied to dead entity self.
		for (ItemStack armor : EntityUT.getArmor(dead)) {
			if (armor == null) continue;
			ItemMeta meta = armor.getItemMeta();
			if (meta == null) continue;
			
			meta.getEnchants().forEach((en, lvl) -> {
				if (lvl < 1) return;
				if (!(en instanceof DeathEnchant)) return;
				
				DeathEnchant deathEnchant = (DeathEnchant) en;
				deathEnchant.use(dead, e, lvl);
			});
		}
		
		// Trigger Killer's enchantments.
		if (killer == null) return;
		
		ItemStack wpn = killer.getInventory().getItemInMainHand();
		if (ItemUT.isAir(wpn)) return;
		
		ItemMeta meta = wpn.getItemMeta();
		if (meta == null) return;
		
		meta.getEnchants().forEach((en, lvl) -> {
			if (lvl < 1) return;
			if (!(en instanceof DeathEnchant)) return;
			
			DeathEnchant deathEnchant = (DeathEnchant) en;
			deathEnchant.use(dead, e, lvl);
		});
	}
}
