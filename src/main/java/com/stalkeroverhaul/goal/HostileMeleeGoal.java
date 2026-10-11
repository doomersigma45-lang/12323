package com.stalkeroverhaul.goal;

import com.stalkeroverhaul.util.StalkState;
import com.stalkeroverhaul.util.Targets;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

/** Persegue e ataca de forma implacável: qualquer mob (menos aliados) e o jogador durante a caçada. */
public class HostileMeleeGoal extends Goal {
    private final Mob mob;
    private int attackCooldown;
    private int repath;

    public HostileMeleeGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private LivingEntity prey() {
        LivingEntity t = mob.getTarget();
        if (t == null || !t.isAlive() || Targets.isAlly(t)) return null;
        if (t instanceof Player p) {
            // jogadores só são atacados durante a caçada do stalker
            if (p.isCreative() || p.isSpectator() || !StalkState.isHunting(mob)) return null;
        }
        return t;
    }

    @Override
    public boolean canUse() {
        return prey() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return prey() != null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        attackCooldown = 0;
        repath = 0;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity t = prey();
        if (t == null) return;
        mob.getLookControl().setLookAt(t, 30.0F, 30.0F);

        if (--repath <= 0) {
            repath = 8;
            mob.getNavigation().moveTo(t, 1.35);
        }
        Path p = mob.getNavigation().getPath();
        if (p == null || !p.canReach()) {
            mob.getMoveControl().setWantedPosition(t.getX(), t.getY(), t.getZ(), 1.35);
            if (mob.horizontalCollision) mob.getJumpControl().jump();
        }

        if (attackCooldown > 0) attackCooldown--;
        double w = mob.getBbWidth() * 2.0;
        double reach = Math.max(w * w + t.getBbWidth(), 6.0);
        if (attackCooldown <= 0 && mob.distanceToSqr(t) <= reach) {
            attackCooldown = 15;
            mob.swing(InteractionHand.MAIN_HAND);
            if (mob.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                mob.doHurtTarget(t);
            } else {
                t.hurt(mob.damageSources().mobAttack(mob), 20.0F);
            }
        }
    }
}
