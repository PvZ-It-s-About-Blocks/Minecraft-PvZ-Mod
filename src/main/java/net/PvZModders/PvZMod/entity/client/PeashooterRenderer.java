package net.PvZModders.PvZMod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.PvZModders.PvZMod.entity.custom.PvZPlantEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PeashooterRenderer extends GeoEntityRenderer<PvZPlantEntity> {
    public PeashooterRenderer(EntityRendererProvider.Context context) {
        super(context, new PeashooterGeoModel());
        this.shadowRadius = 0.35F;
    }

    @Override
    public void render(PvZPlantEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float spawnScale = Mth.clamp((entity.tickCount + partialTick) / 12.0F, 0.15F, 1.0F);
        poseStack.scale(0.9F * spawnScale, 0.9F * spawnScale, 0.9F * spawnScale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    protected void renderNameTag(PvZPlantEntity entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, -1.2D, 0.0D);
        super.renderNameTag(entity, displayName, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
