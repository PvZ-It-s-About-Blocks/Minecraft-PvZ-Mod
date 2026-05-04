package net.PvZModders.PvZMod.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class PvZZombieEntity extends Zombie {
    public PvZZombieEntity(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }
}
