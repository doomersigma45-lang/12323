package com.stalkeroverhaul.util;

import com.stalkeroverhaul.Config;
import net.minecraft.world.entity.Mob;

public final class StalkState {
    private static final String KEY = "so_hunt_until";

    private StalkState() {}

    public static boolean isHunting(Mob mob) {
        return mob.getPersistentData().getLong(KEY) > mob.level().getGameTime();
    }

    public static void startHunt(Mob mob) {
        mob.getPersistentData().putLong(KEY, mob.level().getGameTime() + Config.HUNT_DURATION.get());
    }

    public static void extendHunt(Mob mob) {
        startHunt(mob);
    }

    public static void stopHunt(Mob mob) {
        mob.getPersistentData().putLong(KEY, 0L);
    }
}
