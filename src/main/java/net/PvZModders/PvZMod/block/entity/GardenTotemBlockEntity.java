package net.PvZModders.PvZMod.block.entity;

import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.progression.GardenDefinition;
import net.PvZModders.PvZMod.progression.GardenDefinitions;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.waves.GardenWaveDefinition;
import net.PvZModders.PvZMod.progression.waves.GardenWaveProgress;
import net.PvZModders.PvZMod.progression.waves.OriginalGardenWaves;
import net.PvZModders.PvZMod.progression.waves.WaveReward;
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
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.PvZModders.PvZMod.menu.GardenTotemMenu;

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
    private String gardenName = "Original Garden";
    private String biomeName = "unknown";
    private final GardenWaveProgress waveProgress = new GardenWaveProgress();

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
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) -> new GardenTotemMenu(containerId, inventory, waveProgress.currentWave(), waveProgress.waveActive()),
                Component.literal(gardenName + " Totem").withStyle(ChatFormatting.GREEN)
        ));
    }

    public void startTotemDefense(ServerPlayer player) {
        if (waveProgress.waveActive()) {
            player.displayClientMessage(Component.literal("Totem defense already active").withStyle(ChatFormatting.YELLOW), true);
            return;
        }

        if (waveProgress.currentWave() == 1) {
            grantStarterPlants(player);
        }

        waveProgress.startWave();
        spawnWavePlaceholder(player.serverLevel(), waveProgress.currentWave());
        setChanged();
    }

    public void completeCurrentWave(ServerPlayer player) {
        if (!waveProgress.waveActive()) {
            return;
        }

        int completedWave = waveProgress.currentWave();
        waveProgress.completeCurrentWave();
        grantMilestoneRewards(player, completedWave);
        setChanged();
    }

    private void grantStarterPlants(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Tutorial unlocks: Sunflower and Peashooter").withStyle(ChatFormatting.GREEN));
    }

    private void spawnWavePlaceholder(ServerLevel level, int wave) {
        // TODO: Spawn and balance zombies for this wave around the Totem.
    }

    private void grantMilestoneRewards(ServerPlayer player, int wave) {
        GardenWaveDefinition definition = OriginalGardenWaves.get(wave);
        if (definition.rewards().isEmpty() || waveProgress.isRewardClaimed(wave)) {
            return;
        }

        waveProgress.markRewardClaimed(wave);
        for (WaveReward reward : definition.rewards()) {
            player.sendSystemMessage(Component.literal("Reward unlocked: " + reward.displayName()).withStyle(ChatFormatting.GOLD));
        }
        // TODO: Apply plant unlocks, item unlocks, upgrades, and completion flags to real player/garden progression.
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
        if (tag.contains("WaveProgress")) {
            waveProgress.load(tag.getCompound("WaveProgress"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Initialized", initialized);
        tag.putString("GardenName", gardenName);
        tag.putString("BiomeName", biomeName);
        tag.putDouble("TotemY", totemY);
        tag.putDouble("SinkingPlotterY", sinkingPlotterY);
        CompoundTag waveTag = new CompoundTag();
        waveProgress.save(waveTag);
        tag.put("WaveProgress", waveTag);
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
