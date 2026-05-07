package net.PvZModders.PvZMod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PvZAliasCommand {
    private PvZAliasCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        registerVanillaSummonAliases(dispatcher);
        registerVanillaGiveAliases(dispatcher);
        registerFallbackCommands(dispatcher);
    }

    private static void registerVanillaSummonAliases(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandNode<CommandSourceStack> summonNode = dispatcher.getRoot().getChild("summon");
        if (summonNode == null) {
            return;
        }

        for (String entityId : pvzEntities().keySet()) {
            summonNode.addChild(Commands.literal(entityId)
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> summonAliasEntity(context, entityId, context.getSource().getPosition()))
                    .then(Commands.argument("pos", Vec3Argument.vec3())
                            .executes(context -> summonAliasEntity(context, entityId, Vec3Argument.getVec3(context, "pos"))))
                    .build());
        }
    }

    private static void registerVanillaGiveAliases(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandNode<CommandSourceStack> giveNode = dispatcher.getRoot().getChild("give");
        if (giveNode == null) {
            return;
        }

        CommandNode<CommandSourceStack> targetsNode = giveNode.getChild("targets");
        for (String itemId : pvzItems().keySet()) {
            giveNode.addChild(Commands.literal(itemId)
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> giveAliasItemToSource(context, itemId, 1))
                    .then(Commands.argument("count", IntegerArgumentType.integer(1))
                            .executes(context -> giveAliasItemToSource(context, itemId, IntegerArgumentType.getInteger(context, "count"))))
                    .build());

            if (targetsNode != null) {
                targetsNode.addChild(Commands.literal(itemId)
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> giveAliasItem(context, EntityArgument.getPlayers(context, "targets"), itemId, 1))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                .executes(context -> giveAliasItem(context, EntityArgument.getPlayers(context, "targets"), itemId, IntegerArgumentType.getInteger(context, "count"))))
                        .build());
            }
        }
    }

    private static void registerFallbackCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pvzsummon")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("entity", StringArgumentType.word())
                        .suggests(PvZAliasCommand::suggestPvZEntities)
                        .executes(context -> summonAliasEntity(context, StringArgumentType.getString(context, "entity"), context.getSource().getPosition()))
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .executes(context -> summonAliasEntity(context, StringArgumentType.getString(context, "entity"), Vec3Argument.getVec3(context, "pos"))))));

        dispatcher.register(Commands.literal("pvzgive")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("item", StringArgumentType.word())
                        .suggests(PvZAliasCommand::suggestPvZItems)
                        .executes(context -> giveAliasItemToSource(context, StringArgumentType.getString(context, "item"), 1))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                .executes(context -> giveAliasItemToSource(context, StringArgumentType.getString(context, "item"), IntegerArgumentType.getInteger(context, "count"))))));
    }

    private static int summonAliasEntity(CommandContext<CommandSourceStack> context, String entityId, Vec3 pos) throws CommandSyntaxException {
        EntityType<?> type = pvzEntities().get(entityId);
        if (type == null) {
            context.getSource().sendFailure(Component.literal("Unknown PvZ entity: " + entityId));
            return 0;
        }

        ServerLevel level = context.getSource().getLevel();
        Entity entity = type.create(level);
        if (entity == null) {
            context.getSource().sendFailure(Component.literal("Could not create PvZ entity: " + entityId));
            return 0;
        }

        entity.moveTo(pos.x, pos.y, pos.z, context.getSource().getRotation().y, 0.0F);
        if (entity instanceof Mob mob) {
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);
        }
        level.addFreshEntity(entity);
        context.getSource().sendSuccess(() -> Component.literal("Summoned " + entityId), true);
        return 1;
    }

    private static int giveAliasItemToSource(CommandContext<CommandSourceStack> context, String itemId, int count) throws CommandSyntaxException {
        return giveAliasItem(context, java.util.List.of(context.getSource().getPlayerOrException()), itemId, count);
    }

    private static int giveAliasItem(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players, String itemId, int count) {
        Item item = pvzItems().get(itemId);
        if (item == null) {
            context.getSource().sendFailure(Component.literal("Unknown PvZ item: " + itemId));
            return 0;
        }

        for (ServerPlayer player : players) {
            ItemStack stack = new ItemStack(item, count);
            boolean stored = player.getInventory().add(stack);
            if (!stored || !stack.isEmpty()) {
                player.drop(stack, false);
            }
            player.inventoryMenu.broadcastChanges();
        }

        context.getSource().sendSuccess(() -> Component.literal("Gave " + count + " " + itemId + " to " + players.size() + " player(s)"), true);
        return players.size();
    }

    private static CompletableFuture<Suggestions> suggestPvZEntities(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(pvzEntities().keySet(), builder);
    }

    private static CompletableFuture<Suggestions> suggestPvZItems(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(pvzItems().keySet(), builder);
    }

    private static Map<String, EntityType<?>> pvzEntities() {
        Map<String, EntityType<?>> entities = new TreeMap<>();
        ForgeRegistries.ENTITY_TYPES.getEntries().forEach(entry -> {
            if (PvZ2Mod.MOD_ID.equals(entry.getKey().location().getNamespace())) {
                entities.put(entry.getKey().location().getPath(), entry.getValue());
            }
        });
        return entities;
    }

    private static Map<String, Item> pvzItems() {
        Map<String, Item> items = new TreeMap<>();
        ForgeRegistries.ITEMS.getEntries().forEach(entry -> {
            if (PvZ2Mod.MOD_ID.equals(entry.getKey().location().getNamespace())) {
                items.put(entry.getKey().location().getPath(), entry.getValue());
            }
        });
        return items;
    }
}
