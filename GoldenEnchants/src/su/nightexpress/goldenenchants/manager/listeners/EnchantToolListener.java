package su.nightexpress.goldenenchants.manager.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.IListener;
import su.nexmedia.engine.utils.ItemUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantToolListener extends IListener<GoldenEnchants> {

	//private EnchantManager enchantManager;
	
	public EnchantToolListener(@NotNull EnchantManager enchantManager) {
		super(enchantManager.plugin);
		//this.enchantManager = enchantManager;
	}
	
	// ---------------------------------------------------------------
	// Block Break Enchants
	// ---------------------------------------------------------------
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawnerBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) return;
		
		ItemStack wpn = p.getInventory().getItemInMainHand();
		if (ItemUT.isAir(wpn)) return;
		
		ItemMeta meta = wpn.getItemMeta();
		if (meta == null) return;
		
		meta.getEnchants().forEach((en, lvl) -> {
			if (lvl < 1) return;
			if (!(en instanceof BlockEnchant)) return;
			
			BlockEnchant blockEnchant = (BlockEnchant) en;
			blockEnchant.use(wpn, p, e, lvl);
		});
	}
	
	// ---------------------------------------------------------------
	// Spawner Type Fix
	// ---------------------------------------------------------------
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawnerPlace(BlockPlaceEvent e) {
		Block b = e.getBlock();
		if (b.getType() != Material.SPAWNER) return;
		
		Player p = e.getPlayer();
		
		ItemStack spawner = p.getInventory().getItemInMainHand();
		if (ItemUT.isAir(spawner) || spawner.getType() != Material.SPAWNER) {
			spawner = p.getInventory().getItemInOffHand();
		}
		if (ItemUT.isAir(spawner) || spawner.getType() != Material.SPAWNER) {
			return;
		}
		
		BlockStateMeta meta = (BlockStateMeta) spawner.getItemMeta();
		if (meta == null) return;
		
		CreatureSpawner spawnerItem = (CreatureSpawner) meta.getBlockState();
	    CreatureSpawner spawnerBlock = ((CreatureSpawner) b.getState());
	    
	    spawnerBlock.setSpawnedType(spawnerItem.getSpawnedType());
	    spawnerBlock.update();
	}
}
