package net.PvZModders.PvZMod.block.entity;

import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.progression.GardenDefinition;
import net.PvZModders.PvZMod.progression.GardenDefinitions;
import net.PvZModders.PvZMod.progression.GardenId;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.UUID;

public class GardenTotemBlockEntity extends BlockEntity {
    private static final double TOTEM_RISE_SPEED = 0.08D;
    private static final double PLOTTER_SINK_SPEED = 0.08D;

    private UUID totemDisplayId;
    private UUID sinkingPlotterDisplayId;
    private double totemY;
    private double sinkingPlotterY;
    private boolean initialized;
    private String gardenName = "Initial Garden";
    private String biomeName = "unknown";

    public GardenTotemBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GARDEN_TOTEM_BE.get(), pos, state);
        this.totemY = pos.getY() - 1.0D;
        this.sinkingPlotterY = pos.getY();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GardenTotemBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        be.ensureInitialized(serverLevel, pos);
        be.tickSinkingPlotter(serverLevel, pos);
        be.tickGardenTotem(serverLevel, pos);
    }

    public void initializeFromPlotter(ServerLevel level, BlockPos pos) {
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
        GardenDefinition garden = biomeKey
                .flatMap(GardenDefinitions::forBiome)
                .orElse(GardenDefinitions.get(GardenId.INITIAL_PLAINS));
        this.gardenName = garden.displayName();
        this.biomeName = biomeKey.map(key -> key.location().toString()).orElse("unknown");
        this.initialized = true;
        this.totemY = pos.getY() - 1.0D;
        this.sinkingPlotterY = pos.getY();
        spawnSinkingPlotter(level, pos);
        spawnGardenTotem(level, pos);
        setChanged();
    }

    public void openGardenMenu(ServerPlayer player) {
        SimpleContainer container = new SimpleContainer(27) {
            @Override
            public boolean canPlaceItem(int index, ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack removeItem(int index, int count) {
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeItemNoUpdate(int index) {
                return ItemStack.EMPTY;
            }
        };
        container.setItem(10, namedItem(Items.EXPERIENCE_BOTTLE, "Progress", "Linear garden progress placeholder"));
        container.setItem(13, namedItem(Items.ENDER_PEARL, "Portal", gardenName + " - current garden"));
        container.setItem(16, namedItem(Items.GRASS_BLOCK, "Planter", "Plant growth locations placeholder"));

        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) -> ChestMenu.threeRows(containerId, inventory, container),
                Component.literal(gardenName + " Totem").withStyle(ChatFormatting.GREEN)
        ));
    }

    private ItemStack namedItem(net.minecraft.world.item.Item item, String name, String description) {
        ItemStack stack = new ItemStack(item);
        stack.setHoverName(Component.literal(name).withStyle(ChatFormatting.GREEN));
        stack.getOrCreateTag().putString("GardenTotemDescription", description);
        return stack;
    }

    private void ensureInitialized(ServerLevel level, BlockPos pos) {
        if (!initialized) {
            initializeFromPlotter(level, pos);
        }
        if (totemDisplayId == null || level.getEntity(totemDisplayId) == null) {
            spawnGardenTotem(level, pos);
        }
    }

    private void spawnGardenTotem(ServerLevel level, BlockPos pos) {
        Display.BlockDisplay totem = EntityType.BLOCK_DISPLAY.create(level);
        if (totem == null) {
            return;
        }

        totem.load(createBlockDisplayTag(Blocks.TALL_GRASS.defaultBlockState(), 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F));
        totem.setPos(pos.getX(), totemY, pos.getZ());
        totem.setNoGravity(true);
        level.addFreshEntity(totem);
        totemDisplayId = totem.getUUID();
    }

    private void tickGardenTotem(ServerLevel level, BlockPos pos) {
        Entity entity = totemDisplayId == null ? null : level.getEntity(totemDisplayId);
        if (entity == null) {
            return;
        }

        if (totemY < pos.getY()) {
            totemY = Math.min(pos.getY(), totemY + TOTEM_RISE_SPEED);
            entity.setPos(pos.getX(), totemY, pos.getZ());
            setChanged();
        }
    }

    private void spawnSinkingPlotter(ServerLevel level, BlockPos pos) {
        Display.BlockDisplay plotter = EntityType.BLOCK_DISPLAY.create(level);
        if (plotter == null) {
            return;
        }

        plotter.load(createBlockDisplayTag(ModBlocks.GARDEN_PLOTTER.get().defaultBlockState(), 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
        plotter.setPos(pos.getX(), sinkingPlotterY, pos.getZ());
        plotter.setNoGravity(true);
        level.addFreshEntity(plotter);
        sinkingPlotterDisplayId = plotter.getUUID();
    }

    private void tickSinkingPlotter(ServerLevel level, BlockPos pos) {
        if (sinkingPlotterDisplayId == null) {
            return;
        }

        Entity entity = level.getEntity(sinkingPlotterDisplayId);
        if (entity == null) {
            sinkingPlotterDisplayId = null;
            return;
        }

        sinkingPlotterY -= PLOTTER_SINK_SPEED;
        if (sinkingPlotterY <= pos.getY() - 1.0D) {
            entity.discard();
            sinkingPlotterDisplayId = null;
        } else {
            entity.setPos(pos.getX(), sinkingPlotterY, pos.getZ());
        }
        setChanged();
    }

    private CompoundTag createBlockDisplayTag(BlockState state, float translateX, float translateY, float translateZ, float scaleX, float scaleY, float scaleZ) {
        CompoundTag tag = new CompoundTag();
        tag.put("block_state", NbtUtils.writeBlockState(state));

        CompoundTag transformation = new CompoundTag();
        transformation.put("translation", floatList(translateX, translateY, translateZ));
        transformation.put("scale", floatList(scaleX, scaleY, scaleZ));
        transformation.put("left_rotation", floatList(0.0F, 0.0F, 0.0F, 1.0F));
        transformation.put("right_rotation", floatList(0.0F, 0.0F, 0.0F, 1.0F));
        tag.put("transformation", transformation);

        tag.putFloat("view_range", 32.0F);
        tag.putFloat("shadow_radius", 0.0F);
        tag.putFloat("shadow_strength", 0.0F);
        return tag;
    }

    private ListTag floatList(float... values) {
        ListTag list = new ListTag();
        for (float value : values) {
            list.add(FloatTag.valueOf(value));
        }
        return list;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        initialized = tag.getBoolean("Initialized");
        gardenName = tag.getString("GardenName");
        biomeName = tag.getString("BiomeName");
        totemY = tag.contains("TotemY") ? tag.getDouble("TotemY") : worldPosition.getY();
        sinkingPlotterY = tag.contains("SinkingPlotterY") ? tag.getDouble("SinkingPlotterY") : worldPosition.getY();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Initialized", initialized);
        tag.putString("GardenName", gardenName);
        tag.putString("BiomeName", biomeName);
        tag.putDouble("TotemY", totemY);
        tag.putDouble("SinkingPlotterY", sinkingPlotterY);
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            discardDisplay(serverLevel, totemDisplayId);
            discardDisplay(serverLevel, sinkingPlotterDisplayId);
        }
        super.setRemoved();
    }

    private void discardDisplay(ServerLevel level, UUID displayId) {
        if (displayId == null) {
            return;
        }

        Entity entity = level.getEntity(displayId);
        if (entity != null) {
            entity.discard();
        }
    }
}
