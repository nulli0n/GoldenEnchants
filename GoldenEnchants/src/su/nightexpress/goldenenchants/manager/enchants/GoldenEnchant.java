package su.nightexpress.goldenenchants.manager.enchants;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.core.Version;
import su.nexmedia.engine.utils.NumberUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.eval.Evaluator;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.config.Config;
import su.nightexpress.goldenenchants.manager.EnchantTier;

public abstract class GoldenEnchant extends Enchantment {

	protected final GoldenEnchants plugin;
	protected final JYML cfg;
	protected final String id;
	
	protected String display;
	protected EnchantTier tier;
	protected int minLvl;
	protected int maxLvl;
	protected int tablePlayerLvl;
	protected double tableEnchantChance;
	
	protected static final Set<Material> ITEM_SWORDS;
	protected static final Set<Material> ITEM_AXES;
	protected static final Set<Material> ITEM_PICKAXES;
	protected static final Set<Material> ITEM_SHOVELS;
	protected static final Set<Material> ITEM_HOES;
	protected static final Set<Material> ITEM_HELMETS;
	protected static final Set<Material> ITEM_CHESTPLATES;
	protected static final Set<Material> ITEM_LEGGINGS;
	protected static final Set<Material> ITEM_BOOTS;
	
	static {
		ITEM_SWORDS = Sets.newHashSet(Material.DIAMOND_SWORD, Material.GOLDEN_SWORD,
										Material.IRON_SWORD, Material.STONE_SWORD,
										Material.WOODEN_SWORD);
		
		ITEM_AXES = Sets.newHashSet(Material.DIAMOND_AXE, Material.GOLDEN_AXE,
										Material.IRON_AXE, Material.STONE_AXE,
										Material.WOODEN_AXE);
		
		ITEM_PICKAXES = Sets.newHashSet(Material.DIAMOND_PICKAXE, Material.GOLDEN_PICKAXE,
				Material.IRON_PICKAXE, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE);
		
		ITEM_SHOVELS= Sets.newHashSet(Material.DIAMOND_SHOVEL, Material.GOLDEN_SHOVEL,
				Material.IRON_SHOVEL, Material.STONE_SHOVEL, Material.WOODEN_SHOVEL);
		
		ITEM_HOES = Sets.newHashSet(Material.DIAMOND_HOE, Material.GOLDEN_HOE,
				Material.IRON_HOE, Material.STONE_HOE, Material.WOODEN_HOE);
		
		ITEM_HELMETS = Sets.newHashSet(Material.CHAINMAIL_HELMET, Material.DIAMOND_HELMET,
				Material.GOLDEN_HELMET, Material.IRON_HELMET, Material.LEATHER_HELMET,
				Material.PLAYER_HEAD, Material.CREEPER_HEAD, Material.ZOMBIE_HEAD,
				Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD,
				Material.TURTLE_HELMET);
		
		ITEM_CHESTPLATES = Sets.newHashSet(Material.CHAINMAIL_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
				Material.GOLDEN_CHESTPLATE, Material.IRON_CHESTPLATE, Material.LEATHER_CHESTPLATE);
		
		ITEM_LEGGINGS = Sets.newHashSet(Material.CHAINMAIL_LEGGINGS, Material.DIAMOND_LEGGINGS,
				Material.GOLDEN_LEGGINGS, Material.IRON_LEGGINGS, Material.LEATHER_LEGGINGS);
		
		ITEM_BOOTS = Sets.newHashSet(Material.CHAINMAIL_BOOTS, Material.DIAMOND_BOOTS,
				Material.GOLDEN_BOOTS, Material.IRON_BOOTS, Material.LEATHER_BOOTS);
		
		if (Config.GEN_ENCHANTS_ELYTRA_TO_CHESTPLATE) {
    		ITEM_CHESTPLATES.add(Material.ELYTRA);
    	}
		
		if (Version.CURRENT.isHigher(Version.V1_15_R1)) {
			ITEM_SWORDS.add(Material.NETHERITE_SWORD);
			ITEM_AXES.add(Material.NETHERITE_AXE);
			ITEM_PICKAXES.add(Material.NETHERITE_PICKAXE);
			ITEM_SHOVELS.add(Material.NETHERITE_SHOVEL);
			ITEM_HOES.add(Material.NETHERITE_HOE);
			ITEM_HELMETS.add(Material.NETHERITE_HELMET);
			ITEM_CHESTPLATES.add(Material.NETHERITE_CHESTPLATE);
			ITEM_LEGGINGS.add(Material.NETHERITE_LEGGINGS);
			ITEM_BOOTS.add(Material.NETHERITE_BOOTS);
		}
	}
	
