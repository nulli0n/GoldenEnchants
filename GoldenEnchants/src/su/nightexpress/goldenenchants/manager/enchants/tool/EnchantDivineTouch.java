package su.nightexpress.goldenenchants.manager.enchants.tool;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.LocUT;
import su.nexmedia.engine.utils.StringUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantDivineTouch extends IEnchantChanceTemplate implements BlockEnchant {
	
	private String spawnerName;
	
	public EnchantDivineTouch(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		String path = "settings.";
		this.spawnerName = StringUT.color(cfg.getString(path + "spawner-name", "&aMob Spawner &7(%type%)"));
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isPickaxe(item);
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
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

	@Override
	public void use(@NotNull ItemStack tool, @NotNull Player p, @NotNull BlockBreakEvent e,
			int lvl) {
		
		Block b = e.getBlock();
		if (b.getType() != Material.SPAWNER) return;
		
		if (!this.checkTriggerChance(lvl)) return;
			
		Location l = LocUT.getCenter(b.getLocation());
		World world = l.getWorld();
		if (world == null) return;
		
		CreatureSpawner cs = (CreatureSpawner) b.getState();
		
		ItemStack spawner = new ItemStack(Material.SPAWNER);
		BlockStateMeta meta = (BlockStateMeta) spawner.getItemMeta();
		if (meta == null) return;
		
		BlockState bs = meta.getBlockState();
		CreatureSpawner css = (CreatureSpawner) bs;
		css.setSpawnedType(cs.getSpawnedType());
		css.update(true);
		meta.setBlockState(css);
		meta.setDisplayName(this.spawnerName.replace("%type%", plugin.lang().getEnum(cs.getSpawnedType())));
		spawner.setItemMeta(meta);
		
		world.dropItemNaturally(l, spawner);
		EffectUT.playEffect(l, "VILLAGER_HAPPY", 0.3f, 0.3f, 0.3f, 0.15f, 30);
	}
}
