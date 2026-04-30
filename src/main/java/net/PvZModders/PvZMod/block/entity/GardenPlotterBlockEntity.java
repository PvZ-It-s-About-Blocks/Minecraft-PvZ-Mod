package net.PvZModders.PvZMod.block.entity;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GardenPlotterBlockEntity extends BlockEntity {
    private static final int WIDTH = 15;
    private static final int LENGTH = 15;
    private static final int PREVIEW_REFRESH_TICKS = 20;
    private static final int STATUS_REFRESH_TICKS = 40;
    private static final double TOTEM_RISE_SPEED = 0.08D;

    private final List<UUID> previewDisplayIds = new ArrayList<>();
    private UUID promptDisplayId;
    private UUID totemDisplayId;
    private boolean gardenCreated;
    private double totemY;

    public GardenPlotterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GARDEN_PLOTTER_BE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GardenPlotterBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel) || serverLevel.getGameTime() % 10 != 0) {
            return;
        }

        if (be.gardenCreated) {
            be.clearValidationPreview(serverLevel);
            be.clearPromptDisplay(serverLevel);
            be.tickGardenTotem(serverLevel, pos);
            return;
        }

        be.showValidationPreview(serverLevel, pos);
        be.showGardenMessage(serverLevel, pos);
    }

    private void showValidationPreview(ServerLevel level, BlockPos origin) {
        if (level.getGameTime() % PREVIEW_REFRESH_TICKS != 0) {
            return;
        }

        clearValidationPreview(level);

        ValidationResult validation = validateGarden(level, origin);
        int startX = origin.getX() - (WIDTH / 2);
        int startZ = origin.getZ() - (LENGTH / 2);
        int y = origin.getY();

        for (int dx = 0; dx < WIDTH; dx++) {
            for (int dz = 0; dz < LENGTH; dz++) {
                BlockPos floorPos = new BlockPos(startX + dx, y - 1, startZ + dz);
                BlockPos gardenPos = floorPos.above();

                spawnPreviewDisplay(level, gardenPos, validation.validTiles().contains(gardenPos));
            }
        }

        if (validation.valid()) {
            showPromptDisplay(level, origin);
        } else {
            clearPromptDisplay(level);
        }
    }

    private void spawnPreviewDisplay(ServerLevel level, BlockPos pos, boolean valid) {
        Display.BlockDisplay preview = EntityType.BLOCK_DISPLAY.create(level);
        if (preview == null) {
            return;
        }

        BlockState previewState = valid ? Blocks.LIME_STAINED_GLASS.defaultBlockState() : Blocks.RED_STAINED_GLASS.defaultBlockState();
        preview.load(createPreviewDisplayTag(previewState));
        preview.setPos(pos.getX(), pos.getY(), pos.getZ());
        preview.setNoGravity(true);
        level.addFreshEntity(preview);
        previewDisplayIds.add(preview.getUUID());
    }

    private CompoundTag createPreviewDisplayTag(BlockState previewState) {
        return createBlockDisplayTag(previewState, 0.0F, 0.03F, 0.0F, 1.0F, 0.04F, 1.0F);
    }

    private CompoundTag createTotemDisplayTag() {
        return createBlockDisplayTag(Blocks.TALL_GRASS.defaultBlockState(), 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
        for (UUID displayId : previewDisplayIds) {
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
        String biomeName = biomeKey
                .map(key -> key.location().toString())
                .orElse("unknown");
        GardenDefinition garden = biomeKey
                .flatMap(GardenDefinitions::forBiome)
                .orElse(GardenDefinitions.get(GardenId.INITIAL_PLAINS));
        ChatFormatting color = validation.valid() ? ChatFormatting.GREEN : ChatFormatting.RED;
        Component message = Component.literal("Current Biome: " + biomeName + ", Garden: " + garden.displayName())
                .withStyle(color);

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
        if (gardenCreated) {
            openGardenMenu(player);
            return true;
        }

        ValidationResult validation = validateGarden(serverLevel, worldPosition);
        if (!validation.valid()) {
            player.displayClientMessage(Component.literal("Garden area is invalid").withStyle(ChatFormatting.RED), true);
            return false;
        }

        gardenCreated = true;
        totemY = worldPosition.getY() - 1.0D;
        clearValidationPreview(serverLevel);
        clearPromptDisplay(serverLevel);
        spawnGardenTotem(serverLevel, worldPosition);
        setChanged();
        player.displayClientMessage(Component.literal("Garden created").withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    public void openGardenMenu(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Garden totem menu coming soon").withStyle(ChatFormatting.GREEN));
    }

    public boolean isGardenCreated() {
        return gardenCreated;
    }

    private ValidationResult validateGarden(ServerLevel level, BlockPos origin) {
        List<BlockPos> validTiles = new ArrayList<>();
        int startX = origin.getX() - (WIDTH / 2);
        int startZ = origin.getZ() - (LENGTH / 2);
        int y = origin.getY();
        boolean valid = true;

        for (int dx = 0; dx < WIDTH; dx++) {
            for (int dz = 0; dz < LENGTH; dz++) {
                BlockPos floorPos = new BlockPos(startX + dx, y - 1, startZ + dz);
                BlockPos gardenPos = floorPos.above();
                BlockPos aboveGardenPos = gardenPos.above();

                boolean validFloor = level.getBlockState(floorPos).is(BlockTags.DIRT) || level.getBlockState(floorPos).is(BlockTags.SAND);
                boolean validGardenLevel = gardenPos.equals(origin) || level.getBlockState(gardenPos).isAir();
                boolean validAboveGarden = level.getBlockState(aboveGardenPos).isAir();
                boolean validTile = validFloor && validGardenLevel && validAboveGarden;

                if (validTile) {
                    validTiles.add(gardenPos);
                } else {
                    valid = false;
                }
            }
        }

        return new ValidationResult(valid, validTiles);
    }

    private void spawnGardenTotem(ServerLevel level, BlockPos origin) {
        Display.BlockDisplay totem = EntityType.BLOCK_DISPLAY.create(level);
        if (totem == null) {
            return;
        }

        totem.load(createTotemDisplayTag());
        totem.setPos(origin.getX(), totemY, origin.getZ());
        totem.setNoGravity(true);
        level.addFreshEntity(totem);
        totemDisplayId = totem.getUUID();
    }

    private void tickGardenTotem(ServerLevel level, BlockPos origin) {
        Entity entity = totemDisplayId == null ? null : level.getEntity(totemDisplayId);
        if (entity == null) {
            totemY = Math.min(totemY, origin.getY());
            spawnGardenTotem(level, origin);
            return;
        }

        if (totemY < origin.getY()) {
            totemY = Math.min(origin.getY(), totemY + TOTEM_RISE_SPEED);
            entity.setPos(origin.getX(), totemY, origin.getZ());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        gardenCreated = tag.getBoolean("GardenCreated");
        totemY = tag.contains("TotemY") ? tag.getDouble("TotemY") : worldPosition.getY();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("GardenCreated", gardenCreated);
        tag.putDouble("TotemY", totemY);
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            clearValidationPreview(serverLevel);
            clearPromptDisplay(serverLevel);
            if (totemDisplayId != null) {
                Entity entity = serverLevel.getEntity(totemDisplayId);
                if (entity != null) {
                    entity.discard();
                }
            }
        }
        super.setRemoved();
    }

    private record ValidationResult(boolean valid, List<BlockPos> validTiles) {
    }
}
