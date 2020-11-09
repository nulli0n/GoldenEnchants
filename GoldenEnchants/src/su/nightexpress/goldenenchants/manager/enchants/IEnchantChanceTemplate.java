package su.nightexpress.goldenenchants.manager.enchants;

import java.util.Map;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;

public abstract class IEnchantChanceTemplate extends GoldenEnchant {

	protected TreeMap<Integer, Double> effectChance;
	
	public IEnchantChanceTemplate(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		
		this.effectChance = new TreeMap<>();
		this.loadMapValues(this.effectChance, "settings.enchant-trigger-chance");
	}
	
	public final double getTriggerChance(int lvl) {
		Map.Entry<Integer, Double> e = this.effectChance.floorEntry(lvl);
		return e != null ? e.getValue() : 100D;
	}
	
	public final boolean checkTriggerChance(int lvl) {
		return Rnd.get(true) <= this.getTriggerChance(lvl);
	}
}
