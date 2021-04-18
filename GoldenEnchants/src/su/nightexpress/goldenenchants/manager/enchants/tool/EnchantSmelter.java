package su.nightexpress.goldenenchants.manager.enchants.tool;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.LocUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantSmelter extends IEnchantChanceTemplate implements BlockEnchant {
	
	private Map<Material, Material> smeltingTable;
	
	public EnchantSmelter(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.smeltingTable = new HashMap<>();
    	for (String sFrom : cfg.getSection("settings.smelting-table")) {
    		Material mFrom = Material.getMaterial(sFrom.toUpperCase());
    		if (mFrom == null) {
    			plugin.error("[Smelter] Invalid source material '" + sFrom + "' !");
    			continue;
    		}
    		String sTo = cfg.getString("settings.smelting-table." + sFrom, "");
    		Material mTo = Material.getMaterial(sTo.toUpperCase());
    		if (mTo == null) {
    			plugin.error("[Smelter] Invalid result material '" + sTo + "' !");
    			continue;
    		}
    		this.smeltingTable.put(mFrom, mTo);
    	}
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		Material mat = item.getType();
		return ITEM_PICKAXES.contains(mat) || ITEM_SHOVELS.contains(mat) || ITEM_AXES.contains(mat);
	}
	
	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return en == Enchantment.SILK_TOUCH;
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
	public void use(@NotNull BlockBreakEvent e, @NotNull Player player, @NotNull ItemStack item,
			int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		Block block = e.getBlock();
		
		Material result = this.smeltingTable.get(block.getType());
    	if (result == null) return;
		
    	ItemStack itemSmelt = new ItemStack(result);
    	
		e.setCancelled(true);
	    block.setType(Material.AIR);
	    
	    Location loc = LocUT.getCenter(block.getLocation(), false);
	    block.getWorld().dropItem(loc, itemSmelt);
		block.getWorld().playSound(loc, Sound.BLOCK_LAVA_EXTINGUISH, 0.7f, 0.7f);
		EffectUT.playEffect(loc, "FLAME", 0.2f, 0.2f, 0.2f, 0.03f, 30);
		
		player.getInventory().setItemInMainHand(plugin.getNMS().damageItem(item, 1, player));
	}
}
