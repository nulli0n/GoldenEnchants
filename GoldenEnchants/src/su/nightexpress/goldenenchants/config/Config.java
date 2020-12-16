package su.nightexpress.goldenenchants.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.IConfigTemplate;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.EnchantTier;
import su.nightexpress.goldenenchants.manager.enchants.api.type.ObtainType;

public class Config extends IConfigTemplate {
	
	public Config(@NotNull GoldenEnchants plugin) {
		super(plugin);
	}
	
	public static long GEN_TASK_ARROW_TRAIL_TICK_TIME;
	public static long GEN_TASK_PASSIVE_ENCHANT_TICK_TIME;
	
	public static Set<String> GEN_ENCHANTS_DISABLED;
	public static int GEN_ENCHANTS_MAX_FOR_ITEM;
	public static boolean GEN_ENCHANTS_SWORDS_TO_AXES;
	public static boolean GEN_ENCHANTS_BOWS_TO_CROSSBOWS;
	public static boolean GEN_ENCHANTS_ELYTRA_TO_CHESTPLATE;
	public static boolean GEN_ENCHANTS_PASSIVE_FOR_MOBS;
	
	public static int GEN_TABLE_MAX_ENCHANTS;
	public static double GEN_TABLE_ENCHANT_CHANCE;
	
	public static boolean 		VILLAGERS_ENABLED;
	public static int 			VILLAGERS_ENCHANTS_TOTAL_MAX;
	public static double 		VILLAGERS_ENCHANTS_GOLDEN_ACQUIRE_CHANCE;
	public static int 			VILLAGERS_ENCHANTS_GOLDEN_MIN;
	public static int 			VILLAGERS_ENCHANTS_GOLDEN_MAX;
	
	public static boolean 		LOOTGEN_ENABLED;
	public static int 			LOOTGEN_ENCHANTS_TOTAL_MAX;
	public static double 		LOOTGEN_ENCHANTS_GOLDEN_POPULATE_CHANCE;
	public static int 			LOOTGEN_ENCHANTS_GOLDEN_MIN;
	public static int 			LOOTGEN_ENCHANTS_GOLDEN_MAX;
	
	private static Map<String, EnchantTier> ENCHANT_TIERS;
	
	@Override
    public void load() {
    	String path = "general.tasks.";
    	GEN_TASK_ARROW_TRAIL_TICK_TIME = cfg.getLong(path + "arrow-trails.tick-time", 1);
    	GEN_TASK_PASSIVE_ENCHANT_TICK_TIME = cfg.getLong(path + "passive-enchants.tick-time", 100);
    	
    	path = "general.enchantments.";
    	cfg.addMissing(path + "elytra-as-chestplate", true);
    	cfg.addMissing(path + "disabled", Arrays.asList("enchant_name1", "enchant_name2"));
    	cfg.addMissing(path + "allow-passive-enchants-for-mobs", false);
    	
    	GEN_ENCHANTS_DISABLED = cfg.getStringSet(path + "disabled").stream()
    			.map(String::toLowerCase).collect(Collectors.toSet());
    	GEN_ENCHANTS_MAX_FOR_ITEM = cfg.getInt(path + "max-item-golden-enchants", 3);
    	GEN_ENCHANTS_SWORDS_TO_AXES = cfg.getBoolean(path + "sword-enchants-applies-to-axes");
    	GEN_ENCHANTS_BOWS_TO_CROSSBOWS = cfg.getBoolean(path + "bow-enchants-applies-to-crossbows");
    	GEN_ENCHANTS_ELYTRA_TO_CHESTPLATE = cfg.getBoolean(path + "elytra-as-chestplate");
    	GEN_ENCHANTS_PASSIVE_FOR_MOBS = cfg.getBoolean(path + "allow-passive-enchants-for-mobs");
    	
    	path = "general.enchantment-table.";
    	GEN_TABLE_MAX_ENCHANTS = cfg.getInt(path + "max-golden-enchants", 2);
    	GEN_TABLE_ENCHANT_CHANCE = cfg.getDouble(path + "golden-enchant-chance", 25.0);
    	
    	path = "general.villagers.";
    	cfg.addMissing(path + "enabled", true);
    	cfg.addMissing(path + "enchantments.total-maximum", 3);
    	cfg.addMissing(path + "enchantments.golden-acquire-chance", 25D);
    	cfg.addMissing(path + "enchantments.golden-minimum", 0);
    	cfg.addMissing(path + "enchantments.golden-maximum", 2);
    	
    	if (VILLAGERS_ENABLED = cfg.getBoolean(path + "enabled", true)) {
    		VILLAGERS_ENCHANTS_TOTAL_MAX = cfg.getInt(path + "enchantments.total-maximum", 3);
    		VILLAGERS_ENCHANTS_GOLDEN_ACQUIRE_CHANCE = cfg.getDouble(path + "enchantments.golden-acquire-chance", 25D);
    		VILLAGERS_ENCHANTS_GOLDEN_MIN = cfg.getInt(path + "enchantments.golden-minimum", 0);
    		VILLAGERS_ENCHANTS_GOLDEN_MAX = cfg.getInt(path + "enchantments.golden-maximum", 2);
    	}
    	
    	path = "general.loot-generation.";
    	cfg.addMissing(path + "enabled", true);
    	cfg.addMissing(path + "enchantments.total-maximum", 3);
    	cfg.addMissing(path + "enchantments.golden-populate-chance", 25D);
    	cfg.addMissing(path + "enchantments.golden-minimum", 0);
    	cfg.addMissing(path + "enchantments.golden-maximum", 2);
    	
    	if (LOOTGEN_ENABLED = cfg.getBoolean(path + "enabled", true)) {
    		LOOTGEN_ENCHANTS_TOTAL_MAX = cfg.getInt(path + "enchantments.total-maximum", 3);
    		LOOTGEN_ENCHANTS_GOLDEN_POPULATE_CHANCE = cfg.getDouble(path + "enchantments.golden-populate-chance", 25D);
    		LOOTGEN_ENCHANTS_GOLDEN_MIN = cfg.getInt(path + "enchantments.golden-minimum", 0);
    		LOOTGEN_ENCHANTS_GOLDEN_MAX = cfg.getInt(path + "enchantments.golden-maximum", 2);
    	}
    	
    	this.setupTiers();
    }
	
