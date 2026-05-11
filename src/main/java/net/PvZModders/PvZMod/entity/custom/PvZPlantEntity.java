package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PvZPlantEntity extends SnowGolem implements GeoEntity {
    private static final RawAnimation PEASHOOTER_SPAWN = RawAnimation.begin().thenPlay("animation.peashooter.spawn");
    private static final RawAnimation PEASHOOTER_IDLE = RawAnimation.begin().thenLoop("animation.peashooter.idle");
    private static final RawAnimation PEASHOOTER_SHOOT = RawAnimation.begin().thenPlay("animation.peashooter.shoot");
    private static final RawAnimation PEASHOOTER_DEATH = RawAnimation.begin().thenPlay("animation.peashooter.death");

    private final AnimatableInstanceCache geckoCache = GeckoLibUtil.createInstanceCache(this);

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
        if (!PlantEntityManager.isPlant(this) && !isRegisteredPvZPlantType()) {
            super.aiStep();
        }
    }

    private boolean isRegisteredPvZPlantType() {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(getType());
        return entityId != null && PlantSeedDefinition.getByPlantId(entityId.getPath()).isPresent();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "peashooter_controller", 0, state -> {
            if (!"peashooter".equals(PlantEntityManager.plantId(this))) {
                return PlayState.STOP;
            }
            if (isDeadOrDying() || getHealth() <= 0.0F) {
                state.setAnimation(PEASHOOTER_DEATH);
            } else if (tickCount < 12) {
                state.setAnimation(PEASHOOTER_SPAWN);
            } else if (level().getGameTime() - getPersistentData().getLong(PlantEntityManager.LAST_SHOOT_TICK_TAG) <= 8L) {
                state.setAnimation(PEASHOOTER_SHOOT);
            } else {
                state.setAnimation(PEASHOOTER_IDLE);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geckoCache;
    }
}
