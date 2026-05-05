package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;

public class PvZPlantEntity extends SnowGolem {
    public PvZPlantEntity(EntityType<? extends SnowGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            PlantEntityManager.initializeSummonedPlant(this);
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        if (!PlantEntityManager.isPlant(this)) {
            super.aiStep();
        }
    }
}
