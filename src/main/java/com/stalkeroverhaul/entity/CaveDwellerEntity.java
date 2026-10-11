package com.stalkeroverhaul.entity;

import com.stalkeroverhaul.registry.ModSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class CaveDwellerEntity extends NightmareEntity {
    public CaveDwellerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level, "animation.cave_dweller.",
                "animation.cave_dweller.stalking", "animation.cave_dweller.stalking_idle",
                "animation.cave_dweller.climb");
    }

    public static AttributeSupplier attributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 65.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.FOLLOW_RANGE, 100.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .build();
    }

    @Override
    protected ModSounds.Set sounds() {
        return ModSounds.CD;
    }
}
