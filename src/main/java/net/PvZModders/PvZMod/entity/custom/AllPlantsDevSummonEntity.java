package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class AllPlantsDevSummonEntity extends SnowGolem {
    public AllPlantsDevSummonEntity(EntityType<? extends SnowGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            spawnAllPlantsRow();
            discard();
            return;
        }
        super.tick();
    }

    private void spawnAllPlantsRow() {
        BlockPos origin = blockPosition();
        int index = 0;
        for (PlantSeedDefinition definition : PlantSeedDefinition.all()) {
            BlockPos plantPos = origin.east(index * 2);
            preparePlantSpace(plantPos);

            EntityType<PvZPlantEntity> plantType = ModEntities.PLANTS.get(definition.plantId()).get();
            PvZPlantEntity plant = plantType.create(level());
            if (plant != null) {
                plant.moveTo(plantPos.getX() + 0.5D, plantPos.getY(), plantPos.getZ() + 0.5D, 0.0F, 0.0F);
                PlantEntityManager.initializePlantEntity(plant, definition, level().getGameTime());
                level().addFreshEntity(plant);
            }
            index++;
        }
    }

    private void preparePlantSpace(BlockPos plantPos) {
        level().setBlock(plantPos.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        level().setBlock(plantPos, Blocks.AIR.defaultBlockState(), 3);
        level().setBlock(plantPos.above(), Blocks.AIR.defaultBlockState(), 3);
        level().setBlock(plantPos.above(2), Blocks.AIR.defaultBlockState(), 3);
    }
}
