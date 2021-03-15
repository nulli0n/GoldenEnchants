package su.nightexpress.goldenenchants.manager.enchants.tool;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.MsgUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.InteractEnchant;

public class EnchantReplanter extends IEnchantChanceTemplate implements InteractEnchant, BlockEnchant {

	private boolean replantOnRightClick;
	private boolean replantOnPlantBreak;
	
	private static final Set<Material> CROPS = Sets.newHashSet(
			Material.WHEAT_SEEDS,
			Material.BEETROOT_SEEDS,
			Material.MELON_SEEDS,
			Material.PUMPKIN_SEEDS,
			Material.POTATO,
			Material.CARROT
			);
	
	public EnchantReplanter(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.replantOnRightClick = cfg.getBoolean("settings.replant.on-right-click");
		this.replantOnPlantBreak = cfg.getBoolean("settings.replant.on-plant-break");
	}

	@Override
	public void use(@NotNull Player player, @NotNull ItemStack item, @NotNull PlayerInteractEvent e, int lvl) {
		if (!this.replantOnRightClick) return;
		
		// Check for a event hand. We dont want to trigger it twice.
		if (e.getHand() != EquipmentSlot.HAND) return;
		
		// Check if player holds seeds to plant them by offhand interaction.
		ItemStack off = player.getInventory().getItemInOffHand();
		if (!ItemUT.isAir(off) && CROPS.contains(off.getType())) return;
		
		// Check if clicked block is a farmland.
		Block blockGround = e.getClickedBlock();
		if (blockGround == null || blockGround.getType() != Material.FARMLAND) return;
		
		// Check enchantment trigger chance.
		if (!this.checkTriggerChance(lvl)) return;
		
		// Check if someting is already growing on the farmland.
		Block blockPlant = blockGround.getRelative(BlockFace.UP);
		if (!blockPlant.isEmpty()) return;
		
		// Get the first crops from player's inventory and plant them.
		for (Material seed : CROPS) {
			if (this.takeSeeds(player, seed)) {
				plugin.getNMS().sendAttackPacket(player, 0);
				blockPlant.setType(this.fineSeedsToBlock(seed));
				MsgUT.sound(player, Sound.ITEM_CROP_PLANT);
				break;
			}
		}
	}

	@Override
	public void use(@NotNull ItemStack tool, @NotNull Player p, @NotNull BlockBreakEvent e,
			int lvl) {
		
		if (!this.replantOnPlantBreak) return;
		
		// Check if broken block is supported crop(s).
		Block blockPlant = e.getBlock();
		if (!CROPS.contains(this.fineBlockToSeeds(blockPlant.getType()))) return;
		
		// Check if broken block is actually can grow.
		BlockData dataPlant = blockPlant.getBlockData();
		if (!(dataPlant instanceof Ageable)) return;
		
		// Check enchantment trigger chance.
		if (!this.checkTriggerChance(lvl)) return;
		
		// Check if crop is not at its maximal age to prevent accidient replant.
		Ageable plant = (Ageable) dataPlant;
		if (plant.getAge() < plant.getMaximumAge()) {
			e.setCancelled(true);
			return;
		}
		
		// Replant the gathered crops with a new one.
		if (this.takeSeeds(p, plant.getMaterial())) {
			plugin.getServer().getScheduler().runTask(plugin, () -> {
				blockPlant.setType(plant.getMaterial());
				plant.setAge(0);
				blockPlant.setBlockData(plant);
			});
		}
	}
	
	@NotNull
	private Material fineSeedsToBlock(@NotNull Material material) {
		if (material == Material.POTATO) return Material.POTATOES;
		if (material == Material.CARROT) return Material.CARROTS;
		if (material == Material.BEETROOT_SEEDS) return Material.BEETROOTS;
		if (material == Material.WHEAT_SEEDS) return Material.WHEAT;
		if (material == Material.PUMPKIN_SEEDS) return Material.PUMPKIN_STEM;
		if (material == Material.MELON_SEEDS) return Material.MELON_STEM;
		return material;
	}
	
	@NotNull
	private Material fineBlockToSeeds(@NotNull Material material) {
		if (material == Material.POTATOES) return Material.POTATO;
		if (material == Material.CARROTS) return Material.CARROT;
		if (material == Material.BEETROOTS) return Material.BEETROOT_SEEDS;
		if (material == Material.WHEAT) return Material.WHEAT_SEEDS;
		if (material == Material.MELON_STEM) return Material.MELON_SEEDS;
		if (material == Material.PUMPKIN_STEM) return Material.PUMPKIN_SEEDS;
		return material;
	}
	
	private boolean takeSeeds(@NotNull Player player, @NotNull Material material) {
		material = this.fineBlockToSeeds(material);
		
		int slot = player.getInventory().first(material);
		if (slot < 0) return false;
		
		ItemStack seed = player.getInventory().getItem(slot);
		if (seed == null || ItemUT.isAir(seed)) return false;
		
		seed.setAmount(seed.getAmount() - 1);
		return true;
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_HOES.contains(item.getType());
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
}
