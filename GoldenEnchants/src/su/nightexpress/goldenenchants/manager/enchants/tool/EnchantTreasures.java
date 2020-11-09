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
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantTreasures extends IEnchantChanceTemplate implements BlockEnchant {
	
	private Map<Material, Map<Material, Double>> treasures;
	
	public EnchantTreasures(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.treasures = new HashMap<>();
		
    	for (String sFrom : cfg.getSection("settings.treasures")) {
    		Material mFrom = Material.getMaterial(sFrom.toUpperCase());
    		if (mFrom == null) {
    			plugin.error("[Treasures] Invalid source material '" + sFrom + "' !");
    			continue;
    		}
    		Map<Material, Double> treasuresList = new HashMap<>();
    		
    		for (String sTo : cfg.getSection("settings.treasures." + sFrom)) {
    			Material mTo = Material.getMaterial(sTo.toUpperCase());
        		if (mTo == null) {
        			plugin.error("[Treasures] Invalid result material '" + sTo + "' for '" + sFrom + "' !");
        			continue;
        		}
    			
    			double tChance = cfg.getDouble("settings.treasures." + sFrom + "." + sTo);
    			treasuresList.put(mTo, tChance);
    		}
    		this.treasures.put(mFrom, treasuresList);
    	}
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		Material mat = item.getType();
		return ITEM_PICKAXES.contains(mat) || ITEM_SHOVELS.contains(mat) || ITEM_AXES.contains(mat);
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
	
	@Nullable
    public final ItemStack getTreasure(@NotNull Block b) {
		Map<Material, Double> treasures = this.treasures.get(b.getType());
		if (treasures == null) return null;
		
		Material mat = Rnd.getRandomItem(treasures, true);
		return mat != null ? new ItemStack(mat) : null;
    }

	@Override
	public void use(@NotNull ItemStack tool, @NotNull Player p, @NotNull BlockBreakEvent e,
			int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		Block b = e.getBlock();
		
	    ItemStack item = this.getTreasure(b);
	    if (item == null) return;
	    
	    Location loc = LocUT.getCenter(b.getLocation());
	    b.getWorld().dropItemNaturally(loc, item);
	    b.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 0.7f);
	    EffectUT.playEffect(loc, "VILLAGER_HAPPY", 0.2f, 0.2f, 0.2f, 0.12f, 20);
	}
}
