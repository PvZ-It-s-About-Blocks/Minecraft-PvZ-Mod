package net.PvZModders.PvZMod.entity.client;// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class Sunflower<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart stems;
	private final ModelPart leaves;
	private final ModelPart bone2;

	public Sunflower(ModelPart root) {
		this.stems = root.getChild("stems");
		this.leaves = root.getChild("leaves");
		this.bone2 = root.getChild("bone2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition stems = partdefinition.addOrReplaceChild("stems", CubeListBuilder.create().texOffs(8, 9).addBox(-1.0F, -16.0F, -2.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 9).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition leaves = partdefinition.addOrReplaceChild("leaves", CubeListBuilder.create().texOffs(16, 9).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 20).addBox(0.0F, -1.0F, 0.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(16, 13).addBox(-4.0F, -1.0F, -3.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(16, 17).addBox(0.0F, -1.0F, -3.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition bone2 = partdefinition.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -22.0F, -1.0F, 10.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		stems.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leaves.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bone2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return stems;
	}
}