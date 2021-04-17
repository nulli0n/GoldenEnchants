package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public interface DeathEnchant {

	void use(@NotNull EntityDeathEvent e, @NotNull LivingEntity dead, int lvl);
}
