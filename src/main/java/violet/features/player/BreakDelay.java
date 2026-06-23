package violet.features.player;

import meteordevelopment.orbit.EventHandler;
import violet.config.Feature;
import violet.events.BlockBreakingCooldownEvent;

public class BreakDelay {
    public static final Feature instance = new Feature("breakDelay");

    @EventHandler
    private static void onBlockBreakingCooldown(BlockBreakingCooldownEvent event) {
        if (instance.isActive()) event.cooldown = 0;
    }
}
