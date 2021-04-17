package su.nightexpress.goldenenchants.manager.enchants.tool;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.DataUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.manager.enchants.IEnchantChanceTemplate;
import su.nightexpress.goldenenchants.manager.enchants.api.BlockEnchant;

public class EnchantSilkChest extends IEnchantChanceTemplate implements BlockEnchant {

	private Map<Integer, NamespacedKey> keyItems;
	private String chestName;
	
	public EnchantSilkChest(@NotNull GoldenEnchants plugin, @NotNull JYML cfg) {
		super(plugin, cfg);
		this.keyItems = new TreeMap<>();
		this.chestName = StringUT.color(cfg.getString("settings.chest-name", "%name% &7(%items% items)"));
		
		for (int pos = 0; pos < 27; pos++) {
			this.getItemKey(pos);
		}
	}
	
	private NamespacedKey getItemKey(int pos) {
		return this.keyItems.computeIfAbsent(pos, key -> new NamespacedKey(plugin, "silkchest_item_" + pos));
	}

	@Override
	public void use(@NotNull BlockBreakEvent e, @NotNull Player player, @NotNull ItemStack item, int lvl) {
		Block block = e.getBlock();
		BlockState state = block.getState();
		if (!(state instanceof Chest)) return;
		if (block.getType() == Material.ENDER_CHEST) return;
		
		Chest chest = (Chest) state;
		ItemStack chestItem = new ItemStack(block.getType());
		
		// Store and count chest items.
		int amount = 0;
		int count = 0;
		for (ItemStack itemInv : chest.getInventory().getContents()) {
			if (itemInv == null) itemInv = new ItemStack(Material.AIR);
			else amount++;
			
			String base64 = ItemUT.toBase64(itemInv);
			if (base64 == null) continue;
			if (base64.length() >= Short.MAX_VALUE) {
				block.getWorld().dropItemNaturally(block.getLocation(), itemInv);
				continue;
			}
			
			DataUT.setData(chestItem, this.getItemKey(count++), base64);
		}
		// Do not drop chest items.
		chest.getInventory().clear();
		
		// Apply item meta name and items data string.
		
		ItemMeta meta = chestItem.getItemMeta();
		if (meta != null) {
			String nameOrig = ItemUT.getItemName(chestItem);
			String nameChest = this.chestName.replace("%name%", nameOrig).replace("%items%", String.valueOf(amount));
			meta.setDisplayName(nameChest);
			chestItem.setItemMeta(meta);
		}
		
		// Drop custom chest and do not drop the original one.
		block.getWorld().dropItemNaturally(block.getLocation(), chestItem);
		e.setDropItems(false);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSilkChestPlace(BlockPlaceEvent e) {
		ItemStack item = e.getItemInHand();
		if (ItemUT.isAir(item)) return;
		
		Block block = e.getBlockPlaced();
		BlockState state = block.getState();
		if (!(state instanceof Chest)) return;
		
		Chest chest = (Chest) state;
		Inventory inventory = chest.getInventory();
		
		for (int pos = 0; pos < inventory.getSize(); pos++) {
			String data = DataUT.getStringData(item, this.getItemKey(pos));
			if (data == null) continue;
			
			ItemStack itemInv = ItemUT.fromBase64(data);
			inventory.setItem(pos, itemInv);
		}
	}
	
	@Override
	public boolean canEnchant(@NotNull ItemStack item) {
		return ITEM_AXES.contains(item.getType());
	}

	@Override
	public boolean conflictsWith(@Nullable Enchantment en) {
		return false;
	}

	@Override
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TOOL;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}
}
