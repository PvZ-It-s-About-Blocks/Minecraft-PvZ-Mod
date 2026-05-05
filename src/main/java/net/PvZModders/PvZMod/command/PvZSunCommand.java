package net.PvZModders.PvZMod.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PvZSunCommand {
    private PvZSunCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("pvzsun")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    SunManager.setSun(player, amount);
                                    SunManager.syncSunBar(player);
                                    context.getSource().sendSuccess(() -> Component.literal("Set Sun to " + SunManager.getSun(player)).withStyle(ChatFormatting.GOLD), true);
                                    return 1;
                                })))
                .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    SunManager.addSun(player, amount);
                                    context.getSource().sendSuccess(() -> Component.literal("Sun is now " + SunManager.getSun(player)).withStyle(ChatFormatting.GOLD), true);
                                    return 1;
                                })))
                .then(Commands.literal("cap")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(SunManager.DEFAULT_SUN_VALUE))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    SunManager.setSunCap(player, amount);
                                    SunManager.syncSunBar(player);
                                    context.getSource().sendSuccess(() -> Component.literal("Set Sun cap to " + SunManager.getSunCap(player)).withStyle(ChatFormatting.GOLD), true);
                                    return 1;
                                })))
                .then(Commands.literal("resetcap")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            SunManager.setSunCap(player, SunManager.DEFAULT_SUN_CAP);
                            SunManager.syncSunBar(player);
                            context.getSource().sendSuccess(() -> Component.literal("Reset Sun cap to " + SunManager.DEFAULT_SUN_CAP).withStyle(ChatFormatting.GOLD), true);
                            return 1;
                        })));
    }
}
