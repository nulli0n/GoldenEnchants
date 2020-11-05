package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface CombatEnchant {

	void use(@NotNull ItemStack weapon, @NotNull LivingEntity damager, @NotNull LivingEntity victim, @NotNull EntityDamageByEntityEvent e, int lvl);
}
