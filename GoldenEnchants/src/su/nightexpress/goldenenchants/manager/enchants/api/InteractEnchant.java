package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface InteractEnchant {

	void use(@NotNull PlayerInteractEvent e, @NotNull Player player, @NotNull ItemStack item, int lvl);
}
