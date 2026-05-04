package net.PvZModders.PvZMod.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.PvZSunEntity;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SunExperienceOrbRenderer extends ExperienceOrbRenderer {
    private static final float SUNDROP_SCALE = 0.95F;
    private static final ResourceLocation SUN_PILLAR_TEXTURE = new ResourceLocation(PvZ2Mod.MOD_ID, "textures/block/sunpillar.png");
    private static final float PILLAR_HALF_WIDTH = 0.18F;
    private static final float FALLING_PILLAR_BOTTOM = -16.0F;
    private static final float FALLING_PILLAR_TOP = 3.0F;
    private static final float RESTING_PILLAR_BOTTOM = 0.0F;
    private static final float RESTING_PILLAR_TOP = 3.0F;
    private final ItemRenderer itemRenderer;

    public SunExperienceOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ExperienceOrb orb, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (orb instanceof PvZSunEntity || SunManager.isSunOrb(orb) || orb.getValue() == SunManager.DEFAULT_SUN_VALUE) {
            if (!orb.isInvisible()) {
                renderSun(orb, partialTicks, poseStack, buffer);
            }
            return;
        }

        super.render(orb, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderSun(ExperienceOrb orb, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        renderSunPillar(orb, partialTicks, poseStack, buffer);
        renderItemModel(ModItems.SUNDROP.get().getDefaultInstance(), SUNDROP_SCALE, 90.0F, orb, poseStack, buffer);
    }

    private void renderItemModel(ItemStack stack, float scale, float yRotation, ExperienceOrb orb, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
        poseStack.scale(scale, scale, scale);
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, poseStack, buffer, orb.level(), orb.getId());
        poseStack.popPose();
    }

    private void renderSunPillar(ExperienceOrb orb, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        float bottom = orb.onGround() ? RESTING_PILLAR_BOTTOM : FALLING_PILLAR_BOTTOM;
        float top = orb.onGround() ? RESTING_PILLAR_TOP : FALLING_PILLAR_TOP;
        float rotation = (orb.tickCount + partialTicks) * 3.0F;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(SUN_PILLAR_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        addBeamPlane(consumer, pose, -PILLAR_HALF_WIDTH, PILLAR_HALF_WIDTH, bottom, top);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        addBeamPlane(consumer, poseStack.last(), -PILLAR_HALF_WIDTH, PILLAR_HALF_WIDTH, bottom, top);
        poseStack.popPose();
    }

    private static void addBeamPlane(VertexConsumer consumer, PoseStack.Pose pose, float minX, float maxX, float bottom, float top) {
        addBeamVertex(consumer, pose, minX, bottom, 0.0F, 0.0F, 1.0F);
        addBeamVertex(consumer, pose, maxX, bottom, 0.0F, 1.0F, 1.0F);
        addBeamVertex(consumer, pose, maxX, top, 0.0F, 1.0F, 0.0F);
        addBeamVertex(consumer, pose, minX, top, 0.0F, 0.0F, 0.0F);
    }

    private static void addBeamVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(255, 255, 255, 190)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
