package su.nightexpress.goldenenchants.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.manager.IListener;
import su.nexmedia.engine.manager.IManager;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.config.Config;
import su.nightexpress.goldenenchants.manager.enchants.EnchantTier;
import su.nightexpress.goldenenchants.manager.enchants.GoldenEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.type.ObtainType;
import su.nightexpress.goldenenchants.manager.listeners.EnchantGenericListener;
import su.nightexpress.goldenenchants.manager.listeners.EnchantHandlerListener;
import su.nightexpress.goldenenchants.manager.tasks.ArrowTrailsTask;
import su.nightexpress.goldenenchants.manager.tasks.PassiveEnchantsTask;

public class EnchantManager extends IManager<GoldenEnchants> {
	
	private Set<IListener<GoldenEnchants>> listeners;
	public EnchantPopulator populator;
	
	private ArrowTrailsTask arrowTrailsTask;
	private PassiveEnchantsTask passiveEnchantsTask;
	
	private static final String META_ENCHANT_ARROW = "GOLDEN_ENCHANTS_ARROW_META_WEAPON";
	
	public EnchantManager(@NotNull GoldenEnchants plugin) {
		super(plugin);
	}
	
	@Override
	public void setup() {
		EnchantRegister.setup();
		
		this.listeners = new HashSet<>();
		this.listeners.add(new EnchantHandlerListener(this));
		this.listeners.add(new EnchantGenericListener(this));
		this.listeners.forEach(listener -> listener.registerListeners());
		
		if (Config.LOOTGEN_ENABLED) {
			this.populator = new EnchantPopulator();
			this.plugin.getServer().getWorlds().forEach(world -> world.getPopulators().add(populator));
		}
		
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
		if (this.listeners != null) {
			this.listeners.forEach(listener -> listener.unregisterListeners());
			this.listeners.clear();
			this.listeners = null;
		}
		if (this.populator != null) {
			this.plugin.getServer().getWorlds().forEach(world -> world.getPopulators().remove(this.populator));
			this.populator = null;
		}
		
		EnchantRegister.shutdown();
	}
	
	public static boolean isEnchantable(@NotNull ItemStack item) {
		return item.getType() == Material.ENCHANTED_BOOK 
				|| ItemUT.isWeapon(item) || ItemUT.isArmor(item);
	}
	
	public static void populateEnchantments(@NotNull ItemStack item, @NotNull ObtainType obtainType) {
		int enchHas = EnchantManager.getItemEnchantsAmount(item);
		int enchMax = Config.getPopulationEnchantsTotalMax(obtainType);
		int enchRoll = Rnd.get(Config.getPopulationEnchantsGoldenMin(obtainType), Config.getPopulationEnchantsGoldenMax(obtainType));
		
		for (int count = 0; (count < enchRoll && count + enchHas < enchMax); count++) {
			EnchantTier tier = EnchantManager.getTierByChance(obtainType);
			if (tier == null) continue;
			
			GoldenEnchant enchant = tier.getEnchant(obtainType);
			if (enchant == null) continue;
			
			int lvl = Rnd.get(enchant.getStartLevel(), enchant.getMaxLevel());
			if (!EnchantManager.canEnchant(item, enchant, lvl)) continue;
			
			EnchantManager.addEnchant(item, enchant, lvl, false);
		}
		EnchantManager.updateItemLoreEnchants(item);
	}

	public static boolean hasEnchant(@NotNull ItemStack item, @NotNull Enchantment en) {
		return EnchantManager.getEnchantLevel(item, en) != 0;
	}
	
	public static int getEnchantLevel(@NotNull ItemStack item, @NotNull Enchantment en) {
		ItemMeta meta = item.getItemMeta();
		return meta != null ? meta.getEnchantLevel(en) : 0;
	}
	
	public static void updateItemLoreEnchants(@NotNull ItemStack item) {
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
		
		enchants.forEach((en, level) -> {
			if (!(en instanceof GoldenEnchant)) return;
			
			GoldenEnchant ge = (GoldenEnchant) en;
			ItemUT.addLore(item, ge.getId(), ge.getFormatted(level), 0);
		});
	}
	
	public static boolean canEnchant(@NotNull ItemStack item, @NotNull GoldenEnchant en, int lvl) {
		if (lvl < 1) return false;
		if (!en.canEnchantItem(item)) return false;
		if (getItemGoldenEnchantsAmount(item) >= Config.GEN_ENCHANTS_MAX_FOR_ITEM) return false;
		
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
	
	public static boolean addEnchant(@NotNull ItemStack item, @NotNull GoldenEnchant en, int lvl, boolean force) {
		if (!force && !canEnchant(item, en, lvl)) return false;
		
		EnchantManager.removeEnchant(item, en);
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
	
	public static boolean removeEnchant(@NotNull ItemStack item, @NotNull GoldenEnchant en) {
		ItemUT.delLore(item, en.getId());
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return false;
		
		if (meta instanceof EnchantmentStorageMeta) {
			((EnchantmentStorageMeta) meta).removeStoredEnchant(en);
		}
		else {
			meta.removeEnchant(en);
		}
		item.setItemMeta(meta);
		
		return true;
	}
	
	@NotNull
	public static Map<GoldenEnchant, Integer> getItemGoldenEnchants(@NotNull ItemStack item) {
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
			if (en instanceof GoldenEnchant && lvl > 0) {
				map.put((GoldenEnchant) en, lvl);
			}
		});
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	public static <T> Map<T, Integer> getItemGoldenEnchants(@NotNull ItemStack item, @NotNull Class<T> clazz) {
		Map<T, Integer> map = new HashMap<>();
		
		EnchantManager.getItemGoldenEnchants(item).forEach((en, level) -> {
			if (!clazz.isAssignableFrom(en.getClass()) || level < 1) return;
			map.put((T) en, level);
		});
		
		return map;
	}
	
	public static int getItemGoldenEnchantsAmount(@NotNull ItemStack item) {
		return getItemGoldenEnchants(item).size();
	}
	
	@NotNull
	public static Map<Enchantment, Integer> getItemEnchants(@NotNull ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return Collections.emptyMap();
		
		if (meta instanceof EnchantmentStorageMeta) {
			return ((EnchantmentStorageMeta) meta).getStoredEnchants();
		}
		else {
			return meta.getEnchants();
		}
	}
	
	public static int getItemEnchantsAmount(@NotNull ItemStack item) {
		return getItemEnchants(item).size();
	}
	
	@Nullable
	public static EnchantTier getTierById(@NotNull String id) {
		return Config.getTierById(id);
	}
	
	@NotNull
	public static Collection<EnchantTier> getTiers() {
		return Config.getTiers();
	}
	
	@NotNull
	public static List<String> getTierIds() {
		return Config.getTierIds();
	}
	
	@Nullable
	public static EnchantTier getTierByChance(@NotNull ObtainType obtainType) {
		return Config.getTierByChance(obtainType);
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
