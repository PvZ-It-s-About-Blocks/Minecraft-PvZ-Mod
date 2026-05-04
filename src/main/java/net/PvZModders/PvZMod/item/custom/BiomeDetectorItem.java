package net.PvZModders.PvZMod.item.custom;

import com.mojang.datafixers.util.Pair;
import net.PvZModders.PvZMod.menu.BiomeDetectorMenu;
import net.PvZModders.PvZMod.progression.GardenBiomeCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BiomeDetectorItem extends CompassItem {
    private static final String TARGET_TAG = "BiomeDetectorTarget";
    private static final int SEARCH_RADIUS = 6400;
    private static final int HORIZONTAL_STEP = 32;
    private static final int VERTICAL_STEP = 64;

    public BiomeDetectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            Optional<GardenBiomeCategory> target = getTarget(stack);
            if (target.isEmpty() || serverPlayer.isShiftKeyDown()) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new BiomeDetectorMenu(containerId, inventory, stack),
                        Component.literal("Biome Detector")
                ));
            } else {
                refreshDetection(stack, serverPlayer, target.get());
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        getTarget(stack).ifPresentOrElse(
                target -> tooltip.add(colored("Detecting " + target.displayName(), target.color())),
                () -> tooltip.add(Component.literal("Select a biome target"))
        );
    }

    @Override
    public Component getName(ItemStack stack) {
        return getTarget(stack)
                .map(target -> colored("Detecting " + target.displayName(), target.color()))
                .orElse(Component.literal("Biome Detector"));
    }

    public static boolean selectTarget(ItemStack stack, ServerPlayer player, int buttonId) {
        GardenBiomeCategory[] categories = GardenBiomeCategory.values();
        if (buttonId < 0 || buttonId >= categories.length) {
            return false;
        }

        GardenBiomeCategory target = categories[buttonId];
        stack.getOrCreateTag().putString(TARGET_TAG, target.name());
        refreshDetection(stack, player, target);
        player.getInventory().setChanged();
        return true;
    }

    public static Optional<GardenBiomeCategory> getTarget(ItemStack stack) {
        if (!stack.hasTag() || !stack.getOrCreateTag().contains(TARGET_TAG)) {
            return Optional.empty();
        }
        return GardenBiomeCategory.byId(stack.getOrCreateTag().getString(TARGET_TAG));
    }

    private static void refreshDetection(ItemStack stack, ServerPlayer player, GardenBiomeCategory target) {
        ServerLevel level = player.serverLevel();
        Pair<BlockPos, net.minecraft.core.Holder<Biome>> found = level.findClosestBiome3d(
                holder -> holder.unwrapKey().map(target.biomes()::contains).orElse(false),
                player.blockPosition(),
                SEARCH_RADIUS,
                HORIZONTAL_STEP,
                VERTICAL_STEP
        );

        if (found == null) {
            player.displayClientMessage(Component.literal("No " + target.displayName() + " found nearby")
                    .withStyle(style -> style.withColor(TextColor.fromRgb(0xD33F3F))), true);
            return;
        }

        writeCompassTarget(stack, level, found.getFirst());
        player.displayClientMessage(colored("Detecting " + target.displayName(), target.color()), true);
    }

    private static void writeCompassTarget(ItemStack stack, ServerLevel level, BlockPos pos) {
        stack.getOrCreateTag().put("LodestonePos", NbtUtils.writeBlockPos(pos));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, level.dimension())
                .result()
                .ifPresent(tag -> stack.getOrCreateTag().put("LodestoneDimension", tag));
        stack.getOrCreateTag().putBoolean("LodestoneTracked", false);
    }

    private static Component colored(String text, int color) {
        return Component.literal(text).withStyle(style -> style.withColor(TextColor.fromRgb(color)));
    }
}
