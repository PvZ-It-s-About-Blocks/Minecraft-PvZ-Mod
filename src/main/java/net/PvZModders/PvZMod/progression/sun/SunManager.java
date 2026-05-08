package net.PvZModders.PvZMod.progression.sun;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.entity.custom.PvZSunEntity;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.GardenProgressSavedData;
import net.PvZModders.PvZMod.progression.atmosphere.DarkAgesBiomeEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class SunManager {
    public static final int DEFAULT_SUN_VALUE = 25;
    public static final int DEFAULT_SUN_CAP = 300;

    private static final String PLAYER_SUN_TAG = "PvZSun";
    private static final String PLAYER_SUN_CAP_TAG = "PvZSunCap";
    private static final String NEXT_SUN_DROP_TICK_TAG = "PvZNextSunDropTick";
    public static final String SUN_ORB_TAG = "PvZSunOrb";
    public static final String SUN_VALUE_TAG = "PvZSunValue";
    public static final String SUN_SPAWN_TICK_TAG = "PvZSunSpawnTick";
    public static final String SUN_ANCHOR_X_TAG = "PvZSunAnchorX";
    public static final String SUN_ANCHOR_Z_TAG = "PvZSunAnchorZ";
    public static final String SUN_PILLAR_VISIBLE_TAG = "PvZSunPillarVisible";
    private static final String FIRST_WAVE_SUN_GRANTED_TAG = "PvZFirstWaveSunGranted";
    private static final int SUN_LIFETIME_TICKS = 25 * 20;
    private static final int SUN_BLINK_START_TICKS = 20 * 20;
    private static final int SUN_DROP_RADIUS = 32;
    private static final int SUN_DROP_HEIGHT = 15;
    private static final int MIN_DROP_DELAY_TICKS = 4 * 20;
    private static final int RANDOM_DROP_DELAY_TICKS = 3 * 20;
    private static final double SUN_PICKUP_RADIUS = 1.25D;
    private static final DustParticleOptions SUN_PILLAR_PARTICLE = new DustParticleOptions(new Vector3f(1.0F, 0.86F, 0.0F), 1.15F);
    private static final int SUN_PILLAR_PARTICLE_INTERVAL_TICKS = 5;

    private SunManager() {
    }

    public static void unlockSunDrops(ServerLevel level, ServerPlayer player) {
        SunSavedData sunData = SunSavedData.get(level);
        boolean wasUnlocked = sunData.sunUnlocked();
        sunData.unlockSun();
        syncSunBar(player);

        if (!player.getPersistentData().getBoolean(FIRST_WAVE_SUN_GRANTED_TAG)) {
            player.sendSystemMessage(Component.literal("Penny: Sun is your main currency. Pick it up before it fades!").withStyle(ChatFormatting.YELLOW));
            spawnFirstWaveSun(level, player);
            player.getPersistentData().putBoolean(FIRST_WAVE_SUN_GRANTED_TAG, true);
        } else if (!wasUnlocked) {
            player.sendSystemMessage(Component.literal("Penny: Sun will now fall during the day.").withStyle(ChatFormatting.YELLOW));
        }
        scheduleNextSunDrop(player, level.getGameTime() + MIN_DROP_DELAY_TICKS);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ServerLevel overworld = event.getServer().overworld();
        boolean sunUnlocked = SunSavedData.get(overworld).sunUnlocked();
        boolean waveActive = isAnyGardenWaveActive(overworld);
        Set<UUID> updatedSunDrops = new HashSet<>();

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            syncSunBar(player);
            tickNearbySunDrops(player.serverLevel(), player, updatedSunDrops);
            if (sunUnlocked || waveActive) {
                tickSunDrops(player);
            }
        }
    }

    private static boolean isAnyGardenWaveActive(ServerLevel level) {
        GardenProgressSavedData progressData = GardenProgressSavedData.get(level);
        for (GardenId gardenId : GardenId.values()) {
            if (progressData.getWaveProgress(gardenId).waveActive()) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntity().getPersistentData().getBoolean(GardenTotemBlockEntity.WAVE_ZOMBIE_TAG)) {
            event.setDroppedExperience(0);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().getPersistentData().getBoolean(GardenTotemBlockEntity.WAVE_ZOMBIE_TAG)) {
            event.getDrops().clear();
        }
    }

    @SubscribeEvent
    public static void onExperiencePickup(PlayerXpEvent.PickupXp event) {
        ExperienceOrb orb = event.getOrb();
        if (!isSunOrb(orb)) {
            return;
        }

        event.setCanceled(true);
        Player player = event.getEntity();
        addSun(player, getSunValue(orb));
        player.take(orb, 1);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.25F, 1.4F);
        orb.discard();
    }

    @SubscribeEvent
    public static void onSunItemPickup(EntityItemPickupEvent event) {
        ItemEntity item = event.getItem();
        ItemStack stack = item.getItem();
        if (!isSunItem(stack)) {
            return;
        }

        event.setCanceled(true);
        Player player = event.getEntity();
        addSun(player, getSunValue(stack) * stack.getCount());
        player.take(item, stack.getCount());
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.25F, 1.4F);
        item.discard();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        syncSunBar(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        setSun(event.getEntity(), getSun(event.getOriginal()));
        setSunCap(event.getEntity(), getSunCap(event.getOriginal()));
        syncSunBar(event.getEntity());
    }

    public static int getSun(Player player) {
        return player.getPersistentData().getInt(PLAYER_SUN_TAG);
    }

    public static int getSunCap(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (!tag.contains(PLAYER_SUN_CAP_TAG)) {
            tag.putInt(PLAYER_SUN_CAP_TAG, DEFAULT_SUN_CAP);
        }
        return tag.getInt(PLAYER_SUN_CAP_TAG);
    }

    public static void addSun(Player player, int amount) {
        setSun(player, Math.min(getSunCap(player), getSun(player) + amount));
        syncSunBar(player);
    }

    public static boolean spendSun(Player player, int amount) {
        if (getSun(player) < amount) {
            return false;
        }
        setSun(player, getSun(player) - amount);
        syncSunBar(player);
        return true;
    }

    public static int drainSun(Player player) {
        int sun = getSun(player);
        setSun(player, 0);
        syncSunBar(player);
        return sun;
    }

    public static int absorbSunNear(ServerLevel level, ServerPlayer player, Vec3 origin, double radius) {
        int absorbed = 0;
        AABB area = AABB.ofSize(origin, radius * 2.0D, radius * 2.0D, radius * 2.0D);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area, entity -> isSunItem(entity.getItem()))) {
            absorbed += getSunValue(item.getItem()) * item.getItem().getCount();
            item.discard();
        }
        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, area, SunManager::isSunOrb)) {
            absorbed += getSunValue(orb);
            orb.discard();
        }
        if (absorbed > 0) {
            addSun(player, absorbed);
            level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.35F, 1.35F);
        }
        return absorbed;
    }

    public static void setSun(Player player, int amount) {
        int cap = getSunCap(player);
        player.getPersistentData().putInt(PLAYER_SUN_TAG, Math.max(0, Math.min(cap, amount)));
    }

    public static void setSunCap(Player player, int cap) {
        int safeCap = Math.max(DEFAULT_SUN_VALUE, cap);
        player.getPersistentData().putInt(PLAYER_SUN_CAP_TAG, safeCap);
        if (getSun(player) > safeCap) {
            setSun(player, safeCap);
        }
    }

    public static void syncSunBar(Player player) {
        int sun = getSun(player);
        int cap = getSunCap(player);
        player.experienceProgress = cap <= 0 ? 0.0F : sun / (float) cap;
        player.experienceLevel = sun;
        player.totalExperience = sun;

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
        }
    }

    private static void tickSunDrops(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        tickDarkAgesBiomeMessage(player);
        if (!canSpawnNaturalSun(level) || DarkAgesBiomeEffects.shouldSuppressPassiveSunDrops(player)) {
            scheduleNextSunDrop(player, level.getGameTime() + MIN_DROP_DELAY_TICKS);
            return;
        }

        long gameTime = level.getGameTime();
        CompoundTag tag = player.getPersistentData();
        if (!tag.contains(NEXT_SUN_DROP_TICK_TAG) || gameTime >= tag.getLong(NEXT_SUN_DROP_TICK_TAG)) {
            spawnRandomSun(level, player);
            scheduleNextSunDrop(player, gameTime + MIN_DROP_DELAY_TICKS + player.getRandom().nextInt(RANDOM_DROP_DELAY_TICKS + 1));
        }
    }

    private static boolean canSpawnNaturalSun(ServerLevel level) {
        return level.isDay() || isAnyGardenWaveActive(level.getServer().overworld());
    }

    private static void tickDarkAgesBiomeMessage(ServerPlayer player) {
        boolean inDarkAges = DarkAgesBiomeEffects.isPlayerInDarkAgesBiome(player);
        boolean wasInDarkAges = DarkAgesBiomeEffects.wasInDarkAgesBiome(player);
        if (inDarkAges == wasInDarkAges) {
            return;
        }

        DarkAgesBiomeEffects.setInDarkAgesBiome(player, inDarkAges);
        player.displayClientMessage(Component.literal(inDarkAges
                ? "The sky darkens. Natural Sun cannot reach this place."
                : "Natural Sun returns.").withStyle(inDarkAges ? ChatFormatting.DARK_PURPLE : ChatFormatting.YELLOW), true);
    }

    private static void scheduleNextSunDrop(ServerPlayer player, long gameTime) {
        player.getPersistentData().putLong(NEXT_SUN_DROP_TICK_TAG, gameTime);
    }

    private static void spawnFirstWaveSun(ServerLevel level, ServerPlayer player) {
        BlockPos front = player.blockPosition().relative(player.getDirection(), 3);
        BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, front);
        spawnSun(level, ground.above(SUN_DROP_HEIGHT), DEFAULT_SUN_VALUE, true);
    }

    private static void spawnRandomSun(ServerLevel level, ServerPlayer player) {
        int dx = level.random.nextInt(SUN_DROP_RADIUS * 2 + 1) - SUN_DROP_RADIUS;
        int dz = level.random.nextInt(SUN_DROP_RADIUS * 2 + 1) - SUN_DROP_RADIUS;
        BlockPos xz = player.blockPosition().offset(dx, 0, dz);
        BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, xz);
        spawnSun(level, ground.above(SUN_DROP_HEIGHT), DEFAULT_SUN_VALUE, true);
    }

    private static void spawnSun(ServerLevel level, BlockPos pos) {
        spawnSun(level, pos, DEFAULT_SUN_VALUE, true);
    }

    private static void spawnSun(ServerLevel level, BlockPos pos, int value, boolean showPillar) {
        ItemStack stack = new ItemStack(ModItems.SUNDROP.get());
        initializeSunItem(stack, level, value);
        stack.getOrCreateTag().putBoolean(SUN_PILLAR_VISIBLE_TAG, showPillar);
        ItemEntity sun = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
        sun.getPersistentData().putBoolean(SUN_PILLAR_VISIBLE_TAG, showPillar);
        sun.setDeltaMovement(0.0D, -0.03D, 0.0D);
        sun.setNoPickUpDelay();
        level.addFreshEntity(sun);
    }

    public static void spawnSunAt(ServerLevel level, BlockPos pos) {
        spawnSun(level, pos, DEFAULT_SUN_VALUE, false);
    }

    public static void spawnSunAt(ServerLevel level, BlockPos pos, int value) {
        spawnSun(level, pos, value, false);
    }

    public static void initializeSunOrb(ExperienceOrb sun, ServerLevel level, int value) {
        sun.setCustomName(Component.literal("Sun").withStyle(ChatFormatting.YELLOW));
        sun.getPersistentData().putBoolean(SUN_ORB_TAG, true);
        sun.getPersistentData().putInt(SUN_VALUE_TAG, value);
        sun.getPersistentData().putLong(SUN_SPAWN_TICK_TAG, level.getGameTime());
        sun.getPersistentData().putDouble(SUN_ANCHOR_X_TAG, sun.getX());
        sun.getPersistentData().putDouble(SUN_ANCHOR_Z_TAG, sun.getZ());
        if (!sun.getPersistentData().contains(SUN_PILLAR_VISIBLE_TAG)) {
            sun.getPersistentData().putBoolean(SUN_PILLAR_VISIBLE_TAG, true);
        }
    }

    private static void initializeSunItem(ItemStack stack, ServerLevel level, int value) {
        stack.setHoverName(Component.literal("Sun").withStyle(ChatFormatting.YELLOW));
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(SUN_ORB_TAG, true);
        tag.putInt(SUN_VALUE_TAG, value);
        tag.putLong(SUN_SPAWN_TICK_TAG, level.getGameTime());
    }

    private static void tickNearbySunDrops(ServerLevel level, ServerPlayer player, Set<UUID> updatedSunDrops) {
        AABB searchArea = player.getBoundingBox().inflate(SUN_DROP_RADIUS + SUN_DROP_HEIGHT + 16);
        for (Entity entity : level.getEntities(player, searchArea, entity -> entity instanceof ItemEntity item && isSunItem(item.getItem()))) {
            if (updatedSunDrops.add(entity.getUUID()) && entity instanceof ItemEntity item) {
                tickSunItem(level, item);
            }
        }
        for (Entity entity : level.getEntities(player, searchArea, entity -> entity instanceof ExperienceOrb orb && isSunOrb(orb))) {
            if (updatedSunDrops.add(entity.getUUID()) && entity instanceof ExperienceOrb orb) {
                tickSunOrb(level, orb);
            }
        }
    }

    private static void tickSunOrb(ServerLevel level, ExperienceOrb sun) {
        long spawnTick = sun.getPersistentData().getLong(SUN_SPAWN_TICK_TAG);
        int age = (int) (level.getGameTime() - spawnTick);
        if (age >= SUN_LIFETIME_TICKS) {
            sun.discard();
            return;
        }

        if (age >= SUN_BLINK_START_TICKS) {
            sun.setInvisible((age / 5) % 2 == 0);
        }

        keepSunAnchoredUntilClose(level, sun);
    }

    private static void renderSunPillarParticles(ServerLevel level, ItemEntity sun) {
        if (level.getGameTime() % SUN_PILLAR_PARTICLE_INTERVAL_TICKS != 0L) {
            return;
        }

        BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sun.blockPosition());
        double bottomY = Math.min(sun.getY(), ground.getY() + 0.2D);
        double topY = Math.max(sun.getY() + 0.35D, ground.getY() + 3.0D);
        for (double y = bottomY; y <= topY; y += 0.85D) {
            level.sendParticles(SUN_PILLAR_PARTICLE, sun.getX(), y, sun.getZ(), 1, 0.04D, 0.0D, 0.04D, 0.0D);
        }
        level.sendParticles(ParticleTypes.END_ROD, sun.getX(), sun.getY() + 0.3D, sun.getZ(), 1, 0.03D, 0.08D, 0.03D, 0.0D);
    }

    private static void tickSunItem(ServerLevel level, ItemEntity sun) {
        CompoundTag stackTag = sun.getItem().getOrCreateTag();
        if (!stackTag.contains(SUN_SPAWN_TICK_TAG)) {
            stackTag.putLong(SUN_SPAWN_TICK_TAG, level.getGameTime());
        }
        long spawnTick = stackTag.getLong(SUN_SPAWN_TICK_TAG);
        int age = (int) (level.getGameTime() - spawnTick);
        if (age >= SUN_LIFETIME_TICKS) {
            sun.discard();
            return;
        }

        if (age >= SUN_BLINK_START_TICKS) {
            sun.setInvisible((age / 5) % 2 == 0);
        }

        if (shouldRenderSunPillar(sun)) {
            renderSunPillarParticles(level, sun);
        }
        keepSunAnchoredUntilClose(level, sun);
    }

    private static void keepSunAnchoredUntilClose(ServerLevel level, ExperienceOrb sun) {
        Player nearestPlayer = level.getNearestPlayer(sun, SUN_PICKUP_RADIUS);
        if (nearestPlayer != null) {
            return;
        }

        CompoundTag tag = sun.getPersistentData();
        if (!tag.contains(SUN_ANCHOR_X_TAG) || !tag.contains(SUN_ANCHOR_Z_TAG)) {
            tag.putDouble(SUN_ANCHOR_X_TAG, sun.getX());
            tag.putDouble(SUN_ANCHOR_Z_TAG, sun.getZ());
        }

        double anchorX = tag.getDouble(SUN_ANCHOR_X_TAG);
        double anchorZ = tag.getDouble(SUN_ANCHOR_Z_TAG);
        Vec3 movement = sun.getDeltaMovement();
        sun.setPos(anchorX, sun.getY(), anchorZ);
        sun.setDeltaMovement(0.0D, Math.min(movement.y, 0.0D), 0.0D);
    }

    private static void keepSunAnchoredUntilClose(ServerLevel level, ItemEntity sun) {
        Player nearestPlayer = level.getNearestPlayer(sun, SUN_PICKUP_RADIUS);
        if (nearestPlayer != null) {
            return;
        }

        CompoundTag tag = sun.getPersistentData();
        if (!tag.contains(SUN_ANCHOR_X_TAG) || !tag.contains(SUN_ANCHOR_Z_TAG)) {
            tag.putDouble(SUN_ANCHOR_X_TAG, sun.getX());
            tag.putDouble(SUN_ANCHOR_Z_TAG, sun.getZ());
        }

        double anchorX = tag.getDouble(SUN_ANCHOR_X_TAG);
        double anchorZ = tag.getDouble(SUN_ANCHOR_Z_TAG);
        Vec3 movement = sun.getDeltaMovement();
        sun.setPos(anchorX, sun.getY(), anchorZ);
        sun.setDeltaMovement(0.0D, Math.min(movement.y, 0.0D), 0.0D);
    }

    public static boolean isSunOrb(ExperienceOrb orb) {
        return orb instanceof PvZSunEntity || orb.getPersistentData().getBoolean(SUN_ORB_TAG);
    }

    private static int getSunValue(ExperienceOrb orb) {
        if (orb.getPersistentData().contains(SUN_VALUE_TAG)) {
            return orb.getPersistentData().getInt(SUN_VALUE_TAG);
        }
        return DEFAULT_SUN_VALUE;
    }

    private static boolean isSunItem(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.SUNDROP.get())
                && (!stack.hasTag() || stack.getOrCreateTag().getBoolean(SUN_ORB_TAG) || "Sun".equals(stack.getHoverName().getString()));
    }

    private static int getSunValue(ItemStack stack) {
        if (stack.hasTag() && stack.getOrCreateTag().contains(SUN_VALUE_TAG)) {
            return stack.getOrCreateTag().getInt(SUN_VALUE_TAG);
        }
        return DEFAULT_SUN_VALUE;
    }

    public static boolean shouldRenderSunPillar(ExperienceOrb orb) {
        CompoundTag tag = orb.getPersistentData();
        return !tag.contains(SUN_PILLAR_VISIBLE_TAG) || tag.getBoolean(SUN_PILLAR_VISIBLE_TAG);
    }

    public static boolean shouldRenderSunPillar(ItemEntity item) {
        CompoundTag entityTag = item.getPersistentData();
        if (entityTag.contains(SUN_PILLAR_VISIBLE_TAG)) {
            return entityTag.getBoolean(SUN_PILLAR_VISIBLE_TAG);
        }
        CompoundTag stackTag = item.getItem().getOrCreateTag();
        return stackTag.getBoolean(SUN_PILLAR_VISIBLE_TAG);
    }
}
