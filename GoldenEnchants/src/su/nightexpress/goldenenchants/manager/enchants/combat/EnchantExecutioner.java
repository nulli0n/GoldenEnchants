package su.nightexpress.goldenenchants.manager.enchants.combat;


import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.fogus.engine.config.api.JYML;
import su.fogus.engine.utils.LocUT;
import su.fogus.engine.utils.StringUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.DeathEnchant;

public class EnchantExecutioner extends IEnchantChanceTemplate implements DeathEnchant {
	
	private String enchantParticle;
	private String headName;
	
	public EnchantExecutioner(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		String path = "settings.";
		this.enchantParticle = cfg.getString(path + "enchant-particle-effect", Particle.BLOCK_CRACK.name() + ":REDSTONE_BLOCK");
		this.headName = StringUT.color(cfg.getString(path + "head-name", "&c%entity%'s Head"));
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

	@SuppressWarnings("deprecation")
	@Override
	public void use(@NotNull LivingEntity victim, @NotNull EntityDeathEvent e, int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		ItemStack item;
		if (victim instanceof WitherSkeleton) {
			item = new ItemStack(Material.WITHER_SKELETON_SKULL);
		}
		else if (victim instanceof Zombie) {
			item = new ItemStack(Material.ZOMBIE_HEAD);
		}
		else if (victim instanceof Skeleton) {
			item = new ItemStack(Material.SKELETON_SKULL);
		}
		else if (victim instanceof Creeper) {
			item = new ItemStack(Material.CREEPER_HEAD);
		}
		else if (victim instanceof EnderDragon) {
			item = new ItemStack(Material.DRAGON_HEAD);
		}
		else {
			item = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta sm = (SkullMeta) item.getItemMeta();
			if (sm == null) return;
			
			String name;
			String disp;
			if (victim instanceof Player) {
				name = victim.getName();
				disp = this.headName.replace("%entity%", victim.getName());
			}
			else {
				name = this.getValidSkullName(victim);
				disp = this.headName.replace("%entity%", plugin.lang().getEnum(victim.getType()));
			}
			if (name.isEmpty()) return;
			
			sm.setOwner(name);
			sm.setDisplayName(disp);
			item.setItemMeta(sm);
		}
		victim.getWorld().dropItemNaturally(victim.getLocation(), item);
		
		LocUT.playEffect(victim.getEyeLocation(), this.enchantParticle, 0.2f, 0.15f, 0.2f, 0.15f, 40);
	}
	
	@NotNull
	private String getValidSkullName(@NotNull Entity e) {
		EntityType et = e.getType();
		
		switch (et) {
			case MAGMA_CUBE: {
				return "MHF_LavaSlime";
			}
			case ELDER_GUARDIAN: {
				return "MHF_EGuardian";
			}
			case IRON_GOLEM: {
				return "MHF_Golem";
			}
			default: {
				String s = et.name().toLowerCase().replace("_", " ");
				return "MHF_" + StringUT.capitalizeFully(s).replace(" ", "");
			}
		}
	}
}
