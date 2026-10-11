package com.stalkeroverhaul.registry;

import com.stalkeroverhaul.StalkerOverhaul;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, StalkerOverhaul.MODID);

    /** Conjunto de sons de uma criatura. Os arquivos ficam em sounds/ud/ e sounds/cd/. */
    public static final class Set {
        public final List<RegistryObject<SoundEvent>> ambient = new ArrayList<>();
        public final List<RegistryObject<SoundEvent>> step = new ArrayList<>();
        public final List<RegistryObject<SoundEvent>> chase = new ArrayList<>();
        public final List<RegistryObject<SoundEvent>> flee = new ArrayList<>();
        public final List<RegistryObject<SoundEvent>> hurt = new ArrayList<>();
        public final List<RegistryObject<SoundEvent>> climb = new ArrayList<>();
        public RegistryObject<SoundEvent> spotted;
        public RegistryObject<SoundEvent> disappear;
        public RegistryObject<SoundEvent> death;
    }

    public static final Set UD = new Set();
    public static final Set CD = new Set();

    private static RegistryObject<SoundEvent> reg(String prefix, String name) {
        ResourceLocation id = new ResourceLocation(StalkerOverhaul.MODID, prefix + "." + name);
        return SOUNDS.register(prefix + "." + name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    private static void fill(List<RegistryObject<SoundEvent>> list, String prefix, String base, int count) {
        for (int i = 1; i <= count; i++) list.add(reg(prefix, base + "_" + i));
    }

    static {
        // Unfortunate Development (21 sons)
        fill(UD.ambient, "ud", "cavenoise", 4);
        fill(UD.step, "ud", "chase_step", 4);
        fill(UD.chase, "ud", "chase", 4);
        fill(UD.flee, "ud", "flee", 2);
        fill(UD.hurt, "ud", "dweller_hurt", 4);
        UD.spotted = reg("ud", "spotted");
        UD.disappear = reg("ud", "disappear");
        UD.death = reg("ud", "dweller_death");

        // Cave Dweller (34 sons)
        fill(CD.ambient, "cd", "stalk", 5);
        fill(CD.step, "cd", "chase_step", 4);
        fill(CD.chase, "cd", "chase", 4);
        fill(CD.flee, "cd", "flee", 2);
        fill(CD.hurt, "cd", "dweller_hurt", 4);
        fill(CD.climb, "cd", "climb", 8);
        CD.spotted = reg("cd", "spotted");
        CD.disappear = reg("cd", "disappear");
        CD.death = reg("cd", "dweller_death");
        // cavenoise_1..4 do Cave Dweller ficam disponíveis como sons de ambiente distante
        fill(CD.ambient, "cd", "cavenoise", 4);
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }

    private ModSounds() {}
}
