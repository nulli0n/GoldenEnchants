package su.nightexpress.goldenenchants.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.commands.api.ISubCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.Perms;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.manager.enchants.EnchantTier;
import su.nightexpress.goldenenchants.manager.enchants.GoldenEnchant;
import su.nightexpress.goldenenchants.manager.enchants.api.type.ObtainType;

public class TierbookCommand extends ISubCommand<GoldenEnchants> {

	public TierbookCommand(@NotNull GoldenEnchants plugin) {
		super(plugin, new String[] {"tierbook"}, Perms.ADMIN);
	}
	
	@Override
	@NotNull
	public String description() {
		return plugin.lang().Command_TierBook_Desc.getMsg();
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
	public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
		if (i == 1) {
			return PlayerUT.getPlayerNames();
		}
		if (i == 2) {
			return EnchantManager.getTierIds();
		}
		if (i == 3) {
			return Arrays.asList("-1", "1", "5", "10");
		}
		return super.getTab(player, i, args);
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
		EnchantTier tier = EnchantManager.getTierById(en);
		if (tier == null) {
			plugin.lang().Command_TierBook_Error.send(sender);
			return;
		}
		
		GoldenEnchant ench = tier.getEnchant(ObtainType.ENCHANTING);
		if (ench == null) {
			plugin.lang().Error_NoEnchant.send(sender);
			return;
		}
		
		int lvl = this.getNumI(sender, args[3], -1, true);
		if (lvl < 1) {
			lvl = Rnd.get(ench.getStartLevel(), ench.getMaxLevel());
		}
		
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantManager.addEnchant(item, ench, lvl, true);
		ItemUT.addItem(p, item);
		
		plugin.lang().Command_TierBook_Done
				.replace("%enchant%", tier.getName())
				.replace("%player%", p.getName())
				.send(sender);
	}
}
