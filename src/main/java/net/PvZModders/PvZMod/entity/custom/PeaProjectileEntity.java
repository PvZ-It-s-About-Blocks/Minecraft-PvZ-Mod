package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class PeaProjectileEntity extends Snowball {
    public PeaProjectileEntity(EntityType<? extends PeaProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PeaProjectileEntity(Level level, LivingEntity owner) {
        super(ModEntities.PEA_PROJECTILE.get(), level);
        setOwner(owner);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.PEA.get();
    }
}
