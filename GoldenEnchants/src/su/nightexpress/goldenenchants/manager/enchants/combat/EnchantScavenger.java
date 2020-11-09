package su.nightexpress.goldenenchants.manager.enchants.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.DeathEnchant;

public class EnchantScavenger extends IEnchantChanceTemplate implements DeathEnchant {

	private Map<EntityType, Map<Material, Map.Entry<int[], Double>>> loot;
	
	public EnchantScavenger(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.loot = new HashMap<>();
		
		
		for (String eId : cfg.getSection("settings.treasures")) {
			EntityType eType = CollectionsUT.getEnum(eId, EntityType.class);
			if (eType == null || !eType.isAlive()) {
				plugin.error("[Scavenger] Invalid entity type '" + eId + "' !");
				continue;
			}
			
			Map<Material, Map.Entry<int[], Double>> items = new HashMap<>();
			for (String itemId : cfg.getSection("settings.treasures." + eId)) {
				Material material = Material.getMaterial(itemId.toUpperCase());
				if (material == null) {
					plugin.error("[Scavenger] Invalid item material '" + itemId + "' !");
					continue;
				}
				
				String path = "settings.treasures." + eId + "." + itemId + ".";
				String[] amountSplit = cfg.getString(path + "amount", "1:1").split(":");
				int amountMin = StringUT.getInteger(amountSplit[0], 1);
				int amountMax = StringUT.getInteger(amountSplit[1], 1);
				int[] amount = new int[] {amountMin, amountMax};
				
				double chance = cfg.getDouble(path + "chance");
				if (chance <= 0) continue;
				
				Map.Entry<int[], Double> item = new AbstractMap.SimpleEntry<>(amount, chance);
				items.put(material, item);
			}
			this.loot.put(eType, items);
		}
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return this.isSword(item);
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public void use(@NotNull LivingEntity dead, @NotNull EntityDeathEvent e, int lvl) {
		Map<Material, Map.Entry<int[], Double>> items = this.loot.get(dead.getType());
		if (items == null) return;
		
		if (!this.checkTriggerChance(lvl)) return;
		
		items.forEach((material, data) -> {
			double chance = data.getValue();
			if (Rnd.get(true) > chance) return;
			
			int amount = Rnd.get(data.getKey()[0], data.getKey()[1]);
			if (amount <= 0) return;
			
			ItemStack item = new ItemStack(material);
			e.getDrops().add(item);
		});
	}
}
