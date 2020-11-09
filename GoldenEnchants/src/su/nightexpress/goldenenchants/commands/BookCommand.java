package su.nightexpress.goldenenchants.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.commands.api.ISubCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.Perms;

public class BookCommand extends ISubCommand<GoldenEnchants> {
	
	public BookCommand(@NotNull GoldenEnchants plugin) {
		super(plugin, new String[] {"book"}, Perms.ADMIN);
	}
	
	@Override
	@NotNull
	public String description() {
		return plugin.lang().Command_Book_Desc.getMsg();
	}

	@Override
	@NotNull
	public String usage() {
		return plugin.lang().Command_Book_Usage.getMsg();
	}

	@Override
	public boolean playersOnly() {
		return false;
	}

	@Override
	@NotNull
	public List<String> getTab(@NotNull Player p, int i, @NotNull String[] args) {
		if (i == 1) {
			return PlayerUT.getPlayerNames();
		}
		if (i == 2) {
	        List<String> list = new ArrayList<String>();
	        for (Enchantment e : Enchantment.values()) {
	        	list.add(e.getKey().getKey());
	        }
	        return list;
		}
		if (i == 3) {
			return Arrays.asList("-1", "1", "5", "10");
		}
		return super.getTab(p, i, args);
	}
	
	@Override
	public void perform(CommandSender sender, String label, String[] args) {
		if (args.length != 4) {
			this.printUsage(sender);
			return;
		}
		
		String pName = args[1];
		Player p = plugin.getServer().getPlayer(pName);
		if (p == null) {
			this.errPlayer(sender);
			return;
		}
		
		String enchantName = args[2].toLowerCase();
		Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
		if (e == null) {
			plugin.lang().Error_NoEnchant.send(sender, true);
			return;
		}
		
		int lvl = this.getNumI(sender, args[3], -1, true);
		if (lvl < 1) {
			lvl = Rnd.get(e.getStartLevel(), e.getMaxLevel());
		}
		
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
	    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
	    if (meta == null) return;
	    
	    meta.addStoredEnchant(e, lvl, true);
	    item.setItemMeta(meta);
	        
		plugin.getEnchantManager().updateItemLoreEnchants(item);
		ItemUT.addItem(p, item);
		
		plugin.lang().Command_Book_Done
			.replace("%enchant%", enchantName)
			.replace("%player%", p.getName())
			.send(sender, true);
	}
}
