package com.stalkeroverhaul;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class Config {
    public static final ForgeConfigSpec SPEC;

    // Unfortunate Development
    public static final ForgeConfigSpec.DoubleValue UD_HEALTH;
    public static final ForgeConfigSpec.DoubleValue UD_DAMAGE;
    // Cave Noise (Cave Dweller)
    public static final ForgeConfigSpec.DoubleValue CD_HEALTH;
    public static final ForgeConfigSpec.DoubleValue CD_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue FOLLOW_RANGE;

    // Zumbis
    public static final ForgeConfigSpec.DoubleValue ZOMBIE_HEALTH;
    public static final ForgeConfigSpec.DoubleValue ZOMBIE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue ZOMBIE_SPEED;
    public static final ForgeConfigSpec.DoubleValue ZOMBIE_ARMOR;
    public static final ForgeConfigSpec.DoubleValue ZOMBIE_KB_RESIST;
    public static final ForgeConfigSpec.BooleanValue ZOMBIE_INCLUDE_PIGLINS;

    // Quebra de blocos
    public static final ForgeConfigSpec.BooleanValue BREAK_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue BREAK_UNBREAKABLE;
    public static final ForgeConfigSpec.BooleanValue DROP_BLOCKS;

    // Stalker
    public static final ForgeConfigSpec.BooleanValue STALKER_MODE;
    public static final ForgeConfigSpec.DoubleValue STALK_DISTANCE_MAX;
    public static final ForgeConfigSpec.DoubleValue STALK_DISTANCE_MIN;
    public static final ForgeConfigSpec.DoubleValue HUNT_DISTANCE;
    public static final ForgeConfigSpec.IntValue STARE_TICKS;
    public static final ForgeConfigSpec.IntValue AMBUSH_ODDS;
    public static final ForgeConfigSpec.IntValue HUNT_DURATION;

    // Laser
    public static final ForgeConfigSpec.BooleanValue LASER_ENABLED;
    public static final ForgeConfigSpec.DoubleValue LASER_RANGE;
    public static final ForgeConfigSpec.DoubleValue LASER_DAMAGE;
    public static final ForgeConfigSpec.IntValue LASER_COOLDOWN;
    public static final ForgeConfigSpec.IntValue LASER_CHARGE;
    public static final ForgeConfigSpec.IntValue LASER_SLOW_TICKS;
    public static final ForgeConfigSpec.IntValue LASER_FIRE_SECONDS;

    // Spawn natural
    public static final ForgeConfigSpec.BooleanValue SPAWN_ENABLED;
    public static final ForgeConfigSpec.IntValue SPAWN_MIN_SECONDS;
    public static final ForgeConfigSpec.IntValue SPAWN_MAX_SECONDS;
    public static final ForgeConfigSpec.IntValue SPAWN_MAX_ALIVE;
    public static final ForgeConfigSpec.IntValue LEAVE_SECONDS;

    // Diversos
    public static final ForgeConfigSpec.BooleanValue AMBIENT;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("creatures");
        UD_HEALTH = b.defineInRange("unfortunateHealth", 1000.0, 1.0, 1024.0);
        UD_DAMAGE = b.defineInRange("unfortunateDamage", 45.0, 1.0, 2048.0);
        CD_HEALTH = b.defineInRange("dwellerHealth", 800.0, 1.0, 1024.0);
        CD_DAMAGE = b.defineInRange("dwellerDamage", 40.0, 1.0, 2048.0);
        FOLLOW_RANGE = b.defineInRange("followRange", 64.0, 8.0, 2048.0);
        b.pop();

        b.push("zombies");
        ZOMBIE_HEALTH = b.comment("Zumbi normal = 20 HP").defineInRange("health", 40.0, 1.0, 1024.0);
        ZOMBIE_DAMAGE = b.defineInRange("damage", 15.0, 1.0, 2048.0);
        ZOMBIE_SPEED = b.comment("Zumbi normal = 0.23").defineInRange("speed", 0.36, 0.05, 2.0);
        ZOMBIE_ARMOR = b.defineInRange("armor", 20.0, 0.0, 30.0);
        ZOMBIE_KB_RESIST = b.defineInRange("knockbackResistance", 0.8, 0.0, 1.0);
        ZOMBIE_INCLUDE_PIGLINS = b.comment("Incluir zombified piglins no buff")
                .define("includeZombifiedPiglins", false);
        b.pop();

        b.push("blockBreaking");
        BREAK_BLOCKS = b.define("enabled", true);
        BREAK_UNBREAKABLE = b.comment("Quebrar também bedrock, barrier, etc.").define("breakUnbreakable", true);
        DROP_BLOCKS = b.comment("Blocos quebrados dropam itens (pode dar lag)").define("dropItems", false);
        b.pop();

        b.push("stalker");
        STALKER_MODE = b.define("enabled", true);
        STALK_DISTANCE_MAX = b.defineInRange("maxDistance", 32.0, 8.0, 128.0);
        STALK_DISTANCE_MIN = b.defineInRange("minDistance", 14.0, 4.0, 64.0);
        HUNT_DISTANCE = b.comment("Se o jogador chegar mais perto que isso, a caçada começa")
                .defineInRange("huntDistance", 8.0, 2.0, 32.0);
        STARE_TICKS = b.comment("Ticks encarando a criatura até ela atacar").defineInRange("stareTicks", 80, 10, 1200);
        AMBUSH_ODDS = b.comment("1 em N por segundo de atacar de surpresa quando não está sendo observada")
                .defineInRange("ambushOdds", 40, 2, 100000);
        HUNT_DURATION = b.comment("Duração da caçada em ticks").defineInRange("huntDuration", 900, 100, 72000);
        b.pop();

        b.push("laser");
        LASER_ENABLED = b.define("enabled", true);
        LASER_RANGE = b.defineInRange("range", 32.0, 4.0, 128.0);
        LASER_DAMAGE = b.defineInRange("damage", 8.0, 0.0, 1000.0);
        LASER_COOLDOWN = b.defineInRange("cooldownTicks", 140, 20, 12000);
        LASER_CHARGE = b.defineInRange("chargeTicks", 25, 5, 200);
        LASER_SLOW_TICKS = b.defineInRange("slownessTicks", 160, 0, 12000);
        LASER_FIRE_SECONDS = b.defineInRange("fireSeconds", 8, 0, 600);
        b.pop();

        b.push("spawning");
        SPAWN_ENABLED = b.comment("Spawn natural das duas criaturas").define("enabled", true);
        SPAWN_MIN_SECONDS = b.comment("Tempo mínimo entre tentativas de spawn, por jogador").defineInRange("minSeconds", 120, 5, 86400);
        SPAWN_MAX_SECONDS = b.defineInRange("maxSeconds", 300, 5, 86400);
        SPAWN_MAX_ALIVE = b.comment("Máximo de cada criatura no mundo ao mesmo tempo").defineInRange("maxAlive", 2, 0, 100);
        LEAVE_SECONDS = b.comment("Segundos sem caçar até a criatura ir embora").defineInRange("leaveSeconds", 400, 10, 86400);
        b.pop();

        b.push("misc");
        AMBIENT = b.define("ambientEffects", true);
        b.pop();

        SPEC = b.build();
    }

    private Config() {}
}
