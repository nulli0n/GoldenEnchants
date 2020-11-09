package su.nightexpress.goldenenchants.config;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.ILangTemplate;
import su.nightexpress.goldenenchants.GoldenEnchants;

public class Lang extends ILangTemplate {

	public Lang(@NotNull GoldenEnchants plugin) {
		super(plugin);
	}
	
	@Override
	protected void setupEnums() {
		
	}
	
	public JLangMsg Command_Enchant_Usage = new JLangMsg("<enchant> <level>");
	public JLangMsg Command_Enchant_Desc = new JLangMsg("Enchants the item in your hand.");
	public JLangMsg Command_Enchant_Done = new JLangMsg("&aSuccessfully enchanted!");
	
	public JLangMsg Command_List_Desc = new JLangMsg("List of Golden Enchants");
	public JLangMsg Command_List_Format_List = new JLangMsg(
			"\n"
			+ "&8&m-------- &e List of Golden Enchants &8&m--------"
			+ "\n"
			+ "%enchant% %button_book% %button_enchant%"
			+ "\n"
			+ "&8&m-------- &e Page &7%page% &e of &7 %pages% &8&m--------"
			+ "\n");
	public JLangMsg Command_List_Enchant_Hint = new JLangMsg(
			""
			+ "&7Tier: &f%tier%"
			+ "\n"
			+ "&7Chance: &f%chance%"
			+ "\n"
			+ "&7Min Level: &f%min-level%"
			+ "\n"
			+ "&7Max Level: &f%max-level%");
	public JLangMsg Command_List_Button_Book_Name = new JLangMsg("&e&l[Get Book]");
	public JLangMsg Command_List_Button_Book_Hint = new JLangMsg("&7Gives enchant book to your inventory.");
	public JLangMsg Command_List_Button_Enchant_Name = new JLangMsg("&b&l[Enchant Item]");
	public JLangMsg Command_List_Button_Enchant_Hint = new JLangMsg("&7Enchants item in your hand.");
	
	public JLangMsg Command_Book_Usage = new JLangMsg("<player> <enchant> <level>");
	public JLangMsg Command_Book_Desc = new JLangMsg("Gives custom enchanted book.");
	public JLangMsg Command_Book_Done = new JLangMsg("Given &6%enchant%&7 enchanted book to &6%player%&7.");
	
	public JLangMsg Command_TierBook_Usage = new JLangMsg("<player> <tier> <level>");
	public JLangMsg Command_TierBook_Desc = new JLangMsg("Gives an enchanted book.");
	public JLangMsg Command_TierBook_Error = new JLangMsg("&cInvalid tier!");
	public JLangMsg Command_TierBook_Done = new JLangMsg("Given &6%enchant%&7 enchanted book to &6%player%&7.");
	
	public JLangMsg Error_NoEnchant = new JLangMsg("&cNo such enchant.");

}
