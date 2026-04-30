package net.PvZModders.PvZMod.entity.client;// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.PvZModders.PvZMod.PvZ2Mod;
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
import net.minecraft.world.entity.Entity;

public class pennytest<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(PvZ2Mod.MOD_ID, "pennytest"), "main");
	private final ModelPart body;
	private final ModelPart portal;
	private final ModelPart wheels;
	private final ModelPart FLW;
	private final ModelPart RRW;
	private final ModelPart FRW;
	private final ModelPart RLW;

	public pennytest(ModelPart root) {
		this.body = root.getChild("body");
		this.portal = root.getChild("portal");
		this.wheels = root.getChild("wheels");
		this.FLW = this.wheels.getChild("FLW");
		this.RRW = this.wheels.getChild("RRW");
		this.FRW = this.wheels.getChild("FRW");
		this.RLW = this.wheels.getChild("RLW");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -26.0F, -29.0F, 40.0F, 30.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, 20.0F, 16.0F));

		PartDefinition portal = partdefinition.addOrReplaceChild("portal", CubeListBuilder.create().texOffs(0, 57).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition wheels = partdefinition.addOrReplaceChild("wheels", CubeListBuilder.create(), PartPose.offset(-14.0F, 20.0F, 16.0F));

		PartDefinition FLW = wheels.addOrReplaceChild("FLW", CubeListBuilder.create().texOffs(60, 57).addBox(21.0F, -4.0F, -4.0F, 9.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition RRW = wheels.addOrReplaceChild("RRW", CubeListBuilder.create().texOffs(60, 69).addBox(-4.0F, -4.0F, -31.0F, 9.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition FRW = wheels.addOrReplaceChild("FRW", CubeListBuilder.create().texOffs(34, 69).addBox(21.0F, -4.0F, -31.0F, 9.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition RLW = wheels.addOrReplaceChild("RLW", CubeListBuilder.create().texOffs(34, 57).addBox(-4.0F, -4.0F, -4.0F, 9.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		body.x = -14.0F;
		body.y = 20.0F;
		body.z = 16.0F;
		body.xScale = 1.0F;
		body.yScale = 1.0F;
		body.zScale = 1.0F;
		wheels.x = -14.0F;
		wheels.y = 20.0F;
		wheels.z = 16.0F;
		wheels.xScale = 1.0F;
		wheels.yScale = 1.0F;
		wheels.zScale = 1.0F;
		portal.x = 0.0F;
		portal.y = 24.0F;
		portal.z = 0.0F;
		portal.xScale = 1.0F;
		portal.yScale = 1.0F;
		portal.zScale = 1.0F;

		float seconds = Math.min(ageInTicks / 20.0F, 8.0F);
		if (seconds < 3.9F) {
			float portalPulse = 0.4F + (float) Math.sin(seconds * 14.0F) * 0.25F;
			portal.x = 17.0F;
			portal.xScale = 2.0F + portalPulse;
			portal.yScale = 0.8F + portalPulse;
			portal.zScale = 2.0F + portalPulse;

			body.x = -25.0F + seconds * 3.6F;
			body.xScale = 0.0F;
			body.yScale = 0.0F;
			body.zScale = 1.0F;
			wheels.xScale = 0.0F;
			wheels.yScale = 0.0F;
			wheels.zScale = 1.0F;
		} else if (seconds < 4.2F) {
			float emerge = (seconds - 3.9F) / 0.3F;
			body.xScale = emerge;
			body.yScale = 0.8F * emerge;
			wheels.xScale = emerge;
			wheels.yScale = emerge;
			portal.xScale = 8.5F - emerge * 7.5F;
			portal.yScale = 1.15F - emerge * 0.15F;
			portal.zScale = 3.1F - emerge * 2.1F;
		} else {
			portal.xScale = 0.1F;
			portal.yScale = 0.1F;
			portal.zScale = 0.1F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		portal.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		wheels.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
