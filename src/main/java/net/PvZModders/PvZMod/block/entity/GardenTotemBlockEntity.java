package net.PvZModders.PvZMod.block.entity;

import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.custom.GardenTotemBlock;
import net.PvZModders.PvZMod.progression.GardenDefinition;
import net.PvZModders.PvZMod.progression.GardenDefinitions;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.waves.GardenWaveDefinition;
import net.PvZModders.PvZMod.progression.waves.GardenWaveProgress;
import net.PvZModders.PvZMod.progression.waves.OriginalGardenWaves;
import net.PvZModders.PvZMod.progression.waves.WaveReward;
import net.PvZModders.PvZMod.progression.waves.WaveSpawnDirection;
import net.PvZModders.PvZMod.progression.waves.WaveSpawnGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.PvZModders.PvZMod.menu.GardenTotemMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class GardenTotemBlockEntity extends BlockEntity {
    private static final double TOTEM_RISE_SPEED = 0.08D;
    private static final double PLOTTER_SINK_SPEED = 0.08D;
    private static final int GARDEN_RADIUS = 7;
    private static final int TOTEM_MAX_HEALTH = 100;
    private static final int ZOMBIE_TOTEM_DAMAGE = 4;

    private UUID totemDisplayId;
    private UUID sinkingPlotterDisplayId;
    private UUID healthBarDisplayId;
    private double totemY;
    private double sinkingPlotterY;
    private boolean initialized;
    private String gardenName = "Original Garden";
    private String biomeName = "unknown";
    private int totemHealth = TOTEM_MAX_HEALTH;
    private final GardenWaveProgress waveProgress = new GardenWaveProgress();
    private final Set<UUID> activeWaveEntityIds = new HashSet<>();

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
        be.syncHealthBar(serverLevel, pos);
        be.tickActiveWaveZombies(serverLevel);
        be.tickWaveObjective(serverLevel);
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
        placeTotemColumn(level, pos);
        spawnSinkingPlotter(level, pos);
        syncHealthBar(level, pos);
        setChanged();
    }

    public void openGardenMenu(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) -> new GardenTotemMenu(containerId, inventory, this),
                Component.literal(gardenName + " Totem").withStyle(ChatFormatting.GREEN)
        ));
    }

    public int getCurrentWave() {
        return waveProgress.currentWave();
    }

    public boolean isWaveActive() {
        return waveProgress.waveActive();
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
        totemHealth = TOTEM_MAX_HEALTH;
        List<WaveSpawnDirection> directions = spawnWave(player.serverLevel(), OriginalGardenWaves.get(waveProgress.currentWave()));
        showWaveDirectionTitle(player, directions);
        player.displayClientMessage(Component.literal("Spawned " + activeWaveEntityIds.size() + " zombies on the garden border").withStyle(ChatFormatting.GRAY), true);
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

    public void devClearCurrentWave(ServerPlayer player) {
        if (!waveProgress.waveActive()) {
            if (waveProgress.currentWave() == 1) {
                grantStarterPlants(player);
            }
            waveProgress.startWave();
        }

        if (level instanceof ServerLevel serverLevel) {
            discardActiveWaveEntities(serverLevel);
        }
        completeCurrentWave(player);
        player.displayClientMessage(Component.literal("Dev cleared current wave").withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    private void grantStarterPlants(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Tutorial unlocks: Sunflower and Peashooter").withStyle(ChatFormatting.GREEN));
    }

    private List<WaveSpawnDirection> spawnWave(ServerLevel level, GardenWaveDefinition definition) {
        activeWaveEntityIds.clear();
        List<WaveSpawnDirection> waveDirections = new ArrayList<>();

        for (WaveSpawnGroup group : definition.spawnGroups()) {
            List<WaveSpawnDirection> directions = resolveDirections(level, group);
            waveDirections.addAll(directions);
            spawnGroup(level, group, directions);
        }

        return waveDirections.stream().distinct().toList();
    }

    private List<WaveSpawnDirection> resolveDirections(ServerLevel level, WaveSpawnGroup group) {
        if (!group.usesRandomDirections()) {
            return group.fixedDirections();
        }

        List<WaveSpawnDirection> directions = new ArrayList<>(List.of(WaveSpawnDirection.values()));
        for (int i = directions.size() - 1; i > 0; i--) {
            Collections.swap(directions, i, level.random.nextInt(i + 1));
        }
        return directions.subList(0, Math.max(1, Math.min(group.directionCount(), directions.size())));
    }

    private void spawnGroup(ServerLevel level, WaveSpawnGroup group, List<WaveSpawnDirection> directions) {
        Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(new ResourceLocation(group.entityTypeId()));
        if (entityType.isEmpty()) {
            return;
        }

        int perDirection = Math.max(1, (int) Math.ceil(group.count() / (double) directions.size()));
        int spawned = 0;
        for (WaveSpawnDirection direction : directions) {
            for (int i = 0; i < perDirection && spawned < group.count(); i++) {
                BlockPos spawnPos = findSpawnPos(level, direction, spawned, group.count());
                Entity entity = spawnWaveEntity(level, entityType.get(), spawnPos);
                if (entity == null) {
                    continue;
                }

                if (entity instanceof Mob mob) {
                    mob.setPersistenceRequired();
                    moveMobTowardTotem(mob);
                }
                activeWaveEntityIds.add(entity.getUUID());
                spawned++;
            }
        }
    }

    private Entity spawnWaveEntity(ServerLevel level, EntityType<?> entityType, BlockPos spawnPos) {
        Entity entity = entityType.spawn(level, spawnPos, MobSpawnType.EVENT);
        if (entity != null) {
            return entity;
        }

        entity = entityType.create(level);
        if (entity == null) {
            return null;
        }

        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
        level.addFreshEntity(entity);
        return entity;
    }

    private BlockPos findSpawnPos(ServerLevel level, WaveSpawnDirection direction, int index, int totalCount) {
        int sideOffset = spreadBorderOffset(index, totalCount);
        BlockPos base = direction.borderPosition(worldPosition, GARDEN_RADIUS, sideOffset);
        BlockPos spawnPos = new BlockPos(base.getX(), worldPosition.getY(), base.getZ());

        if (isSpawnClear(level, spawnPos)) {
            return spawnPos;
        }

        return level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base);
    }

    private int spreadBorderOffset(int index, int totalCount) {
        if (totalCount <= 1) {
            return 0;
        }

        double step = (GARDEN_RADIUS * 2.0D) / Math.max(1, totalCount - 1);
        return (int) Math.round(-GARDEN_RADIUS + (index * step));
    }

    private boolean isSpawnClear(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
    }

    private void showWaveDirectionTitle(ServerPlayer player, List<WaveSpawnDirection> directions) {
        String directionText = formatDirections(directions);
        Component message = Component.literal("The Zombies are coming from the " + directionText + "!").withStyle(ChatFormatting.LIGHT_PURPLE);
        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 50, 10));
        player.connection.send(new ClientboundSetTitleTextPacket(Component.empty()));
        player.connection.send(new ClientboundSetSubtitleTextPacket(message));
    }

    private String formatDirections(List<WaveSpawnDirection> directions) {
        if (directions.isEmpty()) {
            return "Unknown";
        }
        if (directions.size() == 1) {
            return directions.get(0).displayName();
        }

        List<String> names = directions.stream().map(WaveSpawnDirection::displayName).toList();
        return String.join(", ", names.subList(0, names.size() - 1)) + " and " + names.get(names.size() - 1);
    }

    private void tickWaveObjective(ServerLevel level) {
        if (!waveProgress.waveActive()) {
            return;
        }

        activeWaveEntityIds.removeIf(entityId -> {
            Entity entity = level.getEntity(entityId);
            return entity == null || !entity.isAlive();
        });

        if (activeWaveEntityIds.isEmpty()) {
            completeCurrentWaveForNearbyPlayers(level);
        }
    }

    private void tickActiveWaveZombies(ServerLevel level) {
        if (!waveProgress.waveActive()) {
            return;
        }

        for (UUID entityId : List.copyOf(activeWaveEntityIds)) {
            Entity entity = level.getEntity(entityId);
            if (!(entity instanceof Mob mob) || !mob.isAlive()) {
                continue;
            }

            if (isMobInTotemAttackRange(mob)) {
                mob.getNavigation().stop();
                mob.getLookControl().setLookAt(worldPosition.getX() + 0.5D, worldPosition.getY() + 1.5D, worldPosition.getZ() + 0.5D);
                if ((level.getGameTime() + entity.getId()) % 20 == 0) {
                    mob.swing(InteractionHand.MAIN_HAND);
                    damageTotem(level, ZOMBIE_TOTEM_DAMAGE);
                }
                continue;
            }

            if (mob.getTarget() == null && (level.getGameTime() + entity.getId()) % 10 == 0) {
                moveMobTowardTotem(mob);
            }
        }
    }

    private void moveMobTowardTotem(Mob mob) {
        BlockPos approachPos = getTotemApproachPos(mob);
        mob.getNavigation().moveTo(approachPos.getX() + 0.5D, approachPos.getY(), approachPos.getZ() + 0.5D, 1.1D);
    }

    private BlockPos getTotemApproachPos(Mob mob) {
        double dx = mob.getX() - (worldPosition.getX() + 0.5D);
        double dz = mob.getZ() - (worldPosition.getZ() + 0.5D);
        if (Math.abs(dx) > Math.abs(dz)) {
            return worldPosition.offset(dx >= 0.0D ? 1 : -1, 0, 0);
        }
        return worldPosition.offset(0, 0, dz >= 0.0D ? 1 : -1);
    }

    private boolean isMobInTotemAttackRange(Mob mob) {
        double dx = Math.abs(mob.getX() - (worldPosition.getX() + 0.5D));
        double dz = Math.abs(mob.getZ() - (worldPosition.getZ() + 0.5D));
        return dx <= 1.85D
                && dz <= 1.85D
                && mob.getY() <= worldPosition.getY() + 3.25D
                && mob.getY() + mob.getBbHeight() >= worldPosition.getY() - 0.25D;
    }

    private void damageTotem(ServerLevel level, int amount) {
        totemHealth = Math.max(0, totemHealth - amount);
        if (totemHealth > 0) {
            setChanged();
            return;
        }

        waveProgress.failCurrentWave();
        discardActiveWaveEntities(level);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 4096.0D) {
                player.displayClientMessage(Component.literal("The Totem was overwhelmed. Wave failed.").withStyle(ChatFormatting.RED), false);
            }
        }
        setChanged();
    }

    private void completeCurrentWaveForNearbyPlayers(ServerLevel level) {
        ServerPlayer nearestPlayer = null;
        double nearestDistance = Double.MAX_VALUE;
        for (ServerPlayer player : level.players()) {
            double distance = player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D);
            if (distance < nearestDistance && distance <= 4096.0D) {
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }

        if (nearestPlayer != null) {
            completeCurrentWave(nearestPlayer);
        } else {
            int completedWave = waveProgress.currentWave();
            waveProgress.completeCurrentWave();
            waveProgress.markRewardClaimed(completedWave);
            setChanged();
        }
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
        placeTotemColumn(level, pos);
        syncHealthBar(level, pos);
    }

    private void placeTotemColumn(ServerLevel level, BlockPos pos) {
        for (int part = 0; part < 3; part++) {
            BlockPos partPos = pos.above(part);
            BlockState expected = ModBlocks.GARDEN_TOTEM.get().defaultBlockState().setValue(GardenTotemBlock.PART, part);
            if (!level.getBlockState(partPos).is(ModBlocks.GARDEN_TOTEM.get())
                    || level.getBlockState(partPos).getValue(GardenTotemBlock.PART) != part) {
                level.setBlock(partPos, expected, 3);
            }
        }
    }

    private void syncHealthBar(ServerLevel level, BlockPos pos) {
        if (level.getGameTime() % 5 != 0 && healthBarDisplayId != null && level.getEntity(healthBarDisplayId) != null) {
            return;
        }

        Entity entity = healthBarDisplayId == null ? null : level.getEntity(healthBarDisplayId);
        Display.TextDisplay healthBar = entity instanceof Display.TextDisplay textDisplay ? textDisplay : null;
        if (healthBar == null) {
            healthBar = EntityType.TEXT_DISPLAY.create(level);
            if (healthBar == null) {
                return;
            }
            healthBar.setNoGravity(true);
            level.addFreshEntity(healthBar);
            healthBarDisplayId = healthBar.getUUID();
        }

        healthBar.load(createHealthBarDisplayTag());
        healthBar.setPos(pos.getX() + 0.5D, pos.getY() + 3.4D, pos.getZ() + 0.5D);
    }

    private CompoundTag createHealthBarDisplayTag() {
        CompoundTag tag = new CompoundTag();
        Component text = Component.literal("Totem " + healthBarText() + " " + totemHealth + "/" + TOTEM_MAX_HEALTH)
                .withStyle(style -> style.withColor(TextColor.fromRgb(healthBarColor())));
        tag.putString("text", Component.Serializer.toJson(text));
        tag.putInt("line_width", 260);
        tag.putByte("text_opacity", (byte) 255);
        tag.putInt("background", 0x66000000);
        tag.putBoolean("see_through", true);
        tag.putString("billboard", "center");
        tag.putFloat("view_range", 48.0F);
        tag.putFloat("shadow_radius", 0.0F);
        tag.putFloat("shadow_strength", 0.0F);
        return tag;
    }

    private String healthBarText() {
        int filled = Math.round((totemHealth / (float) TOTEM_MAX_HEALTH) * 20.0F);
        return "[" + "|".repeat(Math.max(0, filled)) + ".".repeat(Math.max(0, 20 - filled)) + "]";
    }

    private int healthBarColor() {
        if (totemHealth > 60) {
            return 0x44FF44;
        }
        if (totemHealth > 25) {
            return 0xFFD44A;
        }
        return 0xFF4444;
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
        totemHealth = tag.contains("TotemHealth") ? tag.getInt("TotemHealth") : TOTEM_MAX_HEALTH;
        totemY = tag.contains("TotemY") ? tag.getDouble("TotemY") : worldPosition.getY();
        sinkingPlotterY = tag.contains("SinkingPlotterY") ? tag.getDouble("SinkingPlotterY") : worldPosition.getY();
        if (tag.contains("WaveProgress")) {
            waveProgress.load(tag.getCompound("WaveProgress"));
        }
        activeWaveEntityIds.clear();
        ListTag activeEntities = tag.getList("ActiveWaveEntities", net.minecraft.nbt.Tag.TAG_STRING);
        for (int i = 0; i < activeEntities.size(); i++) {
            activeWaveEntityIds.add(UUID.fromString(activeEntities.getString(i)));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Initialized", initialized);
        tag.putString("GardenName", gardenName);
        tag.putString("BiomeName", biomeName);
        tag.putInt("TotemHealth", totemHealth);
        tag.putDouble("TotemY", totemY);
        tag.putDouble("SinkingPlotterY", sinkingPlotterY);
        CompoundTag waveTag = new CompoundTag();
        waveProgress.save(waveTag);
        tag.put("WaveProgress", waveTag);
        ListTag activeEntities = new ListTag();
        for (UUID entityId : activeWaveEntityIds) {
            activeEntities.add(net.minecraft.nbt.StringTag.valueOf(entityId.toString()));
        }
        tag.put("ActiveWaveEntities", activeEntities);
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            discardDisplay(serverLevel, totemDisplayId);
            discardDisplay(serverLevel, sinkingPlotterDisplayId);
            discardDisplay(serverLevel, healthBarDisplayId);
        }
        super.setRemoved();
    }

    private void discardActiveWaveEntities(ServerLevel level) {
        for (UUID entityId : activeWaveEntityIds) {
            Entity entity = level.getEntity(entityId);
            if (entity != null) {
                entity.discard();
            }
        }
        activeWaveEntityIds.clear();
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
