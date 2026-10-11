package com.stalkeroverhaul.event;

import com.stalkeroverhaul.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Efeitos de atmosfera ao redor das criaturas. */
public final class Ambient {
    private Ambient() {}

    public static void tick(Mob mob) {
        if (!Config.AMBIENT.get() || !(mob.level() instanceof ServerLevel sl)) return;

        if (mob.tickCount % 20 == 0) {
            BlockPos c = mob.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(c.offset(-6, -2, -6), c.offset(6, 4, 6))) {
                BlockState s = sl.getBlockState(pos);
                if (s.getBlock() instanceof TorchBlock || s.getBlock() instanceof LanternBlock) {
                    sl.destroyBlock(pos.immutable(), true, mob);
                }
            }
        }

        if (mob.tickCount % 400 == 200) {
            for (ServerPlayer p : sl.players()) {
                if (p.isCreative() || p.isSpectator()) continue;
                if (p.distanceToSqr(mob) < 14.0 * 14.0) {
                    p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0, false, false));
                    sl.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.WARDEN_HEARTBEAT,
                            SoundSource.HOSTILE, 1.0F, 0.9F + mob.getRandom().nextFloat() * 0.2F);
                }
            }
        }
    }
}
