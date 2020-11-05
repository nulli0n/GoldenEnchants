package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface BlockEnchant {

	void use(@NotNull ItemStack tool, @NotNull Player p, @NotNull BlockBreakEvent e, int lvl);
}
