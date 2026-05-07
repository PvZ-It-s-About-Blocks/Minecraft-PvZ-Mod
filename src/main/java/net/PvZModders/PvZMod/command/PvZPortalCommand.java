package net.PvZModders.PvZMod.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.portal.GardenEyeType;
import net.PvZModders.PvZMod.progression.portal.GardenPortalActivationHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PvZPortalCommand {
    private PvZPortalCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("pvzeye")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("give")
                        .then(Commands.argument("eye_type", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        GardenEyeType.REQUIRED.stream().map(GardenEyeType::eyeId), builder))
                                .executes(context -> giveEye(context.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(context, "eye_type")))))
                .then(Commands.literal("giveall")
                        .executes(context -> giveAllEyes(context.getSource().getPlayerOrException()))));

        event.getDispatcher().register(Commands.literal("pvzportal")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("create")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            BlockPos center = BlockPos.containing(player.position()).below();
                            GardenPortalActivationHandler.createPortal(player.serverLevel(), center, false);
                            context.getSource().sendSuccess(() -> Component.literal("Created a Garden Portal frame ring at " + center.toShortString()).withStyle(ChatFormatting.LIGHT_PURPLE), true);
                            return 1;
                        }))
                .then(Commands.literal("reset")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            boolean reset = GardenPortalActivationHandler.resetNearestPortal(player.serverLevel(), player.blockPosition());
                            context.getSource().sendSuccess(() -> Component.literal(reset ? "Reset the nearest Garden Portal." : "No Garden Portal found nearby.").withStyle(reset ? ChatFormatting.YELLOW : ChatFormatting.RED), true);
                            return reset ? 1 : 0;
                        }))
                .then(Commands.literal("fill")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            boolean filled = GardenPortalActivationHandler.fillNearestPortal(player.serverLevel(), player.blockPosition());
                            context.getSource().sendSuccess(() -> Component.literal(filled ? "Filled and checked the nearest Garden Portal." : "No Garden Portal found nearby.").withStyle(filled ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.RED), true);
                            return filled ? 1 : 0;
                        }))
                .then(Commands.literal("status")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            return reportStatus(context.getSource().getLevel(), player.blockPosition(), message -> context.getSource().sendSuccess(() -> message, false));
                        }))
                .then(Commands.literal("convert")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            var converted = GardenPortalActivationHandler.convertNearestVanillaPortal(player.serverLevel(), player.blockPosition());
                            context.getSource().sendSuccess(() -> converted
                                    .<Component>map(pos -> Component.literal("Converted vanilla portal frames at " + pos.toShortString() + ".").withStyle(ChatFormatting.LIGHT_PURPLE))
                                    .orElse(Component.literal("No vanilla End Portal frame ring found nearby.").withStyle(ChatFormatting.RED)), true);
                            return converted.isPresent() ? 1 : 0;
                        })));
    }

    private static int giveEye(ServerPlayer player, String id) {
        return GardenEyeType.byEyeId(id).map(type -> {
            give(player, new ItemStack(ModItems.GARDEN_EYES.get(type).get()));
            player.sendSystemMessage(Component.literal("Gave " + type.eyeDisplayName() + ".").withStyle(ChatFormatting.LIGHT_PURPLE));
            return 1;
        }).orElseGet(() -> {
            player.sendSystemMessage(Component.literal("Unknown Garden Eye: " + id).withStyle(ChatFormatting.RED));
            return 0;
        });
    }

    private static int giveAllEyes(ServerPlayer player) {
        for (GardenEyeType type : GardenEyeType.REQUIRED) {
            give(player, new ItemStack(ModItems.GARDEN_EYES.get(type).get()));
        }
        player.sendSystemMessage(Component.literal("Gave all Garden Eyes.").withStyle(ChatFormatting.LIGHT_PURPLE));
        return GardenEyeType.REQUIRED.size();
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static int reportStatus(ServerLevel level, BlockPos near, java.util.function.Consumer<Component> responder) {
        var status = GardenPortalActivationHandler.getNearestStatus(level, near);
        if (status.isEmpty()) {
            responder.accept(Component.literal("No Garden Portal found nearby.").withStyle(ChatFormatting.RED));
            return 0;
        }
        GardenPortalActivationHandler.PortalStatus portalStatus = status.get();
        responder.accept(Component.literal("Garden Eyes placed: " + portalStatus.filled() + "/12" + (portalStatus.ready() ? " - ready." : "."))
                .withStyle(portalStatus.ready() ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.YELLOW));
        return 1;
    }
}
