package su.nightexpress.goldenenchants.manager.tasks;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.engine.utils.EntityUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.config.Config;
import su.nightexpress.goldenenchants.manager.enchants.api.PassiveEnchant;

public class PassiveEnchantsTask extends ITask<GoldenEnchants> {
	
	public PassiveEnchantsTask(@NotNull GoldenEnchants plugin) {
		super(plugin, Config.GEN_TASK_PASSIVE_ENCHANT_TICK_TIME, false);
	}
	
    public void action() {
    	for (LivingEntity entity : this.getEntities()) {
    		for (ItemStack armor : EntityUT.getEquipment(entity)) {
    			if (armor == null) continue;
    			
    			ItemMeta meta = armor.getItemMeta();
    			if (meta == null) continue;
    			
    			meta.getEnchants().forEach((en, lvl) -> {
    				if (lvl < 1) return;
    				if (!(en instanceof PassiveEnchant)) return;
    				
    				PassiveEnchant passiveEnchant = (PassiveEnchant) en;
    				passiveEnchant.use(entity, lvl);
    			});
    		}
        }
    }
    
    @NotNull
    private Collection<@NotNull ? extends LivingEntity> getEntities() {
    	Set<LivingEntity> list = new HashSet<>(plugin.getServer().getOnlinePlayers());
    	
    	if (Config.GEN_ENCHANTS_PASSIVE_FOR_MOBS) {
    		plugin.getServer().getWorlds().forEach(world -> {
    			list.addAll(world.getEntitiesByClass(LivingEntity.class));
    		});
    	}
    	return list;
    }
}
