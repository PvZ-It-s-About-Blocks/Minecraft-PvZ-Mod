package net.PvZModders.PvZMod.block.entity;

import net.PvZModders.PvZMod.progression.GardenDefinition;
import net.PvZModders.PvZMod.progression.GardenBiomeCategory;
import net.PvZModders.PvZMod.progression.GardenDefinitions;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.GardenPortalSavedData;
import net.PvZModders.PvZMod.progression.pirate.PirateSeasPlankManager;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.custom.GardenTotemBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class GardenPlotterBlockEntity extends BlockEntity {
    private static final int WIDTH = 15;
    private static final int LENGTH = 15;
    private static final int AIR_CLEARANCE = 3;
    private static final int PREVIEW_REFRESH_TICKS = 20;
    private static final int STATUS_REFRESH_TICKS = 40;
    private static final int GREENHOUSE_MIN_DISTANCE = 60;

    private final Map<PreviewMarker, UUID> previewDisplayIds = new HashMap<>();
    private UUID promptDisplayId;

    public GardenPlotterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GARDEN_PLOTTER_BE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GardenPlotterBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel) || serverLevel.getGameTime() % 10 != 0) {
            return;
        }

        be.showValidationPreview(serverLevel, pos);
        be.showGardenMessage(serverLevel, pos);
    }

    private void showValidationPreview(ServerLevel level, BlockPos origin) {
        if (level.getGameTime() % PREVIEW_REFRESH_TICKS != 0) {
            return;
        }

        GardenId intendedGardenId = intendedGardenId(level, origin);
        ValidationResult validation = validateGarden(level, origin, intendedGardenId);
        syncValidationPreview(level, validation.markers());

        if (validation.valid()) {
            showPromptDisplay(level, origin);
        } else {
            clearPromptDisplay(level);
        }
    }

    private void syncValidationPreview(ServerLevel level, Set<PreviewMarker> markers) {
        List<PreviewMarker> staleMarkers = new ArrayList<>();
        for (PreviewMarker marker : previewDisplayIds.keySet()) {
            if (!markers.contains(marker)) {
                staleMarkers.add(marker);
            }
        }

        for (PreviewMarker marker : staleMarkers) {
            Entity entity = level.getEntity(previewDisplayIds.remove(marker));
            if (entity != null) {
                entity.discard();
            }
        }

        for (PreviewMarker marker : markers) {
            if (!previewDisplayIds.containsKey(marker) || level.getEntity(previewDisplayIds.get(marker)) == null) {
                spawnPreviewDisplay(level, marker);
            }
        }
    }

    private void spawnPreviewDisplay(ServerLevel level, PreviewMarker marker) {
        Display.BlockDisplay preview = EntityType.BLOCK_DISPLAY.create(level);
        if (preview == null) {
            return;
        }

        BlockState previewState = marker.valid() ? Blocks.LIME_STAINED_GLASS.defaultBlockState() : Blocks.RED_STAINED_GLASS.defaultBlockState();
        preview.load(marker.fullBlock() ? createObstructionDisplayTag(previewState) : createFloorPreviewDisplayTag(previewState));
        preview.setPos(marker.pos().getX(), marker.pos().getY(), marker.pos().getZ());
        preview.setNoGravity(true);
        level.addFreshEntity(preview);
        previewDisplayIds.put(marker, preview.getUUID());
    }

    private CompoundTag createFloorPreviewDisplayTag(BlockState previewState) {
        return createBlockDisplayTag(previewState, 0.0F, 0.03F, 0.0F, 1.0F, 0.04F, 1.0F);
    }

    private CompoundTag createObstructionDisplayTag(BlockState previewState) {
        return createBlockDisplayTag(previewState, -0.015F, -0.015F, -0.015F, 1.03F, 1.03F, 1.03F);
    }

    private CompoundTag createBlockDisplayTag(BlockState previewState, float translateX, float translateY, float translateZ, float scaleX, float scaleY, float scaleZ) {
        CompoundTag tag = new CompoundTag();
        tag.put("block_state", NbtUtils.writeBlockState(previewState));

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

    private void clearValidationPreview(ServerLevel level) {
        for (UUID displayId : previewDisplayIds.values()) {
            Entity entity = level.getEntity(displayId);
            if (entity != null) {
                entity.discard();
            }
        }
        previewDisplayIds.clear();
    }

    private void showPromptDisplay(ServerLevel level, BlockPos origin) {
        if (promptDisplayId != null && level.getEntity(promptDisplayId) != null) {
            return;
        }

        Display.TextDisplay prompt = EntityType.TEXT_DISPLAY.create(level);
        if (prompt == null) {
            return;
        }

        CompoundTag tag = new CompoundTag();
        Component text = Component.literal("Right click to create garden").withStyle(ChatFormatting.GREEN);
        tag.putString("text", Component.Serializer.toJson(text));
        tag.putInt("line_width", 220);
        tag.putByte("text_opacity", (byte) 255);
        tag.putInt("background", 0x66000000);
        tag.putBoolean("see_through", true);
        tag.putString("billboard", "center");
        tag.putFloat("view_range", 32.0F);
        tag.putFloat("shadow_radius", 0.0F);
        tag.putFloat("shadow_strength", 0.0F);
        prompt.load(tag);
        prompt.setPos(origin.getX() + 0.5D, origin.getY() + 2.2D, origin.getZ() + 0.5D);
        prompt.setNoGravity(true);
        level.addFreshEntity(prompt);
        promptDisplayId = prompt.getUUID();
    }

    private void clearPromptDisplay(ServerLevel level) {
        if (promptDisplayId == null) {
            return;
        }

        Entity entity = level.getEntity(promptDisplayId);
        if (entity != null) {
            entity.discard();
        }
        promptDisplayId = null;
    }

    private void showGardenMessage(ServerLevel level, BlockPos origin) {
        if (level.getGameTime() % STATUS_REFRESH_TICKS != 0) {
            return;
        }

        GardenId intendedGardenId = intendedGardenId(level, origin);
        ValidationResult validation = validateGarden(level, origin, intendedGardenId);
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(origin).unwrapKey();
        Optional<GardenBiomeCategory> category = biomeKey.flatMap(GardenBiomeCategory::forBiome);
        String biomeName = category
                .map(GardenBiomeCategory::displayName)
                .orElse("Unknown");
        GardenDefinition garden = GardenDefinitions.get(intendedGardenId);
        TextColor color = validation.valid()
                ? TextColor.fromRgb(category.map(GardenBiomeCategory::color).orElse(0x2F9F3F))
                : TextColor.fromRgb(0xD33F3F);
        Component message = Component.literal("Current Biome: " + biomeName + ", Garden: " + garden.displayName())
                .withStyle(style -> style.withColor(color));

        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(origin.getX() + 0.5D, origin.getY() + 0.5D, origin.getZ() + 0.5D) <= 144.0D) {
                player.displayClientMessage(message, true);
            }
        }
    }

    public boolean tryCreateGarden(ServerPlayer player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        GardenId intendedGardenId = intendedGardenId(serverLevel, worldPosition);
        ValidationResult validation = validateGarden(serverLevel, worldPosition, intendedGardenId);
        if (!validation.valid()) {
            player.displayClientMessage(Component.literal("Garden area is invalid").withStyle(ChatFormatting.RED), true);
            return false;
        }

        Optional<GardenId> creationGardenId = resolveCreationGardenId(serverLevel, worldPosition, intendedGardenId, player);
        if (creationGardenId.isEmpty()) {
            return false;
        }

        clearValidationPreview(serverLevel);
        clearPromptDisplay(serverLevel);
        serverLevel.setBlock(worldPosition, ModBlocks.GARDEN_TOTEM.get().defaultBlockState().setValue(GardenTotemBlock.PART, 0), 3);
        serverLevel.setBlock(worldPosition.above(), ModBlocks.GARDEN_TOTEM.get().defaultBlockState().setValue(GardenTotemBlock.PART, 1), 3);
        serverLevel.setBlock(worldPosition.above(2), ModBlocks.GARDEN_TOTEM.get().defaultBlockState().setValue(GardenTotemBlock.PART, 2), 3);
        if (serverLevel.getBlockEntity(worldPosition) instanceof GardenTotemBlockEntity gardenTotem) {
            gardenTotem.initializeFromPlotter(serverLevel, worldPosition, creationGardenId.get());
        }
        if (creationGardenId.get() == GardenId.PIRATE_SEAS) {
            PirateSeasPlankManager.createPermanentTotemPlatform(serverLevel, worldPosition);
        }
        player.displayClientMessage(Component.literal(GardenDefinitions.get(creationGardenId.get()).displayName() + " created").withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    private Optional<GardenId> resolveCreationGardenId(ServerLevel level, BlockPos origin, GardenId intendedGardenId, ServerPlayer player) {
        GardenPortalSavedData portalData = GardenPortalSavedData.get(level);
        if (!portalData.hasGarden(intendedGardenId)) {
            return Optional.of(intendedGardenId);
        }

        if (portalData.hasGarden(GardenId.GREENHOUSE)) {
            player.displayClientMessage(Component.literal("Greenhouse Garden already exists.").withStyle(ChatFormatting.RED), true);
            return Optional.empty();
        }

        if (portalData.isAnyGardenWithin(level, origin, GREENHOUSE_MIN_DISTANCE)) {
            player.displayClientMessage(Component.literal("Greenhouse Garden must be at least 60 blocks away from another garden.").withStyle(ChatFormatting.RED), true);
            return Optional.empty();
        }

        return Optional.of(GardenId.GREENHOUSE);
    }

    private GardenId intendedGardenId(ServerLevel level, BlockPos origin) {
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(origin).unwrapKey();
        return biomeKey
                .flatMap(GardenBiomeCategory::forBiome)
                .map(GardenBiomeCategory::gardenId)
                .or(() -> biomeKey.flatMap(GardenDefinitions::forBiome).map(GardenDefinition::id))
                .orElse(GardenId.INITIAL_PLAINS);
    }

    private ValidationResult validateGarden(ServerLevel level, BlockPos origin, GardenId intendedGardenId) {
        Set<PreviewMarker> markers = new HashSet<>();
        int startX = origin.getX() - (WIDTH / 2);
        int startZ = origin.getZ() - (LENGTH / 2);
        int y = origin.getY();
        boolean valid = true;
        boolean bigWaveBeach = intendedGardenId == GardenId.BIG_WAVE_BEACH;
        boolean pirateSeas = intendedGardenId == GardenId.PIRATE_SEAS;
        boolean waterAllowed = bigWaveBeach || pirateSeas;
        int waterTiles = 0;
        int beachLandTiles = 0;

        for (int dx = 0; dx < WIDTH; dx++) {
            for (int dz = 0; dz < LENGTH; dz++) {
                BlockPos floorPos = new BlockPos(startX + dx, y - 1, startZ + dz);
                BlockPos gardenPos = floorPos.above();
                BlockState gardenState = level.getBlockState(gardenPos);
                BlockState floorState = level.getBlockState(floorPos);
                boolean waterInPlot = gardenState.is(Blocks.WATER);
                boolean beachLand = floorState.is(BlockTags.SAND) || floorState.is(Blocks.SANDSTONE) || floorState.is(Blocks.SMOOTH_SANDSTONE) || floorState.is(Blocks.CUT_SANDSTONE);
                boolean validFloor = pirateSeas
                        ? (waterInPlot || gardenPos.equals(origin))
                        : floorState.is(BlockTags.DIRT)
                                || floorState.is(BlockTags.SAND)
                                || (bigWaveBeach && beachLand)
                                || (bigWaveBeach && waterInPlot && floorState.isFaceSturdy(level, floorPos, net.minecraft.core.Direction.UP));
                if (waterAllowed && waterInPlot) {
                    waterTiles++;
                } else if (bigWaveBeach && beachLand) {
                    beachLandTiles++;
                }
                markers.add(new PreviewMarker(gardenPos, validFloor, false));

                boolean validGardenLevel = gardenPos.equals(origin) || gardenState.isAir() || (waterAllowed && waterInPlot);
                if (!validGardenLevel) {
                    markers.add(new PreviewMarker(gardenPos, false, true));
                }

                boolean validAir = true;
                for (int clearance = 1; clearance <= AIR_CLEARANCE; clearance++) {
                    BlockPos airPos = gardenPos.above(clearance);
                    if (!level.getBlockState(airPos).isAir()) {
                        markers.add(new PreviewMarker(airPos, false, true));
                        validAir = false;
                    }
                }

                if (!validFloor || !validGardenLevel || !validAir) {
                    valid = false;
                }
            }
        }

        if (bigWaveBeach) {
            int totalTiles = WIDTH * LENGTH;
            int minimumMixTiles = Math.max(1, totalTiles / 5);
            if (waterTiles < minimumMixTiles || beachLandTiles < minimumMixTiles) {
                valid = false;
                markers.add(new PreviewMarker(origin.above(1), false, true));
            }
        } else if (pirateSeas) {
            int minimumOceanTiles = (WIDTH * LENGTH * 3) / 4;
            if (waterTiles < minimumOceanTiles) {
                valid = false;
                markers.add(new PreviewMarker(origin.above(1), false, true));
            }
        }

        return new ValidationResult(valid, markers);
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            clearValidationPreview(serverLevel);
            clearPromptDisplay(serverLevel);
        }
        super.setRemoved();
    }

    private record ValidationResult(boolean valid, Set<PreviewMarker> markers) {
    }

    private record PreviewMarker(BlockPos pos, boolean valid, boolean fullBlock) {
    }
}
