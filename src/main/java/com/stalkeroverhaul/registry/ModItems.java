package com.stalkeroverhaul.registry;

import com.stalkeroverhaul.StalkerOverhaul;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, StalkerOverhaul.MODID);

    public static final RegistryObject<Item> UD_EGG = ITEMS.register("unfortunate_development_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.UNFORTUNATE_DEVELOPMENT, 0x1A1A1A, 0xFF2222, new Item.Properties()));

    public static final RegistryObject<Item> CD_EGG = ITEMS.register("cave_dweller_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CAVE_DWELLER, 0x2B2B2B, 0xE0E0E0, new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private ModItems() {}
}
