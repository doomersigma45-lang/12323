package com.stalkeroverhaul.entity;

import com.stalkeroverhaul.goal.LaserGoal;
import com.stalkeroverhaul.registry.ModSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class UnfortunateDevelopmentEntity extends NightmareEntity {
    public UnfortunateDevelopmentEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level, "animation.creation.", null, null, null);
    }

    public static AttributeSupplier attributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 100.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .build();
    }

    @Override
    protected void registerExtraGoals() {
        this.goalSelector.addGoal(3, new LaserGoal(this));
    }

    @Override
    protected ModSounds.Set sounds() {
        return ModSounds.UD;
    }
}
