package com.stalkeroverhaul.event;

import com.stalkeroverhaul.Config;
import com.stalkeroverhaul.StalkerOverhaul;
import com.stalkeroverhaul.util.Targets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = StalkerOverhaul.MODID)
public final class MobSetupHandler {
    private static final UUID RAGE_ID = UUID.fromString("5b0f4a9e-3c1d-4f7a-9d52-7a0c1e2b8f31");
    private static final String INIT_TAG = "so_init";

    private MobSetupHandler() {}

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide) return;

        boolean zombie = Targets.isBuffedZombie(mob);
        Targets.Role role = Targets.role(mob);
        if (!zombie && role == Targets.Role.NONE) return;

        // (re)aplica atributos a partir da config
        if (mob.tickCount == 2 || mob.tickCount % 100 == 2) {
            if (zombie) {
                set(mob, Attributes.MAX_HEALTH, Config.ZOMBIE_HEALTH.get());
                set(mob, Attributes.ATTACK_DAMAGE, Config.ZOMBIE_DAMAGE.get());
                set(mob, Attributes.MOVEMENT_SPEED, Config.ZOMBIE_SPEED.get());
                set(mob, Attributes.ARMOR, Config.ZOMBIE_ARMOR.get());
                set(mob, Attributes.KNOCKBACK_RESISTANCE, Config.ZOMBIE_KB_RESIST.get());
                set(mob, Attributes.FOLLOW_RANGE, Math.max(48.0, Config.FOLLOW_RANGE.get() * 0.75));
            } else {
                boolean ud = role == Targets.Role.UNFORTUNATE;
                set(mob, Attributes.MAX_HEALTH, ud ? Config.UD_HEALTH.get() : Config.CD_HEALTH.get());
                set(mob, Attributes.ATTACK_DAMAGE, ud ? Config.UD_DAMAGE.get() : Config.CD_DAMAGE.get());
                set(mob, Attributes.FOLLOW_RANGE, Config.FOLLOW_RANGE.get());
                set(mob, Attributes.KNOCKBACK_RESISTANCE, 1.0);
            }
            CompoundTag data = mob.getPersistentData();
            if (!data.getBoolean(INIT_TAG)) {
                data.putBoolean(INIT_TAG, true);
                mob.setHealth(mob.getMaxHealth());
            }
        }

        if (role != Targets.Role.NONE) {
            if (mob.tickCount % 10 == 0) updateRage(mob);
            BlockBreaker.tick(mob);
            Ambient.tick(mob);
        }
    }

    private static void set(Mob mob, Attribute attr, double value) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst != null && inst.getBaseValue() != value) inst.setBaseValue(value);
    }

    /** Abaixo de 50% de vida a criatura fica mais rápida. */
    private static void updateRage(Mob mob) {
        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        boolean low = mob.getHealth() < mob.getMaxHealth() * 0.5F;
        AttributeModifier existing = speed.getModifier(RAGE_ID);
        if (low && existing == null) {
            speed.addTransientModifier(new AttributeModifier(RAGE_ID, "so_rage", 0.3,
                    AttributeModifier.Operation.MULTIPLY_BASE));
        } else if (!low && existing != null) {
            speed.removeModifier(RAGE_ID);
        }
    }
}
