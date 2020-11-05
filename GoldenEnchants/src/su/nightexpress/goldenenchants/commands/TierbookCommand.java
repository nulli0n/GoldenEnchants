package su.nightexpress.goldenenchants.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import su.fogus.engine.commands.api.ISubCommand;
import su.fogus.engine.utils.ItemUT;
import su.fogus.engine.utils.PlayerUT;
import su.fogus.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.Perms;
import su.nightexpress.goldenenchants.manager.EnchantTier;

public class TierbookCommand extends ISubCommand<GoldenEnchants> {

	public TierbookCommand(@NotNull GoldenEnchants plugin) {
		super(plugin, Perms.ADMIN);
	}
	
	@Override
	@NotNull
	public String description() {
		return plugin.lang().Command_TierBook_Desc.getMsg();
	}

	@Override
	@NotNull
	public String @NotNull [] labels() {
		return new String[] {"tierbook"};
	}

	@Override
	@NotNull
	public String usage() {
		return plugin.lang().Command_TierBook_Usage.getMsg();
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
			return plugin.getEnchantManager().getTierIds();
		}
		if (i == 3) {
			return Arrays.asList("-1", "1", "5", "10");
		}
		return super.getTab(p, i, args);
	}
	
	@Override
	public void perform(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
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
		
		String en = args[2].toLowerCase();
		EnchantTier tier = plugin.getEnchantManager().getTierById(en);
		if (tier == null) {
			plugin.lang().Command_TierBook_Error.send(sender, true);
			return;
		}
		
		Enchantment e = plugin.getEnchantManager().getEnchantByTier(tier);
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
		
		plugin.lang().Command_TierBook_Done
				.replace("%enchant%", tier.getName())
				.replace("%player%", p.getName())
				.send(sender, true);
	}
}
