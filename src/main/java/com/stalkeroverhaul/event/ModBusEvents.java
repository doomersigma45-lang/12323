package com.stalkeroverhaul.event;

import com.stalkeroverhaul.StalkerOverhaul;
import com.stalkeroverhaul.client.NightmareRenderer;
import com.stalkeroverhaul.entity.CaveDwellerEntity;
import com.stalkeroverhaul.entity.UnfortunateDevelopmentEntity;
import com.stalkeroverhaul.registry.ModEntities;
import com.stalkeroverhaul.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class ModBusEvents {
    private ModBusEvents() {}

    @Mod.EventBusSubscriber(modid = StalkerOverhaul.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Common {
        @SubscribeEvent
        public static void attributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.UNFORTUNATE_DEVELOPMENT.get(), UnfortunateDevelopmentEntity.attributes());
            event.put(ModEntities.CAVE_DWELLER.get(), CaveDwellerEntity.attributes());
        }

        @SubscribeEvent
        public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(ModItems.UD_EGG);
                event.accept(ModItems.CD_EGG);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = StalkerOverhaul.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Client {
        @SubscribeEvent
        public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.UNFORTUNATE_DEVELOPMENT.get(),
                    ctx -> new NightmareRenderer<UnfortunateDevelopmentEntity>(ctx, "unfortunate_development"));
            event.registerEntityRenderer(ModEntities.CAVE_DWELLER.get(),
                    ctx -> new NightmareRenderer<CaveDwellerEntity>(ctx, "cave_dweller"));
        }
    }
}
