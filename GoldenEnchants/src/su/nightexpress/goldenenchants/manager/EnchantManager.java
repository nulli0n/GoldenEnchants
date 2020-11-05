package su.nightexpress.goldenenchants.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.fogus.engine.config.api.JYML;
import su.fogus.engine.manager.api.IManager;
import su.fogus.engine.utils.ItemUT;
import su.fogus.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.config.Config;
import su.nightexpress.goldenenchants.manager.enchants.GoldenEnchant;
import su.nightexpress.goldenenchants.manager.listeners.EnchantCombatListener;
import su.nightexpress.goldenenchants.manager.listeners.EnchantGenericListener;
import su.nightexpress.goldenenchants.manager.listeners.EnchantToolListener;
import su.nightexpress.goldenenchants.manager.tasks.ArrowTrailsTask;
import su.nightexpress.goldenenchants.manager.tasks.PassiveEnchantsTask;

public class EnchantManager extends IManager<GoldenEnchants> {

	private Map<String, EnchantTier> tiers;
	private Map<String, List<GoldenEnchant>> tierEnchants;
	
	private EnchantCombatListener combatListener;
	private EnchantToolListener toolListener;
	private EnchantGenericListener genericListener;
	
	private ArrowTrailsTask arrowTrailsTask;
	private PassiveEnchantsTask passiveEnchantsTask;
	
	private static final String META_ENCHANT_ARROW = "GOLDEN_ENCHANTS_ARROW_META_WEAPON";
	
	public EnchantManager(@NotNull GoldenEnchants plugin) {
		super(plugin);
	}
	
	@Override
	public void setup() {
		this.setupTiers();
		EnchantRegister.setup();
		this.sortTierEnchants();
		
		this.combatListener = new EnchantCombatListener(this);
		this.combatListener.registerListeners();
		
		this.toolListener = new EnchantToolListener(this);
		this.toolListener.registerListeners();
		
		this.genericListener = new EnchantGenericListener(this);
		this.genericListener.registerListeners();
		
		//this.registerListeners();
		
		this.arrowTrailsTask = new ArrowTrailsTask(this.plugin);
		this.arrowTrailsTask.start();
		
		this.passiveEnchantsTask = new PassiveEnchantsTask(this.plugin);
		this.passiveEnchantsTask.start();
	}
	
	@Override
	public void shutdown() {
		if (this.arrowTrailsTask != null) {
			this.arrowTrailsTask.stop();
			this.arrowTrailsTask = null;
		}
		if (this.passiveEnchantsTask != null) {
			this.passiveEnchantsTask.stop();
			this.passiveEnchantsTask = null;
		}

		if (this.combatListener != null) {
			this.combatListener.unregisterListeners();
			this.combatListener = null;
		}
		if (this.toolListener != null) {
			this.toolListener.unregisterListeners();
			this.toolListener = null;
		}
		if (this.genericListener != null) {
			this.genericListener.unregisterListeners();
			this.genericListener = null;
		}
		
		if (this.tierEnchants != null) {
			this.tierEnchants.clear();
			this.tierEnchants = null;
		}
		if (this.tiers != null) {
			this.tiers.clear();
			this.tiers = null;
		}
		
		//this.unregisterListeners();
		EnchantRegister.shutdown();
	}
	
	private void setupTiers() {
		this.tiers = new HashMap<>();
		
		JYML cfg = this.plugin.cfg().getJYML();
		for (String sId : cfg.getSection("tiers")) {
			String path = "tiers." + sId + ".";
			String name = cfg.getString(path + "name", sId);
			String color = cfg.getString(path + "color", "&f");
			double chance = cfg.getDouble(path + "chance");
			
			EnchantTier tier = new EnchantTier(sId, name, color, chance);
			this.tiers.put(tier.getId(), tier);
		}
		
		this.plugin.info("Tiers Loaded: " + this.tiers.size());
	}
	
	private void sortTierEnchants() {
		this.tierEnchants = new HashMap<>();
		EnchantRegister.ENCHANT_LIST.forEach(en -> {
			EnchantTier tier = en.getTier();
			String id = tier.getId();
			this.tierEnchants.computeIfAbsent(id, list2 -> new ArrayList<>()).add(en);
		});
	}
	
	public static boolean hasEnchant(@NotNull ItemStack item, @NotNull GoldenEnchant en) {
		return EnchantManager.getEnchantLevel(item, en) != 0;
	}
	
	public static int getEnchantLevel(@NotNull ItemStack item, @NotNull GoldenEnchant en) {
		ItemMeta meta = item.getItemMeta();
		return meta != null ? meta.getEnchantLevel(en) : 0;
	}
	
	public void updateItemLoreEnchants(@NotNull ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;
		
		Map<Enchantment, Integer> enchants;
		if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta meta2 = (EnchantmentStorageMeta) meta;
			enchants = meta2.getStoredEnchants();
		}
		else {
			enchants = meta.getEnchants();
		}
		
		EnchantRegister.ENCHANT_LIST.forEach(ench -> {
			ItemUT.delLore(item, ench.getId());
		});
		
