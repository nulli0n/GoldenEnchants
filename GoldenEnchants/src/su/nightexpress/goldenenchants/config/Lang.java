package su.nightexpress.goldenenchants.config;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.core.config.CoreLang;
import su.nightexpress.goldenenchants.GoldenEnchants;

public class Lang extends CoreLang {

	public Lang(@NotNull GoldenEnchants plugin) {
		super(plugin);
	}
	
	@Override
	protected void setupEnums() {
		
	}
	
	public ILangMsg Command_Enchant_Usage = new ILangMsg(this, "<enchant> <level>");
	public ILangMsg Command_Enchant_Desc = new ILangMsg(this, "Enchants the item in your hand.");
	public ILangMsg Command_Enchant_Done = new ILangMsg(this, "&aSuccessfully enchanted!");
	
	public ILangMsg Command_Book_Usage = new ILangMsg(this, "<player> <enchant> <level>");
	public ILangMsg Command_Book_Desc = new ILangMsg(this, "Gives custom enchanted book.");
	public ILangMsg Command_Book_Done = new ILangMsg(this, "Given &6%enchant%&7 enchanted book to &6%player%&7.");
	
	public ILangMsg Command_TierBook_Usage = new ILangMsg(this, "<player> <tier> <level>");
	public ILangMsg Command_TierBook_Desc = new ILangMsg(this, "Gives an enchanted book.");
	public ILangMsg Command_TierBook_Error = new ILangMsg(this, "&cInvalid tier!");
	public ILangMsg Command_TierBook_Done = new ILangMsg(this, "Given &6%enchant%&7 enchanted book to &6%player%&7.");
	
	public ILangMsg Error_NoEnchant = new ILangMsg(this, "&cNo such enchant.");

}
