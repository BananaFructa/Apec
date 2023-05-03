package org.apecce.apecce.mixins.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.apecce.apecce.ApecCE;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public class MixinBossHealthOverlay {


    @Inject(method = "render", at = @At("HEAD"))
    private void render(PoseStack poseStack, CallbackInfo ci) {
        ApecCE.getInstance().getLogger().info("BossHealthOverlay.render");
    }

    @Inject(method = "drawBar(Lcom/mojang/blaze3d/vertex/PoseStack;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"))
    private void drawBar(PoseStack poseStack, int i, int j, net.minecraft.world.BossEvent bossEvent, CallbackInfo ci) {
        i = 100;
        ApecCE.getInstance().getLogger().info("BossHealthOverlay.drawBar");
    }






}
