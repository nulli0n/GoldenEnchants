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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
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
	
	private static final String META_USER_BLOCK = "GOLDENENCHANTS_TREASURES_FAKE_BLOCK";
	
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
    public final ItemStack getTreasure(@NotNull Block block) {
		Map<Material, Double> treasures = this.treasures.get(block.getType());
		if (treasures == null) return null;
		
		Material mat = Rnd.get(treasures);
		return mat != null ? new ItemStack(mat) : null;
    }

	@Override
	public void use(@NotNull BlockBreakEvent e, @NotNull Player player, @NotNull ItemStack item,
			int lvl) {
		
		if (!this.checkTriggerChance(lvl)) return;
		
		Block block = e.getBlock();
		if (block.hasMetadata(META_USER_BLOCK)) return;
		
	    ItemStack itemDrop = this.getTreasure(block);
	    if (itemDrop == null) return;
	    
	    Location loc = LocUT.getCenter(block.getLocation());
	    block.getWorld().dropItem(loc, itemDrop);
	    block.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 0.7f);
	    EffectUT.playEffect(loc, "VILLAGER_HAPPY", 0.2f, 0.2f, 0.2f, 0.12f, 20);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockDuplicationFix(BlockPlaceEvent e) {
		Block block = e.getBlock();
		Map<Material, Double> treasures = this.treasures.get(block.getType());
		if (treasures == null) return;
		
		block.setMetadata(META_USER_BLOCK, new FixedMetadataValue(plugin, true));
	}
}
