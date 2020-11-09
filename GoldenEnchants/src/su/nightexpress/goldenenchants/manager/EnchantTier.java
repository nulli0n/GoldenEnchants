package su.nightexpress.goldenenchants.manager;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.utils.StringUT;

public class EnchantTier {

	private String id;
	private String name;
	private String color;
	private double chance;
	
	public EnchantTier(
			@NotNull String id,
			@NotNull String name,
			@NotNull String color,
			double chance
			) {
		this.id = id.toLowerCase();
		this.name = StringUT.color(name);
		this.color = StringUT.color(color);
		this.chance = chance;
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
	
	public double getChance() {
		return this.chance;
	}
}
