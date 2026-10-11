package com.stalkeroverhaul.entity;

import com.stalkeroverhaul.Config;
import com.stalkeroverhaul.registry.ModSounds;
import com.stalkeroverhaul.util.StalkState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import com.stalkeroverhaul.goal.HostileMeleeGoal;
import com.stalkeroverhaul.goal.StalkGoal;
import com.stalkeroverhaul.util.Targets;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * Base das duas criaturas. Recriação para 1.20.1 baseada nos mods
 * "Unfortunate Development" (Cadentem) e "Cave Noise" (Gargin), ambos MIT.
 */
public abstract class NightmareEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Boolean> HUNTING = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPOTTED = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STALKING = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final RawAnimation chaseAnim, chaseIdleAnim, calmRunAnim, calmIdleAnim, spottedAnim, climbAnim, stalkAnim, stalkIdleAnim;

    private int chaseSoundClock;
    private int climbSoundClock;
    private int calmTicks;
    private boolean wasHunting;

    protected NightmareEntity(EntityType<? extends Monster> type, Level level, String animPrefix,
                              String stalkName, String stalkIdleName, String climbName) {
        super(type, level);
        this.setMaxUpStep(1.1F);
        this.xpReward = 50;
        chaseAnim = loop(animPrefix + "new_run");
        chaseIdleAnim = loop(animPrefix + "run_idle");
        calmRunAnim = loop(animPrefix + "calm_move");
        calmIdleAnim = loop(animPrefix + "calm_idle");
        spottedAnim = RawAnimation.begin().then(animPrefix + "spotted", Animation.LoopType.HOLD_ON_LAST_FRAME);
        climbAnim = climbName == null ? null : loop(climbName);
        stalkAnim = stalkName == null ? null : loop(stalkName);
        stalkIdleAnim = stalkIdleName == null ? null : RawAnimation.begin().then(stalkIdleName, Animation.LoopType.HOLD_ON_LAST_FRAME);
    }

    private static RawAnimation loop(String name) {
        return RawAnimation.begin().then(name, Animation.LoopType.LOOP);
    }

    protected abstract ModSounds.Set sounds();

    // ---------- dados sincronizados ----------
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HUNTING, false);
        this.entityData.define(SPOTTED, false);
        this.entityData.define(STALKING, false);
        this.entityData.define(CLIMBING, false);
    }

    public void setSpotted(boolean v) { this.entityData.set(SPOTTED, v); }
    public void setStalking(boolean v) { this.entityData.set(STALKING, v); }
    public boolean isHuntingFlag() { return this.entityData.get(HUNTING); }

    // ---------- IA ----------
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new HostileMeleeGoal(this));
        this.goalSelector.addGoal(2, new StalkGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                e -> !(e instanceof Player) && !(e instanceof ArmorStand) && !Targets.isAlly(e) && e.isAlive()));
        registerExtraGoals();
    }

    protected void registerExtraGoals() {}

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    public boolean onClimbable() {
        return this.entityData.get(CLIMBING);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    // ---------- tick ----------
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        boolean hunting = StalkState.isHunting(this)
                || (getTarget() != null && !(getTarget() instanceof Player));
        if (hunting != this.entityData.get(HUNTING)) this.entityData.set(HUNTING, hunting);

        if (hunting) {
            calmTicks = 0;
            // mantém a caçada enquanto o jogador estiver perto
            if (getTarget() instanceof Player p && distanceToSqr(p) < 24.0 * 24.0) StalkState.startHunt(this);
            if (!wasHunting) playSet(sounds().spotted, 3.0F);
            if (--chaseSoundClock <= 0) {
                chaseSoundClock = 100;
                playRandom(sounds().chase, 3.0F);
            }
        } else {
            calmTicks++;
            if (calmTicks > Config.LEAVE_SECONDS.get() * 20) {
                disappear();
                return;
            }
        }
        wasHunting = hunting;

        boolean climb = hunting && this.horizontalCollision && !sounds().climb.isEmpty();
        if (climb != this.entityData.get(CLIMBING)) this.entityData.set(CLIMBING, climb);
        if (climb && --climbSoundClock <= 0) {
            climbSoundClock = 8;
            playRandom(sounds().climb, 2.0F);
        }
    }

    public void disappear() {
        playSet(sounds().disappear, 3.0F);
        this.discard();
    }

    // ---------- sons ----------
    private void playSet(RegistryObject<SoundEvent> s, float vol) {
        if (s != null) this.level().playSound(null, getX(), getY(), getZ(), s.get(), SoundSource.HOSTILE, vol, 1.0F);
    }

    private void playRandom(List<RegistryObject<SoundEvent>> list, float vol) {
        if (list.isEmpty()) return;
        playSet(list.get(this.random.nextInt(list.size())), vol);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (isHuntingFlag() || sounds().ambient.isEmpty()) return null;
        return sounds().ambient.get(this.random.nextInt(sounds().ambient.size())).get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        List<RegistryObject<SoundEvent>> h = sounds().hurt;
        return h.get(this.random.nextInt(h.size())).get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return sounds().death.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!isHuntingFlag()) return;
        List<RegistryObject<SoundEvent>> s = sounds().step;
        this.playSound(s.get(this.random.nextInt(s.size())).get(), 1.5F, 1.0F);
    }

    // ---------- animações (GeckoLib) ----------
    private PlayState predicate(AnimationState<NightmareEntity> state) {
        boolean moving = state.isMoving();
        if (this.entityData.get(HUNTING)) {
            if (this.entityData.get(CLIMBING) && climbAnim != null) return state.setAndContinue(climbAnim);
            return state.setAndContinue(moving ? chaseAnim : chaseIdleAnim);
        }
        if (this.entityData.get(STALKING) && stalkAnim != null && stalkIdleAnim != null) {
            return state.setAndContinue(moving ? stalkAnim : stalkIdleAnim);
        }
        if (this.entityData.get(SPOTTED) && !moving) return state.setAndContinue(spottedAnim);
        return state.setAndContinue(moving ? calmRunAnim : calmIdleAnim);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
