package su.nightexpress.goldenenchants.manager.enchants.armor;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.manager.EnchantRegister;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.MoveEnchant;

public class EnchantFlameWalker extends IEnchantChanceTemplate implements MoveEnchant {

	private static final BlockFace[] FACES = new BlockFace[] {
			BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST
	};

	public EnchantFlameWalker(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
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
}
