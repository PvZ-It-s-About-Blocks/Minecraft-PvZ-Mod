package net.PvZModders.PvZMod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.world.entity.ExperienceOrb;

public class SunExperienceOrbRenderer extends ExperienceOrbRenderer {
    private static final float SUN_SCALE = 1.7F;

    public SunExperienceOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ExperienceOrb orb, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (orb.getValue() == SunManager.DEFAULT_SUN_VALUE) {
            poseStack.pushPose();
            poseStack.scale(SUN_SCALE, SUN_SCALE, SUN_SCALE);
            super.render(orb, entityYaw, partialTicks, poseStack, buffer, packedLight);
            poseStack.popPose();
            return;
        }

        super.render(orb, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
