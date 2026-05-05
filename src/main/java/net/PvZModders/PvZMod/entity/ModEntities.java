package net.PvZModders.PvZMod.entity;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.AllPlantsDevSummonEntity;
import net.PvZModders.PvZMod.entity.custom.FlyingPlaneEntity;
import net.PvZModders.PvZMod.entity.custom.JurassicDinosaurEntity;
import net.PvZModders.PvZMod.entity.custom.PennyVanEntity;
import net.PvZModders.PvZMod.entity.custom.PvZPlantEntity;
import net.PvZModders.PvZMod.entity.custom.PvZSunEntity;
import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.PvZModders.PvZMod.entity.custom.SpeedyMinecartEntity;
import net.PvZModders.PvZMod.entity.custom.WildWestMinecartEntity;
import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PvZ2Mod.MOD_ID);

    public static final RegistryObject<EntityType<PennyVanEntity>> PENNY_VAN =
            ENTITY_TYPES.register("penny_van", () -> EntityType.Builder.<PennyVanEntity>of(PennyVanEntity::new, MobCategory.CREATURE)
                    .sized(2.5F, 2.0F)
                    .build(PvZ2Mod.MOD_ID + ":penny_van"));
    public static final RegistryObject<EntityType<PvZSunEntity>> SUN =
            ENTITY_TYPES.register("sun", () -> EntityType.Builder.<PvZSunEntity>of(PvZSunEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(6)
                    .updateInterval(20)
                    .build(PvZ2Mod.MOD_ID + ":sun"));
    public static final RegistryObject<EntityType<PvZZombieEntity>> GARDEN_ZOMBIE =
            ENTITY_TYPES.register("garden_zombie", () -> EntityType.Builder.<PvZZombieEntity>of(PvZZombieEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(PvZ2Mod.MOD_ID + ":garden_zombie"));
    public static final RegistryObject<EntityType<AllPlantsDevSummonEntity>> ALL_PLANTS =
            ENTITY_TYPES.register("all_plants", () -> EntityType.Builder.<AllPlantsDevSummonEntity>of(AllPlantsDevSummonEntity::new, MobCategory.MISC)
                    .sized(0.7F, 1.9F)
                    .clientTrackingRange(4)
                    .build(PvZ2Mod.MOD_ID + ":all_plants"));
    public static final RegistryObject<EntityType<SpeedyMinecartEntity>> SPEEDY_MINECART =
            ENTITY_TYPES.register("speedy_minecart", () -> EntityType.Builder.<SpeedyMinecartEntity>of(SpeedyMinecartEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.7F)
                    .clientTrackingRange(10)
                    .build(PvZ2Mod.MOD_ID + ":speedy_minecart"));
    public static final RegistryObject<EntityType<WildWestMinecartEntity>> WILD_WEST_MINECART =
            ENTITY_TYPES.register("wild_west_minecart", () -> EntityType.Builder.<WildWestMinecartEntity>of(WildWestMinecartEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.35F)
                    .clientTrackingRange(10)
                    .build(PvZ2Mod.MOD_ID + ":wild_west_minecart"));
    public static final RegistryObject<EntityType<FlyingPlaneEntity>> FLYING_PLANE =
            ENTITY_TYPES.register("flying_plane", () -> EntityType.Builder.<FlyingPlaneEntity>of(FlyingPlaneEntity::new, MobCategory.MISC)
                    .sized(1.4F, 0.6F)
                    .clientTrackingRange(10)
                    .build(PvZ2Mod.MOD_ID + ":flying_plane"));
    public static final RegistryObject<EntityType<JurassicDinosaurEntity>> JURASSIC_DINOSAUR =
            ENTITY_TYPES.register("jurassic_dinosaur", () -> EntityType.Builder.<JurassicDinosaurEntity>of(JurassicDinosaurEntity::new, MobCategory.CREATURE)
                    .sized(1.9F, 1.75F)
                    .clientTrackingRange(10)
                    .build(PvZ2Mod.MOD_ID + ":jurassic_dinosaur"));
    public static final Map<String, RegistryObject<EntityType<PvZPlantEntity>>> PLANTS = registerPlantEntities();

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    private static Map<String, RegistryObject<EntityType<PvZPlantEntity>>> registerPlantEntities() {
        Map<String, RegistryObject<EntityType<PvZPlantEntity>>> plants = new LinkedHashMap<>();
        for (PlantSeedDefinition definition : PlantSeedDefinition.all()) {
            plants.put(definition.plantId(), ENTITY_TYPES.register(definition.plantId(), () -> EntityType.Builder.<PvZPlantEntity>of(PvZPlantEntity::new, MobCategory.CREATURE)
                    .sized(0.7F, 1.9F)
                    .clientTrackingRange(8)
                    .build(PvZ2Mod.MOD_ID + ":" + definition.plantId())));
        }
        return Map.copyOf(plants);
    }

    public static Iterable<RegistryObject<EntityType<PvZPlantEntity>>> plantEntityTypes() {
        return PLANTS.values();
    }

}

