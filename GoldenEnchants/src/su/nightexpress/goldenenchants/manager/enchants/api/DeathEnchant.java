package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public interface DeathEnchant {

	void use(@NotNull LivingEntity dead, @NotNull EntityDeathEvent e, int lvl);
}
