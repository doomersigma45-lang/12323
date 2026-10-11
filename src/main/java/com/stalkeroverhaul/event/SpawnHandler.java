package com.stalkeroverhaul.event;

import com.stalkeroverhaul.Config;
import com.stalkeroverhaul.StalkerOverhaul;
import com.stalkeroverhaul.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Spawn natural: o Cave Dweller aparece em cavernas escuras, o Unfortunate Development na superfície à noite. */
@Mod.EventBusSubscriber(modid = StalkerOverhaul.MODID)
public final class SpawnHandler {
    private static final Map<String, Long> NEXT = new HashMap<>();

    private SpawnHandler() {}

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel sl)) return;
        if (sl.getGameTime() % 40 != 0 || !Config.SPAWN_ENABLED.get()) return;
        if (sl.dimension() != Level.OVERWORLD || sl.getDifficulty() == Difficulty.PEACEFUL) return;

        for (ServerPlayer p : sl.players()) {
            if (p.isCreative() || p.isSpectator()) continue;
            attempt(sl, p, true);
            attempt(sl, p, false);
        }
    }

    private static void attempt(ServerLevel sl, ServerPlayer p, boolean dweller) {
        String key = p.getUUID() + (dweller ? ":cd" : ":ud");
        long now = sl.getGameTime();
        Long next = NEXT.get(key);
        if (next == null) {
            NEXT.put(key, now + delay(sl));
            return;
        }
        if (now < next) return;
        NEXT.put(key, now + delay(sl));

        EntityType<? extends Mob> type = dweller ? ModEntities.CAVE_DWELLER.get() : ModEntities.UNFORTUNATE_DEVELOPMENT.get();
        int alive = sl.getEntities(type, Entity::isAlive).size();
        if (alive >= Config.SPAWN_MAX_ALIVE.get()) return;

        for (int i = 0; i < 16; i++) {
            double ang = sl.random.nextDouble() * Math.PI * 2;
            double dist = 22 + sl.random.nextDouble() * 18;
            int x = p.getBlockX() + (int) (Math.cos(ang) * dist);
            int z = p.getBlockZ() + (int) (Math.sin(ang) * dist);
            for (int y = p.getBlockY() + 12; y >= p.getBlockY() - 24; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (!sl.hasChunkAt(pos)) break;
                if (!valid(sl, pos, type, dweller)) continue;
                Mob mob = type.create(sl);
                if (mob == null) return;
                mob.moveTo(x + 0.5, y, z + 0.5, sl.random.nextFloat() * 360F, 0F);
                mob.finalizeSpawn(sl, sl.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
                sl.addFreshEntity(mob);
                return;
            }
        }
    }

    private static long delay(ServerLevel sl) {
        int min = Config.SPAWN_MIN_SECONDS.get();
        int max = Math.max(min, Config.SPAWN_MAX_SECONDS.get());
        return (min + sl.random.nextInt(max - min + 1)) * 20L;
    }

    private static boolean valid(ServerLevel sl, BlockPos pos, EntityType<? extends Mob> type, boolean dweller) {
        BlockPos below = pos.below();
        if (!sl.getBlockState(below).isFaceSturdy(sl, below, Direction.UP)) return false;
        if (!sl.noCollision(type.getAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) return false;
        if (sl.getBrightness(LightLayer.BLOCK, pos) > 7) return false;
        if (dweller) {
            return !sl.canSeeSky(pos) && pos.getY() < 63;
        }
        return sl.canSeeSky(pos) && sl.isNight();
    }
}
