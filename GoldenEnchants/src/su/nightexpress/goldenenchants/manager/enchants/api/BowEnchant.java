package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface BowEnchant {

	void use(@NotNull ItemStack bow, @NotNull LivingEntity shooter, @NotNull EntityShootBowEvent e, int lvl);
}
