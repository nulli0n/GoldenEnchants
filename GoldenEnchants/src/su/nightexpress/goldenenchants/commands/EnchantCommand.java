package su.nightexpress.goldenenchants.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.commands.api.ISubCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.Perms;

public class EnchantCommand extends ISubCommand<GoldenEnchants> {

	public EnchantCommand(@NotNull GoldenEnchants plugin) {
		super(plugin, new String[] {"enchant"}, Perms.ADMIN);
	}
	
	@Override
	@NotNull
	public String description() {
		return plugin.lang().Command_Enchant_Desc.getMsg();
	}

	@Override
	@NotNull
	public String usage() {
		return plugin.lang().Command_Enchant_Usage.getMsg();
	}
	
	@Override
	public boolean playersOnly() {
		return true;
	}

	@Override
	@NotNull
	public List<String> getTab(@NotNull Player p, int i, @NotNull String[] args) {
		if (i == 1) {
	        List<String> list = new ArrayList<String>();
	        for (Enchantment e : Enchantment.values()) {
	        	list.add(e.getKey().getKey());
	        }
	        return list;
		}
		if (i == 2) {
			return Arrays.asList("-1", "1", "5", "10");
		}
		return super.getTab(p, i, args);
	}
	
	@Override
	public void perform(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if (args.length != 3) {
			this.printUsage(sender);
			return;
		}
		
		Player p = (Player) sender;
		ItemStack item = p.getInventory().getItemInMainHand();
		if (ItemUT.isAir(item)) {
			this.errItem(sender);
			return;
		}
		
		String en = args[1].toLowerCase();
		Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft(en));
		if (e == null) {
			plugin.lang().Error_NoEnchant.send(sender, true);
			return;
		}
		
		int lvl = this.getNumI(sender, args[2], -1, true);
		if (lvl < 1) {
			lvl = Rnd.get(e.getStartLevel(), e.getMaxLevel());
		}
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;
		
		if (meta instanceof EnchantmentStorageMeta) {
	        ((EnchantmentStorageMeta)meta).addStoredEnchant(e, lvl, true);
		}
		else {
			meta.addEnchant(e, lvl, true);
		}
		item.setItemMeta(meta);
		plugin.getEnchantManager().updateItemLoreEnchants(item);
		
		plugin.lang().Command_Enchant_Done.send(sender, true);
	}
}