		for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
			if (!(e.getKey() instanceof GoldenEnchant)) continue;
			
			GoldenEnchant ge = (GoldenEnchant) e.getKey();
			ItemUT.addLore(item, ge.getId(), ge.getFormatted(e.getValue()), 0);
		}
	}
	
	public boolean canEnchant(@NotNull ItemStack item, @NotNull GoldenEnchant en, int lvl) {
		if (lvl < 1) return false;
		if (!en.canEnchantItem(item)) return false;
		if (this.getItemGoldenEnchantsAmount(item) >= Config.GEN_ENCHANTS_MAX_FOR_ITEM) return false;
		
		if (lvl < en.getStartLevel()) lvl = en.getStartLevel();
		if (lvl > en.getMaxLevel()) lvl = en.getMaxLevel();
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return false;
		
		if (meta.getEnchants().keySet().stream().anyMatch(e -> en.conflictsWith(e))) {
			return false;
		}
		
		int lvlHas = meta.getEnchantLevel(en);
		if (lvlHas >= lvl) {
			return false;
		}
		
		return true;
	}
	
	public boolean addEnchant(@NotNull ItemStack item, @NotNull GoldenEnchant en, int lvl) {
		if (!this.canEnchant(item, en, lvl)) return false;
		this.removeEnchant(item, en);
		
		ItemUT.addLore(item, en.getId(), en.getFormatted(lvl), 0);
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return false;
		
		if (meta instanceof EnchantmentStorageMeta) {
			((EnchantmentStorageMeta) meta).addStoredEnchant(en, lvl, true);
		}
		else {
			meta.addEnchant(en, lvl, true);
		}
		item.setItemMeta(meta);
		
		return true;
	}
	
	public boolean removeEnchant(@NotNull ItemStack item, @NotNull GoldenEnchant en) {
		ItemUT.delLore(item, en.getId());
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null || !meta.hasEnchant(en)) return false;
		
		meta.removeEnchant(en);
		item.setItemMeta(meta);
		
		return true;
	}
	
	@NotNull
	public Map<GoldenEnchant, Integer> getItemGoldenEnchants(@NotNull ItemStack item) {
		Map<GoldenEnchant, Integer> map = new HashMap<>();
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return map;
		
		Map<Enchantment, Integer> enchs;
		if (meta instanceof EnchantmentStorageMeta) {
			enchs = ((EnchantmentStorageMeta) meta).getStoredEnchants();
		}
		else {
			enchs = meta.getEnchants();
		}
		
		enchs.forEach((en, lvl) -> {
			if (en instanceof GoldenEnchant) {
				map.put((GoldenEnchant) en, lvl);
			}
		});
		
		return map;
	}
	
	public int getItemGoldenEnchantsAmount(@NotNull ItemStack item) {
		return this.getItemGoldenEnchants(item).size();
	}
	
	@Nullable
	public EnchantTier getTierById(@NotNull String id) {
		return this.tiers.get(id.toLowerCase());
	}
	
	@NotNull
	public Collection<EnchantTier> getTiers() {
		return this.tiers.values();
	}
	
	@NotNull
	public List<String> getTierIds() {
		return new ArrayList<>(this.tiers.keySet());
	}
	
	@NotNull
	public List<GoldenEnchant> getTierEnchants(@NotNull String tier) {
		return this.tierEnchants.getOrDefault(tier.toLowerCase(), new ArrayList<>());
	}
	
	@Nullable
	public EnchantTier getTierByChance() {
		Map<EnchantTier, Double> map = new HashMap<>();
		for (EnchantTier tier : this.tiers.values()) {
			if (tier.getChance() <= 0) continue;
			map.put(tier, tier.getChance());
		}
		
		return Rnd.getRandomItem(map, true);
	}
	
	@Nullable
	public GoldenEnchant getEnchantByTier(@NotNull EnchantTier tier) {
		return this.getEnchantByTier(tier.getId(), -1);
	}

	@Nullable
	public GoldenEnchant getEnchantByTier(@NotNull String tier, int expLevel) {
		Map<GoldenEnchant, Double> map = new HashMap<>();
		
		this.getTierEnchants(tier).stream().filter(en -> {
			return en.getEnchantmentChance() > 0 && 
					(expLevel < 0 || en.getTableMinPlayerLevel() <= expLevel);
		}).forEach(en -> map.put(en, en.getEnchantmentChance()));
		
		return map.isEmpty() ? null : Rnd.getRandomItem(map, true);
	}
	
	public void setArrowWeapon(@NotNull Projectile pj, @NotNull ItemStack bow) {
		pj.setMetadata(META_ENCHANT_ARROW, new FixedMetadataValue(plugin, bow));
	}
	
	@Nullable
	public ItemStack getArrowWeapon(@NotNull Projectile pj) {
		if (pj.hasMetadata(META_ENCHANT_ARROW)) {
			return (ItemStack) pj.getMetadata(META_ENCHANT_ARROW).get(0).value();
		}
		return null;
	}
}
