package net.PvZModders.PvZMod.entity.client;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.PvZPlantEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PeashooterGeoModel extends GeoModel<PvZPlantEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(PvZ2Mod.MOD_ID, "geo/peashooter.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(PvZ2Mod.MOD_ID, "textures/entity/plants/peashooter.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(PvZ2Mod.MOD_ID, "animations/peashooter.animation.json");

    @Override
    public ResourceLocation getModelResource(PvZPlantEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PvZPlantEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PvZPlantEntity animatable) {
        return ANIMATION;
    }
}
