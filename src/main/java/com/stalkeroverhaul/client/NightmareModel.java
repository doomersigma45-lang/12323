package com.stalkeroverhaul.client;

import com.stalkeroverhaul.StalkerOverhaul;
import com.stalkeroverhaul.entity.NightmareEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class NightmareModel<T extends NightmareEntity> extends GeoModel<T> {
    private final String name;

    public NightmareModel(String name) {
        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(StalkerOverhaul.MODID, "geo/" + name + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation(StalkerOverhaul.MODID, "textures/entity/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(StalkerOverhaul.MODID, "animations/" + name + ".animation.json");
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(data.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(data.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
