package su.nightexpress.goldenenchants;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.commands.api.IGeneralCommand;
import su.nexmedia.engine.core.Version;
import su.nexmedia.engine.utils.Reflex;
import su.nightexpress.goldenenchants.commands.BookCommand;
import su.nightexpress.goldenenchants.commands.EnchantCommand;
import su.nightexpress.goldenenchants.commands.ListCommand;
import su.nightexpress.goldenenchants.commands.TierbookCommand;
import su.nightexpress.goldenenchants.config.Config;
import su.nightexpress.goldenenchants.config.Lang;
import su.nightexpress.goldenenchants.manager.EnchantManager;
import su.nightexpress.goldenenchants.nms.EnchantNMS;

public class GoldenEnchants extends NexPlugin<GoldenEnchants> {
	
	private static GoldenEnchants inst;
	
	private Config config;
	private Lang lang;
	
	private EnchantNMS nmsHandler;
	private EnchantManager enchantManager;
	
    public static GoldenEnchants getInstance() {
    	return inst;
    }
    
	public GoldenEnchants() {
	    inst = this;
	}
	
	@Override
	public void enable() {
		if (!this.setNMS()) {
			this.error("Could not setup internal NMS handler!");
			this.getPluginManager().disablePlugin(this);
			return;
		}
		
		this.enchantManager = new EnchantManager(this);
		this.enchantManager.setup();
	}

	@Override
	public void disable() {
		if (this.enchantManager != null) {
			this.enchantManager.shutdown();
			this.enchantManager = null;
		}
		this.nmsHandler = null;
	}
	
	private boolean setNMS() {
    	Version current = Version.CURRENT;
    	if (current == null) return false;
    	
    	String pack = EnchantNMS.class.getPackage().getName();
    	Class<?> clazz = Reflex.getClass(pack, current.name());
    	if (clazz == null) return false;
    	
    	try {
			this.nmsHandler = (EnchantNMS) clazz.getConstructor().newInstance();
		} 
    	catch (Exception e) {
			e.printStackTrace();
		}
		return this.nmsHandler != null;
	}

	@Override
	public void setConfig() {
		this.config = new Config(this);
		this.config.setup();
		
		this.lang = new Lang(this);
		this.lang.setup();
	}
	
	@Override
	public void registerCmds(@NotNull IGeneralCommand<GoldenEnchants> mainCommand) {
		mainCommand.addSubCommand(new BookCommand(this));
		mainCommand.addSubCommand(new EnchantCommand(this));
		mainCommand.addSubCommand(new ListCommand(this));
		mainCommand.addSubCommand(new TierbookCommand(this));
	}

	@Override
	public void registerHooks() {
		
	}
	
	@Override
	public void registerEditor() {
		
	}

	@Override
	@NotNull
	public Config cfg() {
		return this.config;
	}

	@Override
	@NotNull
	public Lang lang() {
		return this.lang;
	}

	@NotNull
	public EnchantManager getEnchantManager() {
		return this.enchantManager;
	}
	
	@NotNull
	public EnchantNMS getNMSHandler() {
		return nmsHandler;
	}
}
