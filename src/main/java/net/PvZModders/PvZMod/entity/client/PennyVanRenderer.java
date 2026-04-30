package net.PvZModders.PvZMod.entity.client;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.PennyVanEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PennyVanRenderer extends MobRenderer<PennyVanEntity, pennytest<PennyVanEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PvZ2Mod.MOD_ID, "textures/entity/pennyvan.png");

    public PennyVanRenderer(EntityRendererProvider.Context context) {
        super(context, new pennytest<>(context.bakeLayer(pennytest.LAYER_LOCATION)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(PennyVanEntity entity) {
        return TEXTURE;
    }
}
