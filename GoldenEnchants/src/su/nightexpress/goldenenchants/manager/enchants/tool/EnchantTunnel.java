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

import su.fogus.engine.config.api.JYML;
import su.fogus.engine.utils.ItemUT;
import su.fogus.engine.utils.LocUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantTunnel extends IEnchantChanceTemplate implements BlockEnchant {

	private static final String LOOP_FIX = "EVENT_STOP";
	private boolean disableOnSneak;
	
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
	public void use(@NotNull ItemStack tool, @NotNull Player p, @NotNull BlockBreakEvent e, int lvl) {
		if (this.disableOnSneak && p.isSneaking()) return;
		
		if (p.hasMetadata(LOOP_FIX)) {
			p.removeMetadata(LOOP_FIX, plugin);
			return;
		}
		
		if (!this.checkTriggerChance(lvl)) return;
		
		BlockFace dir = LocUT.getDirection(p);
		Block block = e.getBlock();
		if (block.getType().isInteractable()) return;
		if (block.getDrops(tool).isEmpty()) return;
		
		boolean isY = dir != null && block.getRelative(dir.getOppositeFace()).isEmpty();
		boolean isZ = dir == BlockFace.EAST || dir == BlockFace.WEST;
		
		for (int i = 0; i < 5; i++) {
			if (ItemUT.isAir(tool)) break;
			
			int xAdd = i == 0 ? 1 : (i == 1 ? -1 : 0);
			int zAdd = i == 2 ? 1 : (i == 3 ? -1 : 0);
			
			Block blockAdd;
			if (isY) {
				blockAdd = block.getLocation().clone().add(isZ ? 0 : xAdd, zAdd, isZ ? xAdd : 0).getBlock();
			}
			else {
				blockAdd = block.getLocation().clone().add(xAdd, 0, zAdd).getBlock();
			}
			
			if (blockAdd.getType().isInteractable()) continue;
			if (blockAdd.getDrops(tool).isEmpty()) continue;
			if (blockAdd.isLiquid()) continue;
			if (blockAdd.getType() == Material.BEDROCK) continue;
			if (blockAdd.getType() == Material.END_PORTAL) continue;
			if (blockAdd.getType() == Material.END_PORTAL_FRAME);
			
			p.setMetadata(LOOP_FIX, new FixedMetadataValue(plugin, "event"));
			BlockBreakEvent event = new BlockBreakEvent(blockAdd, p);
			plugin.getPluginManager().callEvent(event);
			if (event.isCancelled()) continue;
			
			if (!blockAdd.breakNaturally(tool)) continue;
			
			p.getInventory().setItemInMainHand(tool = plugin.getNMS().damageItem(tool, 1, p));
			/*if (dmg != null) {
				PlayerItemDamageEvent event2 = new PlayerItemDamageEvent(p, tool, 1);
				plugin.getPluginManager().callEvent(event2);
				if (event2.isCancelled()) continue;
				
				dmg.setDamage(dmg.getDamage() + 1);
				tool.setItemMeta(meta);
			}*/
		}
	}
}
