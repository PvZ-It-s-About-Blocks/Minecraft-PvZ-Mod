package net.PvZModders.PvZMod.block.entity;

import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.custom.GardenTotemBlock;
import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.GardenDefinition;
import net.PvZModders.PvZMod.progression.GardenDefinitions;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.GardenPortalOption;
import net.PvZModders.PvZMod.progression.GardenPortalSavedData;
import net.PvZModders.PvZMod.progression.GardenProgressSavedData;
import net.PvZModders.PvZMod.progression.plants.GardenPlantDefinition;
import net.PvZModders.PvZMod.progression.plants.GardenPlantProductionSavedData;
import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.PvZModders.PvZMod.progression.seed.SeedStorage;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.PvZModders.PvZMod.network.ModMessages;
import net.PvZModders.PvZMod.progression.waves.GardenWaveDefinition;
import net.PvZModders.PvZMod.progression.waves.GardenWaveProgress;
import net.PvZModders.PvZMod.progression.waves.GardenWaves;
import net.PvZModders.PvZMod.progression.waves.OriginalGardenWaves;
import net.PvZModders.PvZMod.progression.waves.WaveReward;
import net.PvZModders.PvZMod.progression.waves.WaveSpawnDirection;
import net.PvZModders.PvZMod.progression.waves.WaveSpawnGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
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
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.RelativeMovement;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
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
    private static final int DEFAULT_WAVE_DURATION_TICKS = 20 * 60;
    private static final int FIRST_SPAWN_DELAY_TICKS = 20;
    private static final int KILL_ACCELERATED_MIN_DELAY_TICKS = 20 * 3;
    private static final int KILL_ACCELERATED_RANDOM_DELAY_TICKS = 20 * 2;
    private static final double FINAL_PUSH_PROGRESS = 0.78D;
    private static final String SEED_HOLDER_GRANTED_TAG = "PvZSeedHolderGranted";
    private static final ResourceLocation SEED_HOLDER_RECIPE = new ResourceLocation(PvZ2Mod.MOD_ID, "seed_holder");

    private UUID totemDisplayId;
    private UUID sinkingPlotterDisplayId;
    private UUID healthBarDisplayId;
    private double totemY;
    private double sinkingPlotterY;
    private boolean initialized;
    private GardenId gardenId = GardenId.INITIAL_PLAINS;
    private String gardenName = "Original Garden";
    private String biomeName = "unknown";
    private int totemHealth = TOTEM_MAX_HEALTH;
    private final GardenWaveProgress legacyWaveProgress = new GardenWaveProgress();
    private final Set<UUID> activeWaveEntityIds = new HashSet<>();
    private final List<WaveSpawnDirection> activeWaveDirections = new ArrayList<>();
    private final ServerBossEvent waveBossBar = new ServerBossEvent(
            Component.literal("Wave Progress"),
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.PROGRESS
    );
    private long activeWaveStartTick = -1L;
    private long activeWaveNextSpawnTick = -1L;
    private int activeWaveTotalZombies;
    private int activeWaveSpawned;
    private boolean activeWaveFinalPushStarted;

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
        be.tickGardenPlantProduction(serverLevel);
        be.tickSinkingPlotter(serverLevel, pos);
        be.syncHealthBar(serverLevel, pos);
        be.tickWaveSpawnSchedule(serverLevel);
        be.updateWaveBossBar(serverLevel);
        be.tickActiveWaveZombies(serverLevel);
        be.tickWaveObjective(serverLevel);
    }

    public void initializeFromPlotter(ServerLevel level, BlockPos pos) {
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
        GardenDefinition garden = biomeKey
                .flatMap(GardenDefinitions::forBiome)
                .orElse(GardenDefinitions.get(GardenId.INITIAL_PLAINS));
        initializeAsGarden(level, pos, garden.id(), biomeKey.map(key -> key.location().toString()).orElse("unknown"));
    }

    public void initializeFromPlotter(ServerLevel level, BlockPos pos, GardenId explicitGardenId) {
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
        initializeAsGarden(level, pos, explicitGardenId, biomeKey.map(key -> key.location().toString()).orElse("unknown"));
    }

    public void initializeAsGarden(ServerLevel level, BlockPos pos, GardenId gardenId, String biomeName) {
        GardenDefinition garden = GardenDefinitions.get(gardenId);
        this.gardenId = garden.id();
        this.gardenName = garden.displayName();
        this.biomeName = biomeName;
        this.initialized = true;
        this.totemY = pos.getY() - 1.0D;
        this.sinkingPlotterY = pos.getY();
        placeTotemColumn(level, pos);
        registerPortal(level, pos);
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
        return getWaveProgress().currentWave();
    }

    public boolean isWaveActive() {
        return getWaveProgress().waveActive();
    }

    public int getPortalDiscoveryMask() {
        if (level instanceof ServerLevel serverLevel) {
            return GardenPortalSavedData.get(serverLevel).discoveredMask();
        }
        return GardenPortalOption.values()[GardenPortalOption.indexOf(gardenId)].mask();
    }

    public int getCurrentPortalIndex() {
        return GardenPortalOption.indexOf(gardenId);
    }

    public int getGardenPortalIndex() {
        return GardenPortalOption.indexOf(gardenId);
    }

    public int getGardenPlantCount(int plantIndex) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return 0;
        }
        List<GardenPlantDefinition> plants = gardenPlants();
        if (plantIndex < 0 || plantIndex >= plants.size()) {
            return 0;
        }
        return GardenPlantProductionSavedData.get(serverLevel).count(gardenId, plants.get(plantIndex).plantId());
    }

    public int getGardenPlantRemainingSeconds(int plantIndex) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return 0;
        }
        List<GardenPlantDefinition> plants = gardenPlants();
        if (plantIndex < 0 || plantIndex >= plants.size()) {
            return 0;
        }
        return GardenPlantProductionSavedData.get(serverLevel).remainingSeconds(serverLevel, gardenId, plants.get(plantIndex));
    }

    public boolean isGardenPlantUnlocked(int plantIndex) {
        List<GardenPlantDefinition> plants = gardenPlants();
        if (plantIndex < 0 || plantIndex >= plants.size()) {
            return false;
        }
        return plants.get(plantIndex).isUnlockedAtWave(getWaveProgress().currentWave());
    }

    public List<GardenPlantDefinition> getGardenPlants() {
        return gardenPlants();
    }

    public ItemStack removeGardenPlantPackets(int plantIndex, int amount) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return ItemStack.EMPTY;
        }

        List<GardenPlantDefinition> plants = gardenPlants();
        if (plantIndex < 0 || plantIndex >= plants.size()) {
            return ItemStack.EMPTY;
        }

        GardenPlantDefinition plant = plants.get(plantIndex);
        if (!plant.isUnlockedAtWave(getWaveProgress(serverLevel).currentWave())) {
            return ItemStack.EMPTY;
        }

        int taken = GardenPlantProductionSavedData.get(serverLevel).takePackets(gardenId, plant.plantId(), amount);
        if (taken <= 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(BuiltInRegistries.ITEM.get(plant.seedPacketId()), taken);
    }

    public void teleportToGarden(ServerPlayer player, int portalIndex) {
        Optional<GardenPortalOption> option = GardenPortalOption.byIndex(portalIndex);
        if (option.isEmpty()) {
            return;
        }

        if (option.get().gardenId() == gardenId) {
            player.displayClientMessage(Component.literal("Already in this garden").withStyle(ChatFormatting.YELLOW), true);
            return;
        }

        GardenPortalSavedData portalData = GardenPortalSavedData.get(player.serverLevel());
        Optional<GlobalPos> target = portalData.getPortal(option.get().gardenId());
        if (target.isEmpty()) {
            player.displayClientMessage(Component.literal("That garden has not been discovered yet").withStyle(ChatFormatting.GRAY), true);
            return;
        }

        ServerLevel targetLevel = player.server.getLevel(target.get().dimension());
        if (targetLevel == null || !targetLevel.getBlockState(target.get().pos()).is(ModBlocks.GARDEN_TOTEM.get())) {
            portalData.removePortal(option.get().gardenId(), target.get());
            player.displayClientMessage(Component.literal("That garden totem no longer exists").withStyle(ChatFormatting.RED), true);
            return;
        }

        BlockPos arrival = findTeleportArrival(targetLevel, target.get().pos());
        player.closeContainer();
        ModMessages.sendGardenTeleportOverlay(player);
        player.teleportTo(targetLevel, arrival.getX() + 0.5D, arrival.getY(), arrival.getZ() + 0.5D, Set.of(), player.getYRot(), player.getXRot());
    }

    public void startTotemDefense(ServerPlayer player) {
        GardenWaveProgress waveProgress = getWaveProgress();
        if (waveProgress.waveActive()) {
            player.displayClientMessage(Component.literal("Totem defense already active").withStyle(ChatFormatting.YELLOW), true);
            return;
        }

        if (waveProgress.currentWave() == 1) {
            grantStarterPlants(player);
            SunManager.unlockSunDrops(player.serverLevel(), player);
        }

        waveProgress.startWave();
        markWaveProgressDirty(player.serverLevel());
        totemHealth = TOTEM_MAX_HEALTH;
        List<WaveSpawnDirection> directions = prepareWaveSpawnSchedule(player.serverLevel(), waveDefinition(waveProgress.currentWave()));
        showWaveDirectionTitle(player, directions);
        player.displayClientMessage(Component.literal("Wave " + waveProgress.currentWave() + " started").withStyle(ChatFormatting.GRAY), true);
        setChanged();
    }

    public void withdrawGardenPlantPacket(ServerPlayer player, int plantIndex) {
        List<GardenPlantDefinition> plants = gardenPlants();
        if (plantIndex < 0 || plantIndex >= plants.size()) {
            return;
        }

        GardenPlantDefinition plant = plants.get(plantIndex);
        if (!plant.isUnlockedAtWave(getWaveProgress(player.serverLevel()).currentWave())) {
            player.displayClientMessage(Component.literal(plant.unlockHint()).withStyle(ChatFormatting.RED), true);
            return;
        }

        GardenPlantProductionSavedData production = GardenPlantProductionSavedData.get(player.serverLevel());
        if (production.count(gardenId, plant.plantId()) <= 0) {
            player.displayClientMessage(Component.literal("No " + plant.displayName() + " packets ready yet.").withStyle(ChatFormatting.YELLOW), true);
            return;
        }

        if (!production.takePacket(gardenId, plant.plantId())) {
            return;
        }

        if (!SeedStorage.addPlantPacketsToLoadout(player, plant.seedPacketId(), 1)) {
            production.addPacket(gardenId, plant.plantId());
            player.displayClientMessage(Component.literal("Your seed storage is full.").withStyle(ChatFormatting.RED), true);
            return;
        }

        player.displayClientMessage(Component.literal("Loaded " + plant.displayName() + " packet.").withStyle(ChatFormatting.GREEN), true);
    }

    public void completeCurrentWave(ServerPlayer player) {
        completeCurrentWave(player.serverLevel());
    }

    private void completeCurrentWave(ServerLevel level) {
        GardenWaveProgress waveProgress = getWaveProgress(level);
        if (!waveProgress.waveActive()) {
            return;
        }

        int completedWave = waveProgress.currentWave();
        waveProgress.completeCurrentWave();
        markWaveProgressDirty(level);
        clearWaveRuntimeState();
        totemHealth = TOTEM_MAX_HEALTH;
        grantMilestoneRewards(level, completedWave);
        setChanged();
    }

    public void devClearCurrentWave(ServerPlayer player) {
        GardenWaveProgress waveProgress = getWaveProgress();
        if (!waveProgress.waveActive()) {
            if (waveProgress.currentWave() == 1) {
                grantStarterPlants(player);
            }
            waveProgress.startWave();
            markWaveProgressDirty(player.serverLevel());
        }

        if (level instanceof ServerLevel serverLevel) {
            discardActiveWaveEntities(serverLevel);
        }
        completeCurrentWave(player);
        player.displayClientMessage(Component.literal("Dev cleared current wave").withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    private void grantStarterPlants(ServerPlayer player) {
        grantSeedHolder(player);
        unlockSeedHolderRecipe(player);
        player.sendSystemMessage(Component.literal("Tutorial unlocks: Sunflower and Peashooter").withStyle(ChatFormatting.GREEN));
    }

    private void grantSeedHolder(ServerPlayer player) {
        CompoundTag playerData = player.getPersistentData();
        if (playerData.getBoolean(SEED_HOLDER_GRANTED_TAG)) {
            return;
        }

        ItemStack seedHolder = new ItemStack(ModItems.SEED_HOLDER.get());
        if (!player.getInventory().add(seedHolder)) {
            player.drop(seedHolder, false);
        }
        playerData.putBoolean(SEED_HOLDER_GRANTED_TAG, true);
        player.sendSystemMessage(Component.literal("Penny gave you a Seed Holder.").withStyle(ChatFormatting.GREEN));
    }

    private void unlockSeedHolderRecipe(ServerPlayer player) {
        Optional<? extends Recipe<?>> recipe = player.server.getRecipeManager().byKey(SEED_HOLDER_RECIPE);
        recipe.ifPresent(value -> player.awardRecipes(List.of(value)));
    }

    private List<WaveSpawnDirection> prepareWaveSpawnSchedule(ServerLevel level, GardenWaveDefinition definition) {
        activeWaveEntityIds.clear();
        activeWaveDirections.clear();
        activeWaveStartTick = level.getGameTime();
        activeWaveNextSpawnTick = activeWaveStartTick + FIRST_SPAWN_DELAY_TICKS;
        activeWaveTotalZombies = totalSpawnCount(definition);
        activeWaveSpawned = 0;
        activeWaveFinalPushStarted = false;
        List<WaveSpawnDirection> waveDirections = new ArrayList<>();

        for (WaveSpawnGroup group : definition.spawnGroups()) {
            List<WaveSpawnDirection> directions = resolveDirections(level, group);
            waveDirections.addAll(directions);
        }

        activeWaveDirections.addAll(waveDirections.stream().distinct().toList());
        return List.copyOf(activeWaveDirections);
    }

    private int totalSpawnCount(GardenWaveDefinition definition) {
        int total = 0;
        for (WaveSpawnGroup group : definition.spawnGroups()) {
            total += group.count();
        }
        return total;
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

    private void tickWaveSpawnSchedule(ServerLevel level) {
        GardenWaveProgress waveProgress = getWaveProgress(level);
        if (!waveProgress.waveActive()) {
            clearWaveRuntimeState();
            return;
        }

        if (activeWaveStartTick < 0L || activeWaveTotalZombies <= 0 || activeWaveDirections.isEmpty()) {
            prepareWaveSpawnSchedule(level, waveDefinition(waveProgress.currentWave()));
        }

        GardenWaveDefinition definition = waveDefinition(waveProgress.currentWave());
        long gameTime = level.getGameTime();
        long elapsed = Math.max(0L, gameTime - activeWaveStartTick);
        if (!activeWaveFinalPushStarted && elapsed >= (long) (DEFAULT_WAVE_DURATION_TICKS * FINAL_PUSH_PROGRESS)) {
            spawnFinalWavePush(level, definition);
            activeWaveFinalPushStarted = true;
            return;
        }

        if (gameTime >= activeWaveNextSpawnTick && activeWaveSpawned < activeWaveTotalZombies) {
            if (spawnScheduledZombie(level, definition, activeWaveSpawned)) {
                activeWaveSpawned++;
                scheduleNormalNextSpawn(level);
            }
        }
    }

    private void spawnFinalWavePush(ServerLevel level, GardenWaveDefinition definition) {
        int remaining = activeWaveTotalZombies - activeWaveSpawned;
        if (remaining <= 0) {
            return;
        }

        int pushCount = Math.max(1, Math.min(remaining, Math.max(2, activeWaveTotalZombies / 4)));
        for (int i = 0; i < pushCount && activeWaveSpawned < activeWaveTotalZombies; i++) {
            if (spawnScheduledZombie(level, definition, activeWaveSpawned)) {
                activeWaveSpawned++;
            }
        }
        activeWaveNextSpawnTick = level.getGameTime() + KILL_ACCELERATED_MIN_DELAY_TICKS;
    }

    private void scheduleNormalNextSpawn(ServerLevel level) {
        int remaining = activeWaveTotalZombies - activeWaveSpawned;
        if (remaining <= 0) {
            activeWaveNextSpawnTick = Long.MAX_VALUE;
            return;
        }

        long elapsed = Math.max(0L, level.getGameTime() - activeWaveStartTick);
        long timeLeftBeforePush = Math.max(20L, (long) (DEFAULT_WAVE_DURATION_TICKS * FINAL_PUSH_PROGRESS) - elapsed);
        long interval = Math.max(20L * 6, timeLeftBeforePush / Math.max(1, remaining));
        activeWaveNextSpawnTick = level.getGameTime() + interval;
    }

    private void accelerateNextSpawnAfterKill(ServerLevel level) {
        if (activeWaveSpawned >= activeWaveTotalZombies) {
            return;
        }

        long acceleratedTick = level.getGameTime() + KILL_ACCELERATED_MIN_DELAY_TICKS + level.random.nextInt(KILL_ACCELERATED_RANDOM_DELAY_TICKS + 1);
        if (activeWaveNextSpawnTick < 0L || acceleratedTick < activeWaveNextSpawnTick) {
            activeWaveNextSpawnTick = acceleratedTick;
        }
    }

    private boolean spawnScheduledZombie(ServerLevel level, GardenWaveDefinition definition, int spawnIndex) {
        WaveSpawnGroup group = groupForSpawnIndex(definition, spawnIndex);
        if (group == null) {
            return false;
        }

        Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(new ResourceLocation(group.entityTypeId()));
        if (entityType.isEmpty()) {
            return false;
        }

        WaveSpawnDirection direction = activeWaveDirections.get(spawnIndex % activeWaveDirections.size());
        BlockPos spawnPos = findSpawnPos(level, direction, spawnIndex, Math.max(1, activeWaveTotalZombies));
        Entity entity = spawnWaveEntity(level, entityType.get(), spawnPos);
        if (entity == null) {
            return false;
        }

        if (entity instanceof Mob mob) {
            mob.setPersistenceRequired();
            moveMobTowardTotem(mob);
        }
        activeWaveEntityIds.add(entity.getUUID());
        return true;
    }

    private WaveSpawnGroup groupForSpawnIndex(GardenWaveDefinition definition, int spawnIndex) {
        int seen = 0;
        for (WaveSpawnGroup group : definition.spawnGroups()) {
            seen += group.count();
            if (spawnIndex < seen) {
                return group;
            }
        }
        return null;
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
        if (!getWaveProgress(level).waveActive()) {
            return;
        }

        int activeBeforeCleanup = activeWaveEntityIds.size();
        activeWaveEntityIds.removeIf(entityId -> {
            Entity entity = level.getEntity(entityId);
            return entity == null || !entity.isAlive();
        });
        if (activeWaveEntityIds.size() < activeBeforeCleanup) {
            accelerateNextSpawnAfterKill(level);
        }

        if (activeWaveSpawned >= activeWaveTotalZombies && activeWaveEntityIds.isEmpty()) {
            completeCurrentWaveForNearbyPlayers(level);
        }
    }

    private void tickActiveWaveZombies(ServerLevel level) {
        if (!getWaveProgress(level).waveActive()) {
            return;
        }

        for (UUID entityId : List.copyOf(activeWaveEntityIds)) {
            Entity entity = level.getEntity(entityId);
            if (!(entity instanceof Mob mob) || !mob.isAlive()) {
                continue;
            }

            if (PlantEntityManager.attackNearbyPlant(level, mob, ZOMBIE_TOTEM_DAMAGE)) {
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

        getWaveProgress(level).failCurrentWave();
        markWaveProgressDirty(level);
        discardActiveWaveEntities(level);
        clearWaveRuntimeState();
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
            GardenWaveProgress waveProgress = getWaveProgress(level);
            int completedWave = waveProgress.currentWave();
            waveProgress.completeCurrentWave();
            markWaveProgressDirty(level);
            totemHealth = TOTEM_MAX_HEALTH;
            grantMilestoneRewards(level, completedWave);
            setChanged();
        }
    }

    private void grantMilestoneRewards(ServerLevel level, int wave) {
        GardenWaveDefinition definition = GardenWaves.get(gardenId, wave);
        GardenWaveProgress waveProgress = getWaveProgress(level);
        if (definition.rewards().isEmpty() || waveProgress.isRewardClaimed(wave)) {
            return;
        }

        waveProgress.markRewardClaimed(wave);
        markWaveProgressDirty(level);
        for (WaveReward reward : definition.rewards()) {
            for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                player.sendSystemMessage(Component.literal("Reward unlocked: " + reward.displayName()).withStyle(ChatFormatting.GOLD));
                if (reward.type() == net.PvZModders.PvZMod.progression.waves.WaveRewardType.PLANT_UNLOCK) {
                    PlantSeedDefinition.getByPlantId(reward.id()).ifPresent(plantDefinition -> {
                        if (!SeedStorage.addPlantPacketsToLoadout(player, plantDefinition.seedPacketId(), 10)) {
                            player.sendSystemMessage(Component.literal("Your plant hotbar is full. Visit a garden loadout station later to equip " + plantDefinition.displayName() + ".")
                                    .withStyle(ChatFormatting.YELLOW));
                        }
                    });
                } else if (reward.type() == net.PvZModders.PvZMod.progression.waves.WaveRewardType.ITEM_UNLOCK
                        && reward.id().equals("targeting_priority_changer")) {
                    ItemStack rewardStack = new ItemStack(ModItems.TARGETING_PRIORITY_CHANGER.get());
                    if (!player.getInventory().add(rewardStack)) {
                        player.drop(rewardStack, false);
                    }
                }
            }
        }
        // TODO: Apply plant unlocks, item unlocks, upgrades, and completion flags to real player/garden progression.
    }

    private void tickGardenPlantProduction(ServerLevel level) {
        GardenPlantProductionSavedData.get(level).tick(level, gardenId, getWaveProgress(level).currentWave(), gardenPlants());
    }

    private List<GardenPlantDefinition> gardenPlants() {
        return switch (gardenId) {
            case INITIAL_PLAINS -> GardenPlantDefinition.originalGardenPlants();
            case DESERT -> GardenPlantDefinition.ancientEgyptPlants();
            default -> List.of();
        };
    }

    private GardenWaveDefinition waveDefinition(int wave) {
        return GardenWaves.get(gardenId, wave);
    }

    private void ensureInitialized(ServerLevel level, BlockPos pos) {
        if (!initialized) {
            initializeFromPlotter(level, pos);
        }
        migrateLegacyWaveProgress(level);
        unlockSunForExistingOriginalProgress(level);
        placeTotemColumn(level, pos);
        registerPortal(level, pos);
        syncHealthBar(level, pos);
    }

    private void updateWaveBossBar(ServerLevel level) {
        GardenWaveProgress waveProgress = getWaveProgress(level);
        if (!waveProgress.waveActive()) {
            waveBossBar.removeAllPlayers();
            return;
        }

        if (activeWaveStartTick < 0L) {
            activeWaveStartTick = level.getGameTime();
        }

        long elapsed = Math.max(0L, level.getGameTime() - activeWaveStartTick);
        float progress = Math.min(1.0F, elapsed / (float) DEFAULT_WAVE_DURATION_TICKS);
        waveBossBar.setName(Component.literal("Wave " + waveProgress.currentWave() + " - Defend the Totem"));
        waveBossBar.setProgress(progress);

        Set<ServerPlayer> nearbyPlayers = new HashSet<>();
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 4096.0D) {
                nearbyPlayers.add(player);
                waveBossBar.addPlayer(player);
            }
        }

        for (ServerPlayer player : List.copyOf(waveBossBar.getPlayers())) {
            if (!nearbyPlayers.contains(player)) {
                waveBossBar.removePlayer(player);
            }
        }
    }

    private void clearWaveRuntimeState() {
        activeWaveStartTick = -1L;
        activeWaveNextSpawnTick = -1L;
        activeWaveTotalZombies = 0;
        activeWaveSpawned = 0;
        activeWaveFinalPushStarted = false;
        activeWaveDirections.clear();
        waveBossBar.removeAllPlayers();
    }

    private GardenWaveProgress getWaveProgress() {
        if (level instanceof ServerLevel serverLevel) {
            return getWaveProgress(serverLevel);
        }
        return legacyWaveProgress;
    }

    private GardenWaveProgress getWaveProgress(ServerLevel level) {
        GardenProgressSavedData progressData = GardenProgressSavedData.get(level);
        progressData.adoptLegacyProgressIfUnset(gardenId, legacyWaveProgress);
        return progressData.getWaveProgress(gardenId);
    }

    private void markWaveProgressDirty(ServerLevel level) {
        GardenProgressSavedData.get(level).setDirty();
    }

    private void migrateLegacyWaveProgress(ServerLevel level) {
        GardenProgressSavedData.get(level).adoptLegacyProgressIfUnset(gardenId, legacyWaveProgress);
    }

    private void unlockSunForExistingOriginalProgress(ServerLevel level) {
        if (gardenId == GardenId.INITIAL_PLAINS && getWaveProgress(level).currentWave() > 1 && !level.players().isEmpty()) {
            SunManager.unlockSunDrops(level, level.players().get(0));
        }
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

    private void registerPortal(ServerLevel level, BlockPos pos) {
        GardenPortalSavedData.get(level).setPortal(gardenId, level, pos);
    }

    private BlockPos findTeleportArrival(ServerLevel level, BlockPos targetTotemPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = targetTotemPos.relative(direction, 2);
            if (isSafeTeleportSpot(level, candidate)) {
                return candidate;
            }
        }

        return targetTotemPos.above(3);
    }

    private boolean isSafeTeleportSpot(ServerLevel level, BlockPos pos) {
        return !level.getBlockState(pos.below()).isAir()
                && level.getBlockState(pos).isAir()
                && level.getBlockState(pos.above()).isAir();
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
        if (tag.contains("GardenId")) {
            try {
                gardenId = GardenId.valueOf(tag.getString("GardenId"));
            } catch (IllegalArgumentException ignored) {
                gardenId = GardenId.INITIAL_PLAINS;
            }
        }
        gardenName = tag.getString("GardenName");
        biomeName = tag.getString("BiomeName");
        totemHealth = tag.contains("TotemHealth") ? tag.getInt("TotemHealth") : TOTEM_MAX_HEALTH;
        totemY = tag.contains("TotemY") ? tag.getDouble("TotemY") : worldPosition.getY();
        sinkingPlotterY = tag.contains("SinkingPlotterY") ? tag.getDouble("SinkingPlotterY") : worldPosition.getY();
        if (tag.contains("WaveProgress")) {
            legacyWaveProgress.load(tag.getCompound("WaveProgress"));
        }
        activeWaveStartTick = tag.contains("ActiveWaveStartTick") ? tag.getLong("ActiveWaveStartTick") : -1L;
        activeWaveNextSpawnTick = tag.contains("ActiveWaveNextSpawnTick") ? tag.getLong("ActiveWaveNextSpawnTick") : -1L;
        activeWaveTotalZombies = tag.getInt("ActiveWaveTotalZombies");
        activeWaveSpawned = tag.getInt("ActiveWaveSpawned");
        activeWaveFinalPushStarted = tag.getBoolean("ActiveWaveFinalPushStarted");
        activeWaveDirections.clear();
        ListTag directionsTag = tag.getList("ActiveWaveDirections", net.minecraft.nbt.Tag.TAG_STRING);
        for (int i = 0; i < directionsTag.size(); i++) {
            try {
                activeWaveDirections.add(WaveSpawnDirection.valueOf(directionsTag.getString(i)));
            } catch (IllegalArgumentException ignored) {
            }
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
        tag.putString("GardenId", gardenId.name());
        tag.putString("GardenName", gardenName);
        tag.putString("BiomeName", biomeName);
        tag.putInt("TotemHealth", totemHealth);
        tag.putDouble("TotemY", totemY);
        tag.putDouble("SinkingPlotterY", sinkingPlotterY);
        CompoundTag waveTag = new CompoundTag();
        getWaveProgress().save(waveTag);
        tag.put("WaveProgress", waveTag);
        tag.putLong("ActiveWaveStartTick", activeWaveStartTick);
        tag.putLong("ActiveWaveNextSpawnTick", activeWaveNextSpawnTick);
        tag.putInt("ActiveWaveTotalZombies", activeWaveTotalZombies);
        tag.putInt("ActiveWaveSpawned", activeWaveSpawned);
        tag.putBoolean("ActiveWaveFinalPushStarted", activeWaveFinalPushStarted);
        ListTag directionsTag = new ListTag();
        for (WaveSpawnDirection direction : activeWaveDirections) {
            directionsTag.add(net.minecraft.nbt.StringTag.valueOf(direction.name()));
        }
        tag.put("ActiveWaveDirections", directionsTag);
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
            waveBossBar.removeAllPlayers();
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
