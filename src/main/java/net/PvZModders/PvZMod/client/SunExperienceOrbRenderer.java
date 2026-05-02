package net.PvZModders.PvZMod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SunExperienceOrbRenderer extends ExperienceOrbRenderer {
    private static final float SUNDROP_SCALE = 0.95F;
    private static final float PILLAR_SCALE = 1.0F;
    private final ItemRenderer itemRenderer;

    public SunExperienceOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ExperienceOrb orb, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (SunManager.isSunOrb(orb) || orb.getValue() == SunManager.DEFAULT_SUN_VALUE) {
            if (!orb.isInvisible()) {
                renderSun(orb, poseStack, buffer);
            }
            return;
        }

        super.render(orb, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderSun(ExperienceOrb orb, PoseStack poseStack, MultiBufferSource buffer) {
        if (!orb.onGround()) {
            renderItemModel(ModItems.SUN_PILLAR.get().getDefaultInstance(), PILLAR_SCALE, orb, poseStack, buffer);
        }
        renderItemModel(ModItems.SUNDROP.get().getDefaultInstance(), SUNDROP_SCALE, orb, poseStack, buffer);
    }

    private void renderItemModel(ItemStack stack, float scale, ExperienceOrb orb, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(scale, scale, scale);
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, poseStack, buffer, orb.level(), orb.getId());
        poseStack.popPose();
    }
}
