package su.nightexpress.goldenenchants.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.commands.api.ISubCommand;
import su.nexmedia.engine.utils.ClickText;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.Perms;
import su.nightexpress.goldenenchants.manager.EnchantRegister;
import su.nightexpress.goldenenchants.manager.enchants.GoldenEnchant;

public class ListCommand extends ISubCommand<GoldenEnchants> {

	public ListCommand(@NotNull GoldenEnchants plugin) {
		super(plugin, new String[] {"list"}, Perms.ADMIN);
	}
	
	@Override
	public boolean playersOnly() {
		return false;
	}

	@Override
	@NotNull
	public String description() {
		return plugin.lang().Command_List_Desc.getMsg();
	}

	@Override
	@NotNull
	public String usage() {
		return "";
	}
	
	@Override
	@NotNull
	public List<String> getTab(@NotNull Player p, int i, @NotNull String[] args) {
		if (i == 1) {
			return Arrays.asList("[page]");
		}
		return super.getTab(p, i, args);
	}
	
	@Override
	public void perform(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		int page = 1;
		if (args.length == 2) {
			page = this.getNumI(sender, args[1], 1);
		}
		List<GoldenEnchant> list = new ArrayList<>(EnchantRegister.ENCHANT_LIST);
    	int pages = CollectionsUT.split(list, 10).size();
    	if (page > pages) page = pages;
    	if (pages < 1) list = new ArrayList<>();
    	else list = CollectionsUT.split(list, 10).get(page - 1);
		
    	for (String s : plugin.lang().Command_List_Format_List.asList()) {
    		if (s.contains("%enchant%")) {
    			for (GoldenEnchant ge : list) {
    				ClickText ct = new ClickText(s);
    				
    				ct.createPlaceholder("%enchant%", ge.getTier().getColor() + ge.getDisplay())
    						.hint(plugin.lang().Command_List_Enchant_Hint
    								.replace("%max-level%", String.valueOf(ge.getMaxLevel()))
    								.replace("%min-level%", String.valueOf(ge.getStartLevel()))
    								.replace("%chance%", String.valueOf(ge.getEnchantmentChance()))
    								.replace("%tier%", ge.getTier().getName())
    								.getMsg());
    				
    				ct.createPlaceholder("%button_book%", plugin.lang().Command_List_Button_Book_Name.getMsg())
    				.hint(plugin.lang().Command_List_Button_Book_Hint.getMsg())
    				.execCmd("/" + plugin.getLabel() + " book" + " " + sender.getName() + " " + ge.getKey().getKey() + " " + ge.getMaxLevel());
    				
    				ct.createPlaceholder("%button_enchant%", plugin.lang().Command_List_Button_Enchant_Name.getMsg())
    				.hint(plugin.lang().Command_List_Button_Enchant_Hint.getMsg())
    				.execCmd("/" + plugin.getLabel() + " enchant" + " " + ge.getKey().getKey() + " " + ge.getMaxLevel());
    				
    				ct.send(sender);
    			}
    			continue;
    		}
    		sender.sendMessage(s
    				.replace("%pages%", String.valueOf(pages))
    				.replace("%page%", String.valueOf(page)));
    	}
	}
}
