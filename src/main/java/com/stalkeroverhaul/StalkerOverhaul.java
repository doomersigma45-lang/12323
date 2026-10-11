package com.stalkeroverhaul;

import com.stalkeroverhaul.registry.ModEntities;
import com.stalkeroverhaul.registry.ModItems;
import com.stalkeroverhaul.registry.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(StalkerOverhaul.MODID)
public class StalkerOverhaul {
    public static final String MODID = "stalker_overhaul";

    public StalkerOverhaul() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.register(bus);
        ModItems.register(bus);
        ModSounds.register(bus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
