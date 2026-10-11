package com.stalkeroverhaul.goal;

import com.stalkeroverhaul.Config;
import com.stalkeroverhaul.util.StalkState;
import com.stalkeroverhaul.util.Targets;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

/** Laser do Unfortunate Development: queima e deixa lento tudo que atravessa. */
public class LaserGoal extends Goal {
    private static final DustParticleOptions RED = new DustParticleOptions(new Vector3f(1.0F, 0.1F, 0.05F), 1.3F);

    private final Mob mob;
    private LivingEntity target;
    private int charge = -1;
    private long nextAllowed = -1;

    public LaserGoal(Mob mob) {
        this.mob = mob;
    }

    private boolean validTarget(LivingEntity t) {
        if (t == null || !t.isAlive() || t == mob || Targets.isAlly(t)) return false;
        if (t instanceof Player p && (p.isCreative() || p.isSpectator())) return false;
        return mob.distanceToSqr(t) <= Math.pow(Config.LASER_RANGE.get(), 2) && mob.hasLineOfSight(t);
    }

    @Override
    public boolean canUse() {
        if (!Config.LASER_ENABLED.get()) return false;
        long now = mob.level().getGameTime();
        if (nextAllowed < 0) nextAllowed = now + 60;
        if (now < nextAllowed) return false;

        LivingEntity t = mob.getTarget();
        boolean engaged = t != null && !(t instanceof Player) || StalkState.isHunting(mob);
        if (!engaged) return false;
        if (!validTarget(t)) {
            t = mob.level().getNearestPlayer(
                    TargetingConditions.forCombat().range(Config.LASER_RANGE.get()), mob);
            if (!validTarget(t)) return false;
        }
        target = t;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return charge > 0 && target != null && target.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        charge = Config.LASER_CHARGE.get();
        mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 3.0F, 0.8F);
    }

    @Override
    public void tick() {
        if (target == null) return;
        mob.getLookControl().setLookAt(target, 60.0F, 60.0F);
        if (mob.level() instanceof ServerLevel sl) {
            Vec3 eye = mob.getEyePosition();
            sl.sendParticles(ParticleTypes.FLAME, eye.x, eye.y, eye.z, 2, 0.25, 0.25, 0.25, 0.01);
        }
        if (--charge <= 0) {
            fire();
        }
    }

    @Override
    public void stop() {
        charge = -1;
        target = null;
        nextAllowed = mob.level().getGameTime() + Config.LASER_COOLDOWN.get() + mob.getRandom().nextInt(40);
    }

    private void fire() {
        if (!(mob.level() instanceof ServerLevel sl) || target == null) return;
        double range = Config.LASER_RANGE.get();
        Vec3 from = mob.getEyePosition();
        Vec3 aim = target.getEyePosition().subtract(from).normalize();
        Vec3 to = from.add(aim.scale(range));

        BlockHitResult hit = sl.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        Vec3 end = hit.getType() == HitResult.Type.MISS ? to : hit.getLocation();

        double len = from.distanceTo(end);
        for (double d = 0.6; d < len; d += 0.4) {
            Vec3 p = from.add(aim.scale(d));
            sl.sendParticles(RED, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            if (((int) (d / 0.4)) % 3 == 0) {
                sl.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.0);
            }
        }
        sl.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0F, 0.7F);

        AABB area = new AABB(from, end).inflate(1.5);
        List<LivingEntity> list = sl.getEntitiesOfClass(LivingEntity.class, area, e -> {
            if (e == mob || !e.isAlive() || Targets.isAlly(e)) return false;
            return !(e instanceof Player p && (p.isCreative() || p.isSpectator()));
        });
        for (LivingEntity e : list) {
            if (e.getBoundingBox().inflate(0.4).clip(from, end).isEmpty()) continue;
            if (Config.LASER_FIRE_SECONDS.get() > 0) e.setSecondsOnFire(Config.LASER_FIRE_SECONDS.get());
            if (Config.LASER_SLOW_TICKS.get() > 0) {
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Config.LASER_SLOW_TICKS.get(), 3));
            }
            float dmg = Config.LASER_DAMAGE.get().floatValue();
            if (dmg > 0) e.hurt(mob.damageSources().mobAttack(mob), dmg);
        }
        charge = 0;
    }
}
