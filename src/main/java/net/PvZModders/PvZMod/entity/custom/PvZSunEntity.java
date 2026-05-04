package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;

public class PvZSunEntity extends ExperienceOrb {
    public PvZSunEntity(EntityType<? extends ExperienceOrb> entityType, Level level) {
        super(entityType, level);
    }

    public PvZSunEntity(Level level, double x, double y, double z, int value) {
        super(ModEntities.SUN.get(), level);
        setPos(x, y, z);
        if (level instanceof ServerLevel serverLevel) {
            SunManager.initializeSunOrb(this, serverLevel, value);
        }
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel serverLevel && !SunManager.isSunOrb(this)) {
            SunManager.initializeSunOrb(this, serverLevel, SunManager.DEFAULT_SUN_VALUE);
        }
        super.tick();
    }
}
