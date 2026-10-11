package com.stalkeroverhaul.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stalkeroverhaul.entity.NightmareEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NightmareRenderer<T extends NightmareEntity> extends GeoEntityRenderer<T> {
    public NightmareRenderer(EntityRendererProvider.Context context, String name) {
        super(context, new NightmareModel<T>(name));
        this.shadowRadius = 0.3F;
        this.addRenderLayer(new EyesLayer<T>(this, name));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.3F, 1.3F, 1.3F);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
