package com.stalkeroverhaul.event;

import com.stalkeroverhaul.StalkerOverhaul;
import com.stalkeroverhaul.util.Targets;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StalkerOverhaul.MODID)
public final class CombatHandler {
    private CombatHandler() {}

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource src = event.getSource();
        Targets.Role role = Targets.role(victim);

        if (role != Targets.Role.NONE) {
            // imune a projéteis (flechas, tridentes, bolas de fogo...) e a poções
            if (src.is(DamageTypeTags.IS_PROJECTILE)
                    || src.getDirectEntity() instanceof Projectile
                    || src.is(DamageTypes.MAGIC)
                    || src.is(DamageTypes.INDIRECT_MAGIC)) {
                event.setCanceled(true);
                return;
            }
            // Unfortunate Development aguenta explosões
            if (role == Targets.Role.UNFORTUNATE && src.is(DamageTypeTags.IS_EXPLOSION)) {
                event.setCanceled(true);
                return;
            }
        }

        // zumbis e as criaturas não se machucam
        Entity attacker = src.getEntity();
        if (attacker != null && Targets.isAlly(attacker) && Targets.isAlly(victim)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult hit && Targets.isNightmare(hit.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (Targets.isNightmare(event.getEntity())
                && event.getEffectInstance().getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().removeIf(e -> Targets.role(e) == Targets.Role.UNFORTUNATE);
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity next = event.getNewTarget();
        if (next != null && Targets.isNightmare(event.getEntity()) && Targets.isAlly(next)) {
            event.setNewTarget(null);
        }
    }
}
