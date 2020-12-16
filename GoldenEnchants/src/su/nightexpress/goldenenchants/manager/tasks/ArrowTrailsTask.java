package su.nightexpress.goldenenchants.manager.tasks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.engine.utils.EffectUT;
import su.nightexpress.goldenenchants.GoldenEnchants;
import su.nightexpress.goldenenchants.config.Config;

public class ArrowTrailsTask extends ITask<GoldenEnchants> {
	
	private static final Map<Projectile, Set<String>> TRAILS_MAP = new HashMap<>();
	
	public ArrowTrailsTask(@NotNull GoldenEnchants plugin) {
		super(plugin, Config.GEN_TASK_ARROW_TRAIL_TICK_TIME, false);
		TRAILS_MAP.clear();
	}
	
    @Override
	public void action() {
    	TRAILS_MAP.keySet().removeIf(pj -> !pj.isValid() || pj.isDead());
    	
    	TRAILS_MAP.forEach((arrow, effects) -> {
    		effects.forEach(effect -> {
    			EffectUT.playEffect(arrow.getLocation(), effect, 0f, 0f, 0f, 0f, 10);
    		});
    	});
    }
    
    public static void add(@NotNull Projectile pj, @NotNull String effect) {
    	TRAILS_MAP.computeIfAbsent(pj, list -> new HashSet<>()).add(effect);
    }
}