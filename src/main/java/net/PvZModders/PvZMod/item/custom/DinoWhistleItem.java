package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.entity.custom.JurassicDinosaurEntity;
import net.PvZModders.PvZMod.progression.dinosaur.DinosaurType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class DinoWhistleItem extends Item {
    private static final String ACTIVE_DINO_TAG = "PvZActiveDinosaurPet";
    private static final String SELECTED_DINO_TAG = "PvZSelectedDinosaurPet";

    public DinoWhistleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        CompoundTag playerData = player.getPersistentData();
        if (playerData.hasUUID(ACTIVE_DINO_TAG)) {
            UUID activeId = playerData.getUUID(ACTIVE_DINO_TAG);
            Entity active = serverLevel.getEntity(activeId);
            if (active instanceof JurassicDinosaurEntity dinosaur && dinosaur.isAlive()) {
                dinosaur.discard();
                playerData.remove(ACTIVE_DINO_TAG);
                player.displayClientMessage(Component.literal("Dinosaur pet recalled.").withStyle(ChatFormatting.GREEN), true);
                return InteractionResultHolder.consume(stack);
            }
            playerData.remove(ACTIVE_DINO_TAG);
        }

        DinosaurType type = DinosaurType.byName(playerData.getString(SELECTED_DINO_TAG));
        Vec3 spawnPos = player.position().add(player.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize().scale(2.5D));
        JurassicDinosaurEntity dinosaur = new JurassicDinosaurEntity(serverLevel, spawnPos.x, player.getY(), spawnPos.z, type);
        dinosaur.makePet(player);
        if (!serverLevel.addFreshEntity(dinosaur)) {
            return InteractionResultHolder.fail(stack);
        }

        playerData.putUUID(ACTIVE_DINO_TAG, dinosaur.getUUID());
        player.displayClientMessage(Component.literal("Dinosaur pet summoned: " + type.displayName()).withStyle(ChatFormatting.GREEN), true);
        return InteractionResultHolder.consume(stack);
    }
}
