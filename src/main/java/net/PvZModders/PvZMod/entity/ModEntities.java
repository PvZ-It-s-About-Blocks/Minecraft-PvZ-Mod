package net.PvZModders.PvZMod.entity;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.PennyVanEntity;
import net.PvZModders.PvZMod.entity.custom.SunflowerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PvZ2Mod.MOD_ID);

/*    public static final RegistryObject<EntityType<SunflowerEntity>> Sunflower =
            ENTITY_TYPES.register(name: "Sunflower", () -> EntityType.Builder.of(SunflowerEntity::new, MobCategory.CREATURE))
            .sized( pWidth: 2.5f, pHeight: 2,5f)
*/
    public static final RegistryObject<EntityType<PennyVanEntity>> PENNY_VAN =
            ENTITY_TYPES.register("penny_van", () -> EntityType.Builder.<PennyVanEntity>of(PennyVanEntity::new, MobCategory.CREATURE)
                    .sized(2.5F, 2.0F)
                    .build(PvZ2Mod.MOD_ID + ":penny_van"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


}

