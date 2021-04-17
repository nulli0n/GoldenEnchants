package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public interface MoveEnchant {

	void use(@NotNull PlayerMoveEvent e, @NotNull LivingEntity entity, int lvl);
}
