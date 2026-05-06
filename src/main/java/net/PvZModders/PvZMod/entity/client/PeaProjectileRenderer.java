package net.PvZModders.PvZMod.entity.client;

import net.PvZModders.PvZMod.entity.custom.PeaProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class PeaProjectileRenderer extends ThrownItemRenderer<PeaProjectileEntity> {
    public PeaProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, 1.25F, true);
    }
}
