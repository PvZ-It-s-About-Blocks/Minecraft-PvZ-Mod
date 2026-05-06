package net.PvZModders.PvZMod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.PvZPlantEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PeashooterModel<T extends PvZPlantEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(PvZ2Mod.MOD_ID, "peashooter"), "main");

    private final ModelPart root;
    private final ModelPart lowerLimb;
    private final ModelPart upperLimb;
    private final ModelPart mainHead;
    private final ModelPart snout;
    private final ModelPart headLeaf;
    private final ModelPart leaf;
    private final ModelPart leaf2;
    private final ModelPart leaf3;
    private final ModelPart leaf4;

    public PeashooterModel(ModelPart root) {
        this.root = root.getChild("root");
        this.lowerLimb = this.root.getChild("lower_limb");
        this.upperLimb = this.root.getChild("upper_limb");
        this.mainHead = this.root.getChild("main_head");
        this.snout = this.root.getChild("snout");
        this.headLeaf = this.root.getChild("head_leaf");
        this.leaf = this.root.getChild("leaf");
        this.leaf2 = this.root.getChild("leaf2");
        this.leaf3 = this.root.getChild("leaf3");
        this.leaf4 = this.root.getChild("leaf4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootPart = mesh.getRoot();
        PartDefinition root = rootPart.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        root.addOrReplaceChild("lower_limb",
                CubeListBuilder.create().texOffs(0, 30).addBox(-2.0F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition upperLimb = root.addOrReplaceChild("upper_limb",
                CubeListBuilder.create()
                        .texOffs(28, 6).addBox(-2.0F, -10.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 20).addBox(0.0F, -10.5F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("main_head",
                CubeListBuilder.create().texOffs(6, 0).addBox(-5.0F, -14.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("snout",
                CubeListBuilder.create()
                        .texOffs(0, 12).addBox(4.0F, -13.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(26, 27).addBox(3.0F, -12.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("head_leaf",
                CubeListBuilder.create()
                        .texOffs(0, 24).addBox(-2.0F, -15.0F, -2.0F, 3.0F, 0.1F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 27).addBox(1.0F, -15.1F, -1.5F, 3.0F, 0.1F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 4).addBox(1.1F, -14.9F, -1.0F, 4.0F, 0.1F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1058F, -0.0250F, 2.6612F));

        addLeaf(root, "leaf", -2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4800F, 12, 12, 14, 24, 26, 18);
        addLeaf(root, "leaf2", -1.0F, 0.0F, 0.0F, -3.1058F, -0.0250F, 2.6612F, 12, 16, 26, 12, 0, 28);
        addLeaf(root, "leaf3", -1.0F, 0.0F, 0.0F, -1.6440F, -1.0454F, 1.6305F, 0, 20, 26, 15, 28, 0);
        addLeaf(root, "leaf4", -1.0F, 0.0F, 0.0F, 1.6855F, 1.0824F, 1.6974F, 14, 20, 26, 24, 28, 2);

        return LayerDefinition.create(mesh, 64, 64);
    }

    private static void addLeaf(PartDefinition root, String name, float x, float y, float z, float xRot, float yRot, float zRot,
                                int baseU, int baseV, int midU, int midV, int tipU, int tipV) {
        root.addOrReplaceChild(name,
                CubeListBuilder.create()
                        .texOffs(baseU, baseV).addBox(-1.0F, -0.05F, -2.0F, 3.0F, 0.1F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(midU, midV).addBox(2.0F, -0.05F, -1.5F, 3.0F, 0.1F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(tipU, tipV).addBox(2.1F, -0.25F, -1.0F, 4.0F, 0.1F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root.getAllParts().forEach(ModelPart::resetPose);
        root.yRot = Mth.HALF_PI;

        float idleA = Mth.sin(ageInTicks * 0.08F);
        float idleB = Mth.sin(ageInTicks * 0.13F + 0.9F);
        lowerLimb.zRot = Mth.DEG_TO_RAD * (10.0F + idleA * 5.0F);
        upperLimb.zRot = Mth.DEG_TO_RAD * (-20.0F + idleB * 4.0F);
        mainHead.y += idleA * 0.35F;
        snout.y += idleA * 0.35F;
        headLeaf.zRot += idleB * 0.12F;
        leaf.zRot += idleA * 0.07F;
        leaf2.zRot -= idleA * 0.06F;
        leaf3.xRot += idleB * 0.04F;
        leaf4.xRot -= idleB * 0.04F;

        float attackPulse = Mth.sin(this.attackTime * Mth.PI);
        if (attackPulse > 0.0F) {
            mainHead.x -= attackPulse * 1.6F;
            snout.x -= attackPulse * 2.4F;
            snout.xScale = 1.0F + attackPulse * 0.28F;
            snout.yScale = 1.0F - attackPulse * 0.08F;
        }

        if (entity.deathTime > 0) {
            float death = Mth.clamp((entity.deathTime + ageInTicks) / 18.0F, 0.0F, 1.0F);
            root.zRot += death * Mth.HALF_PI;
            root.y += death * 7.0F;
            mainHead.zRot += death * 1.2F;
            snout.zRot += death * 1.5F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
