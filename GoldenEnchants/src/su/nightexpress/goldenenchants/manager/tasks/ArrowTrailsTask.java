package su.nightexpress.goldenenchants.manager.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.engine.utils.EffectUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.config.Config;

public class ArrowTrailsTask extends ITask<GoldenEnchants> {
	
	private static final Map<Projectile, List<String>> TRAILS_MAP = new HashMap<>();
	
	public ArrowTrailsTask(@NotNull GoldenEnchants plugin) {
		super(plugin, Config.GEN_TASK_ARROW_TRAIL_TICK_TIME, false);
		TRAILS_MAP.clear();
	}
	
    @Override
	public void action() {
    	for (Map.Entry<Projectile, List<String>> e : new HashMap<>(TRAILS_MAP).entrySet()) {
    		Projectile pj = e.getKey();
        	if (pj.isOnGround() || !pj.isValid()) {
        		TRAILS_MAP.remove(pj);
        		continue;
        	}
        	for (String effect : e.getValue()) {
        		EffectUT.playEffect(pj.getLocation(), effect, 0f, 0f, 0f, 0f, 10);
        	}
        }
    }
    
    public static void add(@NotNull Projectile pj, @NotNull String effect) {
    	List<String> list = TRAILS_MAP.get(pj);
    	if (list == null) list = new ArrayList<>();
    	
    	list.add(effect);
    	TRAILS_MAP.put(pj, list);
    }
}