package com.stalkeroverhaul.event;

import com.stalkeroverhaul.Config;
import com.stalkeroverhaul.util.StalkState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Quebra qualquer bloco que esteja no caminho entre a criatura e o alvo. */
public final class BlockBreaker {
    private BlockBreaker() {}

    public static void tick(Mob mob) {
        if (!Config.BREAK_BLOCKS.get() || mob.tickCount % 4 != 0) return;
        if (!(mob.level() instanceof ServerLevel sl)) return;

        LivingEntity target = mob.getTarget();
        if (target == null && StalkState.isHunting(mob)) {
            target = sl.getNearestPlayer(mob, 64.0);
        }
        if (target == null || !target.isAlive()) return;
        if (target instanceof Player p && (p.isCreative() || p.isSpectator())) return;

        Vec3 dir = new Vec3(target.getX() - mob.getX(), 0, target.getZ() - mob.getZ());
        if (dir.lengthSqr() > 1.0E-4) dir = dir.normalize();

        AABB box = mob.getBoundingBox().inflate(0.4, 0.0, 0.4).move(dir.scale(0.9));
        double dy = target.getY() - mob.getY();
        if (dy > 1.5) box = box.expandTowards(0, 1.2, 0);
        else if (dy < -1.5) box = box.expandTowards(0, -1.2, 0);

        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX - 1.0E-6, box.maxY - 1.0E-6, box.maxZ - 1.0E-6);

        int budget = 48;
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (budget <= 0) break;
            BlockState state = sl.getBlockState(pos);
            if (state.isAir() || state.getBlock() instanceof LiquidBlock) continue;
            if (!Config.BREAK_UNBREAKABLE.get() && state.getDestroySpeed(sl, pos) < 0) continue;
            sl.destroyBlock(pos.immutable(), Config.DROP_BLOCKS.get(), mob);
            budget--;
        }
    }
}
