package com.stalkeroverhaul.util;

import com.stalkeroverhaul.entity.CaveDwellerEntity;
import com.stalkeroverhaul.entity.UnfortunateDevelopmentEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import com.stalkeroverhaul.Config;

public final class Targets {
    public enum Role { NONE, UNFORTUNATE, DWELLER }

    private Targets() {}

    public static Role role(Entity e) {
        if (e instanceof UnfortunateDevelopmentEntity) return Role.UNFORTUNATE;
        if (e instanceof CaveDwellerEntity) return Role.DWELLER;
        return Role.NONE;
    }

    public static boolean isNightmare(Entity e) {
        return role(e) != Role.NONE;
    }

    public static boolean isBuffedZombie(Entity e) {
        return e instanceof Zombie z && (Config.ZOMBIE_INCLUDE_PIGLINS.get() || !(z instanceof ZombifiedPiglin));
    }

    /** Aliados entre si: zumbis e as duas criaturas. */
    public static boolean isAlly(Entity e) {
        return e instanceof Zombie || isNightmare(e);
    }
}
