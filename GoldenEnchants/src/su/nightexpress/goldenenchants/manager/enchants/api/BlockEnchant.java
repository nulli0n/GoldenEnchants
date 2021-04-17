package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface BlockEnchant {

	void use(@NotNull BlockBreakEvent e, @NotNull Player player, @NotNull ItemStack item, int lvl);
}
