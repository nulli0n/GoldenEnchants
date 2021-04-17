package su.nightexpress.goldenenchants.manager.enchants.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LocationEnchant {

	void use(@Nullable Entity entity, @NotNull Location loc, @NotNull ItemStack wpn, int lvl);
}
