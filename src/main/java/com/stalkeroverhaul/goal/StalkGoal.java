package com.stalkeroverhaul.goal;

import com.stalkeroverhaul.Config;
import com.stalkeroverhaul.entity.NightmareEntity;
import com.stalkeroverhaul.util.StalkState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Comportamento de stalker: segue o jogador à distância, congela quando é observado,
 * e só parte para o ataque se for encarado por muito tempo, se o jogador chegar perto,
 * se for ferido, ou numa emboscada aleatória.
 */
public class StalkGoal extends Goal {
    private final Mob mob;
    private Player player;
    private int stareTicks;
    private int moveCooldown;

    public StalkGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean blockedByOtherTarget() {
        LivingEntity t = mob.getTarget();
        return t != null && t.isAlive() && !(t instanceof Player);
    }

    @Override
    public boolean canUse() {
        if (!Config.STALKER_MODE.get() || StalkState.isHunting(mob) || blockedByOtherTarget()) return false;
        Player p = mob.level().getNearestPlayer(
                TargetingConditions.forCombat().ignoreLineOfSight().range(Config.STALK_DISTANCE_MAX.get() * 3.0), mob);
        if (p == null) return false;
        this.player = p;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return Config.STALKER_MODE.get() && !StalkState.isHunting(mob) && !blockedByOtherTarget()
                && player != null && player.isAlive() && !player.isCreative() && !player.isSpectator()
                && mob.distanceToSqr(player) < Math.pow(Config.STALK_DISTANCE_MAX.get() * 3.5, 2);
    }

    @Override
    public void start() {
        stareTicks = 0;
        moveCooldown = 0;
        if (mob.getTarget() instanceof Player) mob.setTarget(null);
    }

    @Override
    public void stop() {
        if (mob instanceof NightmareEntity n) {
            n.setSpotted(false);
            n.setStalking(false);
        }
        mob.getNavigation().stop();
        player = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private static boolean isLookingAt(Player p, Mob m) {
        Vec3 view = p.getViewVector(1.0F).normalize();
        Vec3 to = new Vec3(m.getX() - p.getX(), m.getEyeY() - p.getEyeY(), m.getZ() - p.getZ());
        double len = to.length();
        if (len < 1.0E-4) return true;
        return view.dot(to.scale(1.0 / len)) > 0.97 && p.hasLineOfSight(m);
    }

    private void goHunt() {
        StalkState.startHunt(mob);
        mob.setTarget(player);
    }

    @Override
    public void tick() {
        double d = mob.distanceTo(player);
        boolean seen = isLookingAt(player, mob);
        mob.getLookControl().setLookAt(player, 30.0F, 30.0F);

        stareTicks = seen ? stareTicks + 1 : Math.max(0, stareTicks - 2);
        if (mob instanceof NightmareEntity n) {
            n.setSpotted(seen);
            n.setStalking(!seen);
        }

        boolean hurtByPlayer = mob.getLastHurtByMob() instanceof Player
                && mob.tickCount - mob.getLastHurtByMobTimestamp() < 100;
        boolean stared = stareTicks > Config.STARE_TICKS.get() && d < Config.STALK_DISTANCE_MAX.get() + 10;
        boolean tooClose = d < Config.HUNT_DISTANCE.get();
        boolean ambush = !seen && d < Config.STALK_DISTANCE_MAX.get()
                && mob.tickCount % 20 == 0 && mob.getRandom().nextInt(Config.AMBUSH_ODDS.get()) == 0;

        if (hurtByPlayer || stared || tooClose || ambush) {
            goHunt();
            return;
        }

        if (moveCooldown > 0) moveCooldown--;

        if (seen && d < Config.STALK_DISTANCE_MAX.get() + 12) {
            // congela enquanto é observado
            mob.getNavigation().stop();
        } else if (d > Config.STALK_DISTANCE_MAX.get()) {
            if (moveCooldown == 0) {
                mob.getNavigation().moveTo(player, 1.0);
                moveCooldown = 10;
            }
        } else if (d > Config.STALK_DISTANCE_MIN.get()) {
            if (moveCooldown == 0) {
                mob.getNavigation().moveTo(player, 0.7);
                moveCooldown = 10;
            }
        } else {
            mob.getNavigation().stop();
        }
    }
}
