package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface PassiveEnchant {
	
	void use(@NotNull LivingEntity user, int lvl);
}
