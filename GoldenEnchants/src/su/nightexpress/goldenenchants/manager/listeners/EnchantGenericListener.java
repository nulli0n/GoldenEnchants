package su.nightexpress.goldenenchants.manager.listeners;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.IListener;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.config.Config;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.manager.enchants.EnchantTier;
import su.nightexpress.goldenenchants.manager.enchants.GoldenEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.type.ObtainType;

public class EnchantGenericListener extends IListener<GoldenEnchants> {
	
	//private EnchantManager enchantManager;
	
	public EnchantGenericListener(@NotNull EnchantManager enchantManager) {
		super(enchantManager.plugin);
		//this.enchantManager = enchantManager;
	}

	// ---------------------------------------------------------------
	// Handle Anvil
	// ---------------------------------------------------------------
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
		
		Map<GoldenEnchant, Integer> enchAdd = EnchantManager.getItemGoldenEnchants(source);
		int cost = inv.getRepairCost();
		
		if (book != null && (book.getType() == Material.ENCHANTED_BOOK || book.getType() == source.getType())) {
			for (Map.Entry<GoldenEnchant, Integer> en : EnchantManager.getItemGoldenEnchants(book).entrySet()) {
				enchAdd.merge(en.getKey(), en.getValue(), (oldLvl, newLvl) -> (oldLvl == newLvl) ? (oldLvl + 1) : (Math.max(oldLvl, newLvl)));
			}
		}
		
		for (Map.Entry<GoldenEnchant, Integer> ent : enchAdd.entrySet()) {
			GoldenEnchant enchant = ent.getKey();
			int lvl = Math.min(enchant.getMaxLevel(), ent.getValue());
			if (EnchantManager.addEnchant(result, enchant, lvl, false)) {
				cost += enchant.getAnvilMergeCost(lvl);
			}
		}
		
		if (!source.equals(result)) {
			EnchantManager.updateItemLoreEnchants(result);
			e.setResult(result);
			
			// NMS ContainerAnvil will set level cost to 0 right after calling the event
			// So we have to change it with a 1 tick delay.
			final int cost2 = cost;
			this.plugin.getServer().getScheduler().runTask(plugin, () -> inv.setRepairCost(cost2));
		}
	}
	
	// ---------------------------------------------------------------
	// Update enchantment lore after grindstone
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchantGrindStone(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		InventoryView top = p.getOpenInventory();
		if (top.getType() != InventoryType.GRINDSTONE) return;
		
		this.plugin.getServer().getScheduler().runTask(plugin, () -> {
			ItemStack result = top.getItem(2);
			if (result == null || ItemUT.isAir(result)) return;
			
			EnchantManager.updateItemLoreEnchants(result);
		});
	}
	
	// ---------------------------------------------------------------
	// Handle Enchanting Table
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEnchantEnchantmentTable(final EnchantItemEvent e) {
		final ItemStack target = e.getItem();
		boolean enchantAdded = false;
		int roll = Rnd.get(Config.GEN_TABLE_MAX_ENCHANTS + 1);
		
		for (int count = 0; count < roll; count++) {
			if (Rnd.get(true) > Config.GEN_TABLE_ENCHANT_CHANCE) continue;
			
			EnchantTier tier = EnchantManager.getTierByChance(ObtainType.ENCHANTING);
			if (tier == null) continue;
			
			GoldenEnchant enchant = tier.getEnchant(e.getExpLevelCost());
			if (enchant == null) continue;
			if (e.getEnchantsToAdd().keySet().stream().anyMatch(add -> add.conflictsWith(enchant) || enchant.conflictsWith(add))) continue;
			
			int lvl = Rnd.get(enchant.getStartLevel(), enchant.getMaxLevel());
			if (!EnchantManager.canEnchant(target, enchant, lvl)) continue;
			
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
				
				EnchantManager.updateItemLoreEnchants(result);
				e.getInventory().setItem(0, result);
			});
		}
	}
	
	// ---------------------------------------------------------------
	// Adding Golden Enchants to Villagers
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchantVillagerAcquire(VillagerAcquireTradeEvent e) {
		if (!Config.VILLAGERS_ENABLED) return;
		
		MerchantRecipe recipe = e.getRecipe();
		ItemStack result = recipe.getResult();
		
		if (!EnchantManager.isEnchantable(result)) return;
		EnchantManager.populateEnchantments(result, ObtainType.VILLAGER);
		
		int uses = recipe.getUses();
		int maxUses = recipe.getMaxUses();
		boolean expReward = recipe.hasExperienceReward();
		int villagerExperience = recipe.getVillagerExperience();
		float priceMultiplier = recipe.getPriceMultiplier();
		
		MerchantRecipe recipe2 = new MerchantRecipe(result, uses, maxUses, expReward, villagerExperience, priceMultiplier);
		e.setRecipe(recipe2);
	}
}