	private void setupTiers() {
    	ENCHANT_TIERS = new HashMap<>();
    	
		// No tiers defined, setup a default one.
		// Every enchantment must have a tier.
		if (cfg.getSection("tiers").isEmpty()) {
			this.plugin.info("No tiers defined! Creating a default one for you...");
			cfg.set("tiers.default.name", "&7Default");
			cfg.set("tiers.default.color", "&7");
			for (ObtainType obtainType : ObtainType.values()) {
				cfg.set("tiers.default.obtain-chance." + obtainType.name(), 100D);
			}
		}
    	
		// Load existing tiers.
		for (String sId : cfg.getSection("tiers")) {
			String path = "tiers." + sId + ".";
			
			double chanceOld = cfg.getDouble(path + "chance", -1D);
			String name = cfg.getString(path + "name", sId);
			String color = cfg.getString(path + "color", "&f");
			Map<ObtainType, Double> chance = new HashMap<>();
			
			cfg.remove(path + "chance"); // Remove old option.
			
			for (ObtainType obtainType : ObtainType.values()) {
				cfg.addMissing(path + "obtain-chance." + obtainType.name(), chanceOld);
				
				double chanceType = cfg.getDouble(path + "obtain-chance." + obtainType.name());
				chance.put(obtainType, chanceType);
			}
			
			EnchantTier tier = new EnchantTier(sId, name, color, chance);
			ENCHANT_TIERS.put(tier.getId(), tier);
		}
		
		this.plugin.info("Tiers Loaded: " + ENCHANT_TIERS.size());
	}
	
	@Nullable
	public static EnchantTier getTierById(@NotNull String id) {
		return ENCHANT_TIERS.get(id.toLowerCase());
	}
	
	@NotNull
	public static Collection<EnchantTier> getTiers() {
		return ENCHANT_TIERS.values();
	}
	
	@NotNull
	public static List<String> getTierIds() {
		return new ArrayList<>(ENCHANT_TIERS.keySet());
	}
	
	@Nullable
	public static EnchantTier getTierByChance(@NotNull ObtainType obtainType) {
		Map<EnchantTier, Double> map = new HashMap<>();
		getTiers().stream().forEach(tier -> map.put(tier, tier.getChance(obtainType)));
		
		return Rnd.get(map);
	}
	
	public static int getPopulationEnchantsTotalMax(@NotNull ObtainType obtainType) {
		if (obtainType == ObtainType.VILLAGER) return VILLAGERS_ENCHANTS_TOTAL_MAX;
		if (obtainType == ObtainType.LOOT_GENERATION) return LOOTGEN_ENCHANTS_TOTAL_MAX;
		return 0;
	}
	
	public static double getPopulationEnchantsChance(@NotNull ObtainType obtainType) {
		if (obtainType == ObtainType.VILLAGER) return VILLAGERS_ENCHANTS_GOLDEN_ACQUIRE_CHANCE;
		if (obtainType == ObtainType.LOOT_GENERATION) return LOOTGEN_ENCHANTS_GOLDEN_POPULATE_CHANCE;
		return 0D;
	}
	
	public static int getPopulationEnchantsGoldenMin(@NotNull ObtainType obtainType) {
		if (obtainType == ObtainType.VILLAGER) return VILLAGERS_ENCHANTS_GOLDEN_MIN;
		if (obtainType == ObtainType.LOOT_GENERATION) return LOOTGEN_ENCHANTS_GOLDEN_MIN;
		return 0;
	}
	
	public static int getPopulationEnchantsGoldenMax(@NotNull ObtainType obtainType) {
		if (obtainType == ObtainType.VILLAGER) return VILLAGERS_ENCHANTS_GOLDEN_MAX;
		if (obtainType == ObtainType.LOOT_GENERATION) return LOOTGEN_ENCHANTS_GOLDEN_MAX;
		return 0;
	}
}
