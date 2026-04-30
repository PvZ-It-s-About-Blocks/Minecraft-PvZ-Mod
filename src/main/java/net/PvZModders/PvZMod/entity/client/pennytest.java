// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class pennytest<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "pennytest"), "main");
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

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		portal.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		wheels.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}