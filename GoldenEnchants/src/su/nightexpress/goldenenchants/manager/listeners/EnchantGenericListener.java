package su.nightexpress.goldenenchants.manager.listeners;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.IListener;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.config.Config;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.manager.EnchantRegister;
import su.nightexpress.goldenenchants.manager.EnchantTier;
import su.nightexpress.goldenenchants.manager.enchants.GoldenEnchant;

public class EnchantGenericListener extends IListener<GoldenEnchants> {

	private static final BlockFace[] FACES = new BlockFace[] {
			BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};
	
	private EnchantManager enchantManager;
	
	public EnchantGenericListener(@NotNull EnchantManager enchantManager) {
		super(enchantManager.plugin);
		this.enchantManager = enchantManager;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEnchantAnvil(PrepareAnvilEvent e) {
		AnvilInventory inv = e.getInventory();
		
		ItemStack source = inv.getItem(0);
		ItemStack book = inv.getItem(1);
		
		if (source == null || source.getType() == Material.AIR) return;
		if (book != null && book.getType() != Material.ENCHANTED_BOOK && book.getAmount() > 1) return;
		if (source.getType() != Material.ENCHANTED_BOOK && source.getAmount() > 1) return;
		
		ItemStack result = e.getResult();
		if (result == null || result.getType() == Material.AIR) {
			result = new ItemStack(source);
		}
		
		Map<GoldenEnchant, Integer> enchAdd = this.enchantManager.getItemGoldenEnchants(source);
		int cost = inv.getRepairCost();
		
		if (book != null && (book.getType() == Material.ENCHANTED_BOOK || book.getType() == source.getType())) {
			for (Map.Entry<GoldenEnchant, Integer> en : this.enchantManager.getItemGoldenEnchants(book).entrySet()) {
				enchAdd.merge(en.getKey(), en.getValue(), (oldLvl, newLvl) -> (oldLvl == newLvl) ? (oldLvl + 1) : (Math.max(oldLvl, newLvl)));
			}
		}
		
		for (Map.Entry<GoldenEnchant, Integer> ent : enchAdd.entrySet()) {
			int lvl = Math.min(ent.getKey().getMaxLevel(), ent.getValue());
			if (this.enchantManager.addEnchant(result, ent.getKey(), lvl)) {
				cost += lvl;
			}
		}
		
		if (!source.equals(result)) {
			this.enchantManager.updateItemLoreEnchants(result);
			e.setResult(result);
			
			// Fix for enchant books with invalid enchantments.
			// NMS will set level cost to 0 AFTER calling the event.
			final int cost2 = cost;
			this.plugin.getServer().getScheduler().runTask(plugin, () -> inv.setRepairCost(cost2));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchantGrindStone(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		InventoryView top = p.getOpenInventory();
		if (top.getType() != InventoryType.GRINDSTONE) return;
		
		this.plugin.getServer().getScheduler().runTask(plugin, () -> {
			ItemStack result = top.getItem(2);
			if (result == null || ItemUT.isAir(result)) return;
			
			this.enchantManager.updateItemLoreEnchants(result);
		});
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEnchantEnchantmentTable(final EnchantItemEvent e) {
		final ItemStack target = e.getItem();
		boolean enchantAdded = false;
		int roll = Rnd.get(Config.GEN_TABLE_MAX_ENCHANTS + 1);
		
		for (int count = 0; count < roll; count++) {
			if (Rnd.get(true) > Config.GEN_TABLE_ENCHANT_CHANCE) continue;
			
			EnchantTier tier = this.enchantManager.getTierByChance();
			if (tier == null) continue;
			
			GoldenEnchant enchant = this.enchantManager.getEnchantByTier(tier.getId(), e.getExpLevelCost());
			if (enchant == null) continue;
			
			int lvl = Rnd.get(enchant.getStartLevel(), enchant.getMaxLevel());
			if (!this.enchantManager.canEnchant(target, enchant, lvl)) continue;
			
			e.getEnchantsToAdd().put(enchant, lvl);
			enchantAdded = true;
		}
		
		if (enchantAdded) {
			plugin.getServer().getScheduler().runTask(plugin, () -> {
				ItemStack result = e.getInventory().getItem(0);
				if (result == null) return;
				
				// Fix enchantments for Enchant Books.
				// Enchants are not added on book because they do not exists in NMS.
				// Server gets enchants from NMS to apply it on Book NBT tags.
				ItemMeta meta = result.getItemMeta();
				if (meta instanceof EnchantmentStorageMeta) {
					EnchantmentStorageMeta meta2 = (EnchantmentStorageMeta) meta;
					e.getEnchantsToAdd().forEach((en, lvl) -> {
						if (!meta2.hasStoredEnchant(en)) {
							meta2.addStoredEnchant(en, lvl, true);
						}
					});
					result.setItemMeta(meta2);
				}
				
				this.enchantManager.updateItemLoreEnchants(result);
				e.getInventory().setItem(0, result);
			});
		}
	}
	
	// ---------------------------------------------------------------
	// Movement Enchants
	// ---------------------------------------------------------------
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
		
		ItemMeta meta = boots.getItemMeta();
		if (meta == null) return;
		
		int level = meta.getEnchants().getOrDefault(EnchantRegister.FLAME_WALKER, 0);
		if (level < 1) return;
		
		EnchantRegister.FLAME_WALKER.use(e, player, level);
	}
}
