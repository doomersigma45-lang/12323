package com.stalkeroverhaul.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stalkeroverhaul.StalkerOverhaul;
import com.stalkeroverhaul.entity.NightmareEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class EyesLayer<T extends NightmareEntity> extends GeoRenderLayer<T> {
    private final ResourceLocation texture;

    public EyesLayer(GeoRenderer<T> renderer, String name) {
        super(renderer);
        this.texture = new ResourceLocation(StalkerOverhaul.MODID, "textures/entity/" + name + "_eyes.png");
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        RenderType eyes = RenderType.eyes(texture);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, eyes,
                bufferSource.getBuffer(eyes), partialTick, 0xF000F0, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
