package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FlyingPlaneEntity extends Minecart {
    public static final double BASE_SPEED = 0.18D;
    public static final double MAX_SPEED_BASE = 0.85D;
    public static final double MAX_SPEED_UPGRADED = 1.15D;
    public static final int SUN_FOR_MAX_SPEED = 300;

    public FlyingPlaneEntity(EntityType<? extends Minecart> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public FlyingPlaneEntity(Level level, double x, double y, double z) {
        super(ModEntities.FLYING_PLANE.get(), level);
        setNoGravity(true);
        setPos(x, y, z);
        xo = x;
        yo = y;
        zo = z;
    }

    @Override
    public void tick() {
        setNoGravity(true);
        super.tick();
        setNoGravity(true);

        Entity passenger = getFirstPassenger();
        if (!(passenger instanceof Player player)) {
            setDeltaMovement(getDeltaMovement().scale(0.96D));
            return;
        }

        Vec3 look = player.getLookAngle().normalize();
        Vec3 side = new Vec3(-look.z, 0.0D, look.x);
        if (side.lengthSqr() > 1.0E-4D) {
            side = side.normalize();
        }

        double speed = getSpeedFromSun(player);
        double throttle = throttleFor(player);
        Vec3 desired = look.scale(speed * throttle).add(side.scale(speed * 0.35D * player.xxa));
        if (player.isShiftKeyDown()) {
            desired = desired.add(0.0D, -speed * 0.35D, 0.0D);
        }

        setYRot(player.getYRot());
        setXRot(player.getXRot());
        setDeltaMovement(desired);
        move(MoverType.SELF, getDeltaMovement());
    }

    public static double getSpeedFromSun(Player player) {
        int sunCap = Math.max(1, SunManager.getSunCap(player));
        double maxSpeed = sunCap > SunManager.DEFAULT_SUN_CAP ? MAX_SPEED_UPGRADED : MAX_SPEED_BASE;
        double t = Math.min(1.0D, SunManager.getSun(player) / (double) SUN_FOR_MAX_SPEED);
        return BASE_SPEED + (maxSpeed - BASE_SPEED) * t;
    }

    private static double throttleFor(Player player) {
        if (player.zza > 0.05F) {
            return 1.0D;
        }
        if (player.zza < -0.05F) {
            return 0.22D;
        }
        return 0.48D;
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
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.FLYING_PLANE.get();
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.RIDEABLE;
    }
}
