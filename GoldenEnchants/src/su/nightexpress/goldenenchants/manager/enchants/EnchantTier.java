package su.nightexpress.goldenenchants.manager.enchants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.manager.enchants.api.type.ObtainType;

public class EnchantTier {

	private String id;
	private String name;
	private String color;
	private Map<ObtainType, Double> chance;
	
	private Set<GoldenEnchant> enchants;
	
	public EnchantTier(
			@NotNull String id,
			@NotNull String name,
			@NotNull String color,
			@NotNull Map<ObtainType, Double> chance
			) {
		this.id = id.toLowerCase();
		this.name = StringUT.color(name);
		this.color = StringUT.color(color);
		this.chance = chance;
		this.enchants = new HashSet<>();
	}
	
	@NotNull
	public String getId() {
		return this.id;
	}
	
	@NotNull
	public String getName() {
		return this.name;
	}
	
	@NotNull
	public String getColor() {
		return this.color;
	}
	
	@NotNull
	public Map<ObtainType, Double> getChance() {
		return this.chance;
	}
	
	public double getChance(@NotNull ObtainType obtainType) {
		return this.getChance().getOrDefault(obtainType, 0D);
	}
	
	@NotNull
	public Set<GoldenEnchant> getEnchants() {
		return this.enchants;
	}
	
	@Nullable
	public Set<GoldenEnchant> getEnchants(@NotNull ObtainType obtainType) {
		Set<GoldenEnchant> set = this.getEnchants().stream()
				.filter(en -> en.getObtainChance(obtainType) > 0)
				.collect(Collectors.toSet());
		return set;
	}
	
	@Nullable
	public GoldenEnchant getEnchant(@NotNull ObtainType obtainType) {
		Map<GoldenEnchant, Double> map = new HashMap<>();
		
		this.getEnchants().stream().filter(en -> {
			return en.getObtainChance(obtainType) > 0;
		}).forEach(en -> map.put(en, en.getObtainChance(obtainType)));
		
		return map.isEmpty() ? null : Rnd.get(map);
	}
	
	@Nullable
	public GoldenEnchant getEnchant(int expLevel) {
		Map<GoldenEnchant, Double> map = new HashMap<>();
		
		this.getEnchants().stream().filter(en -> {
			return en.getObtainChance(ObtainType.ENCHANTING) > 0 && 
					(expLevel < 0 || en.getTableMinPlayerLevel() <= expLevel);
		}).forEach(en -> map.put(en, en.getObtainChance(ObtainType.ENCHANTING)));
		
		return map.isEmpty() ? null : Rnd.get(map);
	}
}
