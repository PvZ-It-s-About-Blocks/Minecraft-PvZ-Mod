package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WildWestMinecartEntity extends Minecart {
    public static final String WILD_WEST_CART_TAG = "PvZWildWestMinecart";
    private static final String ORIGIN_X_TAG = "PvZRailOriginX";
    private static final String ORIGIN_Y_TAG = "PvZRailOriginY";
    private static final String ORIGIN_Z_TAG = "PvZRailOriginZ";
    private static final String AXIS_TAG = "PvZRailAxis";
    private static final String FIXED_OFFSET_TAG = "PvZRailFixedOffset";
    private static final String CURRENT_OFFSET_TAG = "PvZRailCurrentOffset";
    private static final String MIN_OFFSET_TAG = "PvZRailMinOffset";
    private static final String MAX_OFFSET_TAG = "PvZRailMaxOffset";
    private static final String RAIL_INDEX_TAG = "PvZRailIndex";

    public WildWestMinecartEntity(EntityType<? extends Minecart> entityType, Level level) {
        super(entityType, level);
        blocksBuilding = false;
        setInvulnerable(true);
        setNoGravity(true);
    }

    public WildWestMinecartEntity(Level level, double x, double y, double z) {
        this(ModEntities.WILD_WEST_MINECART.get(), level);
        setPos(x, y, z);
        xo = x;
        yo = y;
        zo = z;
    }

    public static WildWestMinecartEntity create(Level level, BlockPos origin, Direction.Axis axis, int railIndex, int fixedOffset, int currentOffset, int minOffset, int maxOffset) {
        Vec3 pos = positionFor(origin, axis, fixedOffset, currentOffset);
        WildWestMinecartEntity cart = new WildWestMinecartEntity(level, pos.x, pos.y, pos.z);
        cart.configure(origin, axis, railIndex, fixedOffset, currentOffset, minOffset, maxOffset);
        return cart;
    }

    public void configure(BlockPos origin, Direction.Axis axis, int railIndex, int fixedOffset, int currentOffset, int minOffset, int maxOffset) {
        CompoundTag tag = getPersistentData();
        tag.putBoolean(WILD_WEST_CART_TAG, true);
        tag.putInt(ORIGIN_X_TAG, origin.getX());
        tag.putInt(ORIGIN_Y_TAG, origin.getY());
        tag.putInt(ORIGIN_Z_TAG, origin.getZ());
        tag.putString(AXIS_TAG, axis.getName());
        tag.putInt(RAIL_INDEX_TAG, railIndex);
        tag.putInt(FIXED_OFFSET_TAG, fixedOffset);
        tag.putInt(CURRENT_OFFSET_TAG, currentOffset);
        tag.putInt(MIN_OFFSET_TAG, minOffset);
        tag.putInt(MAX_OFFSET_TAG, maxOffset);
        setPos(positionFor(origin, axis, fixedOffset, currentOffset));
    }

    public boolean belongsTo(BlockPos origin) {
        CompoundTag tag = getPersistentData();
        return tag.getBoolean(WILD_WEST_CART_TAG)
                && tag.getInt(ORIGIN_X_TAG) == origin.getX()
                && tag.getInt(ORIGIN_Y_TAG) == origin.getY()
                && tag.getInt(ORIGIN_Z_TAG) == origin.getZ();
    }

    public int railIndex() {
        return getPersistentData().getInt(RAIL_INDEX_TAG);
    }

    public Vec3 plantPosition() {
        return position().add(0.0D, 0.45D, 0.0D);
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.35D;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (!level().isClientSide) {
            moveOneBlock(player);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    public boolean moveOneBlock(Player player) {
        Direction.Axis axis = railAxis();
        Vec3 look = player.getLookAngle();
        int direction = axis == Direction.Axis.X
                ? (look.x >= 0.0D ? 1 : -1)
                : (look.z >= 0.0D ? 1 : -1);

        CompoundTag tag = getPersistentData();
        int current = tag.getInt(CURRENT_OFFSET_TAG);
        int next = current + direction;
        if (next < tag.getInt(MIN_OFFSET_TAG) || next > tag.getInt(MAX_OFFSET_TAG)) {
            return false;
        }

        tag.putInt(CURRENT_OFFSET_TAG, next);
        BlockPos origin = origin();
        Vec3 target = positionFor(origin, axis, tag.getInt(FIXED_OFFSET_TAG), next);
        setPos(target);
        setDeltaMovement(Vec3.ZERO);
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        if (type == MoverType.SELF) {
            super.move(type, movement);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    protected Item getDropItem() {
        return Items.MINECART;
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.RIDEABLE;
    }

    private BlockPos origin() {
        CompoundTag tag = getPersistentData();
        return new BlockPos(tag.getInt(ORIGIN_X_TAG), tag.getInt(ORIGIN_Y_TAG), tag.getInt(ORIGIN_Z_TAG));
    }

    private Direction.Axis railAxis() {
        return "x".equals(getPersistentData().getString(AXIS_TAG)) ? Direction.Axis.X : Direction.Axis.Z;
    }

    private static Vec3 positionFor(BlockPos origin, Direction.Axis axis, int fixedOffset, int currentOffset) {
        double y = origin.getY() + 0.05D;
        if (axis == Direction.Axis.X) {
            return new Vec3(origin.getX() + currentOffset + 0.5D, y, origin.getZ() + fixedOffset + 0.5D);
        }
        return new Vec3(origin.getX() + fixedOffset + 0.5D, y, origin.getZ() + currentOffset + 0.5D);
    }
}
