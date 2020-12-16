package su.nightexpress.goldenenchants.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import su.nexmedia.engine.utils.ItemUT;
import su.nightexpress.goldenenchants.manager.enchants.api.type.ObtainType;

public class EnchantPopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		List<Inventory> inventories = new ArrayList<>();
		
		// Chests support.
		for (BlockState state : chunk.getTileEntities()) {
			if (!(state instanceof Chest)) continue;
			if (state instanceof EnderChest) continue;
			
			Chest chest = (Chest) state;
			inventories.add(chest.getBlockInventory());
		}
		
		// Minecarts support.
		for (Entity entity : chunk.getEntities()) {
			if (entity instanceof StorageMinecart) {
				StorageMinecart minecart = (StorageMinecart) entity;
				inventories.add(minecart.getInventory());
			}
		}
		
		inventories.forEach(inventory -> {
			for (ItemStack item : inventory.getContents()) {
				if (ItemUT.isAir(item)) continue;
				if (!EnchantManager.isEnchantable(item)) continue;
				
				EnchantManager.populateEnchantments(item, ObtainType.LOOT_GENERATION);
			}
		});
	}

}
