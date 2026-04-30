package net.PvZModders.PvZMod.block.entity;

import net.PvZModders.PvZMod.progression.GardenDefinition;
import net.PvZModders.PvZMod.progression.GardenBiomeCategory;
import net.PvZModders.PvZMod.progression.GardenDefinitions;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.block.ModBlocks;
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

        ValidationResult validation = validateGarden(level, origin);
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

        ValidationResult validation = validateGarden(level, origin);
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(origin).unwrapKey();
        Optional<GardenBiomeCategory> category = biomeKey.flatMap(GardenBiomeCategory::forBiome);
        String biomeName = category
                .map(GardenBiomeCategory::displayName)
                .orElse("Unknown");
        GardenDefinition garden = category
                .map(GardenBiomeCategory::gardenId)
                .map(GardenDefinitions::get)
                .or(() -> biomeKey.flatMap(GardenDefinitions::forBiome))
                .orElse(GardenDefinitions.get(GardenId.INITIAL_PLAINS));
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
        ValidationResult validation = validateGarden(serverLevel, worldPosition);
        if (!validation.valid()) {
            player.displayClientMessage(Component.literal("Garden area is invalid").withStyle(ChatFormatting.RED), true);
            return false;
        }

        clearValidationPreview(serverLevel);
        clearPromptDisplay(serverLevel);
        serverLevel.setBlock(worldPosition, ModBlocks.GARDEN_TOTEM.get().defaultBlockState(), 3);
        if (serverLevel.getBlockEntity(worldPosition) instanceof GardenTotemBlockEntity gardenTotem) {
            gardenTotem.initializeFromPlotter(serverLevel, worldPosition);
        }
        player.displayClientMessage(Component.literal("Garden created").withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    private ValidationResult validateGarden(ServerLevel level, BlockPos origin) {
        Set<PreviewMarker> markers = new HashSet<>();
        int startX = origin.getX() - (WIDTH / 2);
        int startZ = origin.getZ() - (LENGTH / 2);
        int y = origin.getY();
        boolean valid = true;

        for (int dx = 0; dx < WIDTH; dx++) {
            for (int dz = 0; dz < LENGTH; dz++) {
                BlockPos floorPos = new BlockPos(startX + dx, y - 1, startZ + dz);
                BlockPos gardenPos = floorPos.above();
                boolean validFloor = level.getBlockState(floorPos).is(BlockTags.DIRT) || level.getBlockState(floorPos).is(BlockTags.SAND);
                markers.add(new PreviewMarker(gardenPos, validFloor, false));

                boolean validGardenLevel = gardenPos.equals(origin) || level.getBlockState(gardenPos).isAir();
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
