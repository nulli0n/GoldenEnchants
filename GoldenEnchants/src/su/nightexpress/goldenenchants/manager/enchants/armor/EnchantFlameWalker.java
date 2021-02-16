package su.nightexpress.goldenenchants.manager.enchants.armor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.manager.api.Cleanable;
import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.manager.EnchantRegister;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.MoveEnchant;

public class EnchantFlameWalker extends IEnchantChanceTemplate implements MoveEnchant, Cleanable {

	private static final BlockFace[] FACES = new BlockFace[] {
			BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST
	};
	
	private static Map<Block, Long> BLOCKS_TO_DESTROY;
	private BlockTickTask blockTickTask;
	
	public EnchantFlameWalker(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		BLOCKS_TO_DESTROY = new HashMap<>();
		this.blockTickTask = new BlockTickTask(plugin);
		this.blockTickTask.start();
	}

	public static void addBlock(@NotNull Block block, int seconds) {
		BLOCKS_TO_DESTROY.put(block, System.currentTimeMillis() + seconds * 1000L);
	}
	
	@Override
	public void clear() {
		if (this.blockTickTask != null) {
			this.blockTickTask.stop();
			this.blockTickTask = null;
		}
	}

	@Override
	public void use(@NotNull PlayerMoveEvent e, @NotNull LivingEntity user, int lvl) {
		if (!this.checkTriggerChance(lvl)) return;
		
		plugin.getNMSHandler().handleFlameWalker(user, user.getLocation(), lvl);
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_BOOTS.contains(item.getType());
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_FEET;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantFlameWalker(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (player.isFlying()) return;
		
		Location from = e.getFrom();
		Location to = e.getTo();
		if (to == null) return;
		if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
			return;
		}
		
		Block bTo = to.getBlock().getRelative(BlockFace.DOWN);
		boolean hasLava = false;
		for (BlockFace face : FACES) {
			if (bTo.getRelative(face).getType() == Material.LAVA) {
				hasLava = true;
				break;
			}
		}
		if (!hasLava) return;
	
		ItemStack boots = player.getInventory().getBoots();
		if (boots == null || ItemUT.isAir(boots)) return;
		
		int level = EnchantManager.getEnchantLevel(boots, EnchantRegister.FLAME_WALKER);
		if (level < 1) return;
		
		EnchantRegister.FLAME_WALKER.use(e, player, level);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFlameWalkerBlock(BlockBreakEvent e) {
		if (BLOCKS_TO_DESTROY.containsKey(e.getBlock())) {
			e.setCancelled(true);
			e.getBlock().setType(Material.LAVA);
		}
	}
	
	class BlockTickTask extends ITask<GoldenEnchants> {

		public BlockTickTask(@NotNull GoldenEnchants plugin) {
			super(plugin, 1, false);
		}

		@Override
		public void action() {
			long now = System.currentTimeMillis();
			
			BLOCKS_TO_DESTROY.keySet().removeIf(block -> {
				if (block.isEmpty()) return true;
				
				long time = BLOCKS_TO_DESTROY.get(block);
				if (now >= time) {
					block.setType(Material.LAVA);
					EffectUT.playEffect(block.getLocation(), Particle.BLOCK_CRACK.name() + ":" + Material.COBBLESTONE.name(), 0.5, 0.7, 0.5, 0.03, 50);
					return true;
				}
				return false;
			});
		}
	}
}