	public GoldenEnchant(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(NamespacedKey.minecraft(cfg.getFile().getName().replace(".yml", "").toLowerCase()));
		this.plugin = plugin;
		this.id = this.getKey().getKey();
		this.cfg = cfg;
		
		this.display = StringUT.color(cfg.getString("name", this.getId()));
		this.tier = plugin.getEnchantManager().getTierById(cfg.getString("tier", "null"));
		if (this.tier == null) {
			throw new IllegalStateException("Invalid tier provided for '" + id + "' enchantment!");
		}
		
		this.minLvl = cfg.getInt("level.min");
		this.maxLvl = cfg.getInt("level.max");
		this.tablePlayerLvl = cfg.getInt("enchantment-table.min-player-level");
		this.tableEnchantChance = cfg.getDouble("enchantment-table.chance");
	}
	
	@NotNull
	public final String getId() {
		return this.id;
	}
	
	public abstract boolean canEnchant(@NotNull ItemStack item);
	
	@Override
	public final boolean canEnchantItem(@Nullable ItemStack item) {
		if (item == null) return false;
		if (item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK) {
			return true;
		}
		return this.canEnchant(item);
	}
	
	protected final void loadMapValues(@NotNull TreeMap<Integer, Double> map, @NotNull String path2) {
		// Load different values for each enchantment level.
		Set<String> lvlKeys = cfg.getSection(path2);
		if (!lvlKeys.isEmpty()) {
			for (String sLvl : lvlKeys) {
				int eLvl = StringUT.getInteger(sLvl, 0);
				if (eLvl < this.getStartLevel() || eLvl > this.getMaxLevel()) continue;
				
				String formula = cfg.getString(path2 + "." + sLvl, "0").replace("%level%", sLvl);
				map.put(eLvl, Evaluator.eval(formula, 1));
			}
			return;
		}
		
		// Load the single formula for all enchantment levels.
		for (int lvl = this.getStartLevel(); lvl < (this.getMaxLevel() + 1); lvl++) {
			String sLvl = String.valueOf(lvl);
			String exChance = cfg.getString(path2, "").replace("%level%", sLvl);
			if (exChance.isEmpty()) continue;
			
			map.put(lvl, Evaluator.eval(exChance, 1));
		}
		//System.out.println(getId() + " total map: " + map);
		//System.out.println("------------------------");
	}
	
	protected final boolean isSword(@NotNull ItemStack item) {
		Material mat = item.getType();
		
		if (Config.GEN_ENCHANTS_SWORDS_TO_AXES) {
			return ITEM_SWORDS.contains(mat) || ITEM_AXES.contains(mat);
		}
		return ITEM_SWORDS.contains(mat);
	}
	
	protected final boolean isBow(@NotNull ItemStack item) {
		Material mat = item.getType();
		
		if (Config.GEN_ENCHANTS_BOWS_TO_CROSSBOWS) {
			return item.getType() == Material.BOW || item.getType() == Material.CROSSBOW;
		}
		return mat == Material.BOW;
	}
	
	protected final boolean isPickaxe(@NotNull ItemStack item) {
		return ITEM_PICKAXES.contains(item.getType());
	}
	
	protected final boolean isArmor(@NotNull ItemStack item) {
		Material mat = item.getType();
		
		return ITEM_HELMETS.contains(mat) || ITEM_CHESTPLATES.contains(mat)
				|| ITEM_LEGGINGS.contains(mat) || ITEM_BOOTS.contains(mat);
	}
	
	@Override
	public final int getStartLevel() {
		return this.minLvl;
	}
	
	@Override
	public final int getMaxLevel() {
		return this.maxLvl;
	}
	
	@Override
	@NotNull
	public final String getName() {
		return this.getKey().getKey().toUpperCase();
	}
	
	@NotNull
	public final String getDisplay() {
		return this.display;
	}
	
	@NotNull
	public final String getFormatted(int lvl) {
		return this.getTier().getColor() + this.getDisplay() + " " + NumberUT.toRoman(lvl);
	}
	
	@NotNull
	public final EnchantTier getTier() {
		return this.tier;
	}
	
	public final int getTableMinPlayerLevel() {
		return this.tablePlayerLvl;
	}
	
	public final double getEnchantmentChance() {
		return this.tableEnchantChance;
	}
	
	public final void addPotionEffect(
			@NotNull LivingEntity target, @NotNull PotionEffect effect, boolean compat) {
		
		if (compat) {
			PotionEffect has = target.getPotionEffect(effect.getType());
			if (has != null && has.getAmplifier() > effect.getAmplifier()) {
				return;
			}
		}
		else {
			target.removePotionEffect(effect.getType());
		}
		target.addPotionEffect(effect);
	}
	
	protected final double getMapValue(@NotNull TreeMap<Integer, Double> map, int lvl, double def) {
		Map.Entry<Integer, Double> e = map.floorEntry(lvl);
		return e != null ? e.getValue() : def;
	}
}
