package su.nightexpress.goldenenchants.config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.IConfigTemplate;
import su.nightexpress.goldenenchants.GoldenEnchants;

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
    }
}
