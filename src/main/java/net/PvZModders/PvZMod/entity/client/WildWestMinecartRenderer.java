package net.PvZModders.PvZMod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.PvZModders.PvZMod.entity.custom.WildWestMinecartEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;

public class WildWestMinecartRenderer extends MinecartRenderer<WildWestMinecartEntity> {
    public WildWestMinecartRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.MINECART);
    }

    @Override
    public void render(WildWestMinecartEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.0F, 0.5F, 1.0F);
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
