package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SpeedyMinecartEntity extends Minecart {
    public static final double BASE_SPEED = 0.12D;
    public static final double MAX_SPEED_BASE = 0.48D;
    public static final double MAX_SPEED_UPGRADED = 0.72D;
    public static final int SUN_FOR_MAX_SPEED = 300;

    public SpeedyMinecartEntity(EntityType<? extends Minecart> entityType, Level level) {
        super(entityType, level);
    }

    public SpeedyMinecartEntity(Level level, double x, double y, double z) {
        super(ModEntities.SPEEDY_MINECART.get(), level);
        setPos(x, y, z);
        xo = x;
        yo = y;
        zo = z;
    }

    @Override
    public void tick() {
        super.tick();
        Entity passenger = getFirstPassenger();
        if (!(passenger instanceof Player player)) {
            return;
        }

        Vec3 look = player.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0.0D, look.z);
        if (forward.lengthSqr() < 1.0E-4D) {
            return;
        }

        double speed = getSpeedFromSun(player);
        Vec3 desired = forward.normalize().scale(speed);
        double vertical = onGround() ? getDeltaMovement().y : Math.max(getDeltaMovement().y - 0.03D, -0.6D);
        setDeltaMovement(desired.x, vertical, desired.z);
    }

    public static double getSpeedFromSun(Player player) {
        int sunCap = Math.max(1, SunManager.getSunCap(player));
        double maxSpeed = sunCap > SunManager.DEFAULT_SUN_CAP ? MAX_SPEED_UPGRADED : MAX_SPEED_BASE;
        double t = Math.min(1.0D, SunManager.getSun(player) / (double) SUN_FOR_MAX_SPEED);
        return BASE_SPEED + (maxSpeed - BASE_SPEED) * t;
    }

    @Override
    protected double getMaxSpeed() {
        Entity passenger = getFirstPassenger();
        if (passenger instanceof Player player) {
            return getSpeedFromSun(player);
        }
        return MAX_SPEED_BASE;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult result = super.interact(player, hand);
        return result.consumesAction() ? result : InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    protected Item getDropItem() {
        return ModItems.SPEEDY_MINECART.get();
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.RIDEABLE;
    }
}
