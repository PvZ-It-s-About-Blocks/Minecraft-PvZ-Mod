package net.PvZModders.PvZMod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.PvZPlantEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PeashooterRenderer extends MobRenderer<PvZPlantEntity, PeashooterModel<PvZPlantEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PvZ2Mod.MOD_ID, "textures/entity/peashooter.png");

    public PeashooterRenderer(EntityRendererProvider.Context context) {
        super(context, new PeashooterModel<>(context.bakeLayer(PeashooterModel.LAYER_LOCATION)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(PvZPlantEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(PvZPlantEntity entity, PoseStack poseStack, float partialTickTime) {
        float spawnScale = Mth.clamp((entity.tickCount + partialTickTime) / 12.0F, 0.15F, 1.0F);
        poseStack.scale(0.9F * spawnScale, 0.9F * spawnScale, 0.9F * spawnScale);
    }

    @Override
    protected void renderNameTag(PvZPlantEntity entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, -1.2D, 0.0D);
        super.renderNameTag(entity, displayName, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
