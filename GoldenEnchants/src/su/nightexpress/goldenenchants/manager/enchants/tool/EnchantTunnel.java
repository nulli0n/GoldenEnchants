package su.nightexpress.goldenenchants.manager.enchants.tool;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.LocUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantTunnel extends IEnchantChanceTemplate implements BlockEnchant {

	private boolean disableOnSneak;

	private static final String LOOP_FIX = "EVENT_STOP";
	// X and Z offsets for each block AoE mined
	private static final int[][] MINING_COORD_OFFSETS = new int[][]
		{
			{0, 0},
			{0, -1},
			{-1, 0},
			{0, 1},
			{1, 0},
			{-1, -1},
			{-1, 1},
			{1, -1},
			{1, 1},
		};
	
	public EnchantTunnel(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		cfg.addMissing("settings.disable-on-sneak", true);
		cfg.saveChanges();
		
		this.disableOnSneak = cfg.getBoolean("settings.disable-on-sneak");
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isPickaxe(item) || ITEM_SHOVELS.contains(item.getType());
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
	public void use(@NotNull BlockBreakEvent e, @NotNull Player player, @NotNull ItemStack item, int lvl) {
		if (this.disableOnSneak && player.isSneaking()) return;
		
		if (player.hasMetadata(LOOP_FIX)) {
			player.removeMetadata(LOOP_FIX, plugin);
			return;
		}
		
		if (!this.checkTriggerChance(lvl)) return;
		
		BlockFace dir = LocUT.getDirection(player);
		Block block = e.getBlock();
		
		// Redstone ore seems to be 'interactable'.
		if (block.getType().isInteractable() && block.getType() != Material.REDSTONE_ORE) return;
		if (block.getDrops(item).isEmpty()) return;
		
		boolean isY = dir != null && block.getRelative(dir.getOppositeFace()).isEmpty();
		boolean isZ = dir == BlockFace.EAST || dir == BlockFace.WEST;

		// Mine + shape if Tunnel I, 3x3 if Tunnel II
		int blocksBroken = 1;
		if (lvl == 1) {
			blocksBroken = 2;
		} 
		else if (lvl == 2) {
			blocksBroken = 5;
		} 
		else if (lvl == 3) {
			blocksBroken = 9;
		}
		
		int expDrop = e.getExpToDrop();
		for (int i = 0; i < blocksBroken; i++) {
			if (ItemUT.isAir(item)) break;
			
			int xAdd = MINING_COORD_OFFSETS[i][0];
			int zAdd = MINING_COORD_OFFSETS[i][1];
			
			Block blockAdd;
			if (isY) {
				blockAdd = block.getLocation().clone().add(isZ ? 0 : xAdd, zAdd, isZ ? xAdd : 0).getBlock();
			}
			else {
				blockAdd = block.getLocation().clone().add(xAdd, 0, zAdd).getBlock();
			}
			
			// Skip blocks that should not be mined
			if (blockAdd.getDrops(item).isEmpty()) continue;
			if (blockAdd.isLiquid()) continue;
			
			Material addType = blockAdd.getType();
			if (addType.isInteractable() && addType != Material.REDSTONE_ORE) continue;
			if (addType == Material.BEDROCK || addType == Material.END_PORTAL
					|| addType == Material.END_PORTAL_FRAME) continue;
			if (addType == Material.OBSIDIAN && addType != block.getType()) continue;

			// Add metadata to tool to prevent new block breaking event from triggering mining again
			player.setMetadata(LOOP_FIX, new FixedMetadataValue(plugin, true));
			
			BlockBreakEvent event = new BlockBreakEvent(blockAdd, player);
			plugin.getPluginManager().callEvent(event);
			if (event.isCancelled()) continue;
			
			if (!blockAdd.breakNaturally(item)) continue;
			
			player.getInventory().setItemInMainHand(item = plugin.getNMS().damageItem(item, 1, player));
			
			// Add Exp from mined blocks.
			expDrop += event.getExpToDrop();
		}
		e.setExpToDrop(expDrop);
	}
}
