package com.stalkeroverhaul.registry;

import com.stalkeroverhaul.StalkerOverhaul;
import com.stalkeroverhaul.entity.CaveDwellerEntity;
import com.stalkeroverhaul.entity.UnfortunateDevelopmentEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, StalkerOverhaul.MODID);

    public static final RegistryObject<EntityType<UnfortunateDevelopmentEntity>> UNFORTUNATE_DEVELOPMENT =
            ENTITIES.register("unfortunate_development", () -> EntityType.Builder
                    .of(UnfortunateDevelopmentEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 1.9F).clientTrackingRange(10)
                    .build("unfortunate_development"));

    public static final RegistryObject<EntityType<CaveDwellerEntity>> CAVE_DWELLER =
            ENTITIES.register("cave_dweller", () -> EntityType.Builder
                    .of(CaveDwellerEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 1.9F).clientTrackingRange(10)
                    .build("cave_dweller"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    private ModEntities() {}
}
