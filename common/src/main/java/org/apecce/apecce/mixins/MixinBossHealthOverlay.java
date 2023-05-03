package org.apecce.apecce.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.network.chat.Component;
import org.apecce.apecce.ApecCE;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

// Demo
@Mixin(BossHealthOverlay.class)
public class MixinBossHealthOverlay {


    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(PoseStack poseStack, CallbackInfo ci) {
        ApecCE.getInstance().getLogger().info("BossHealthOverlay.render");
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lcom/mojang/blaze3d/vertex/PoseStack;IILnet/minecraft/world/BossEvent;)V"))
    private void modifyDrawBar(Args args) {
        // X
        args.set(1, 20);

        // Y
        args.set(2, 20);
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;FFI)I"))
    private void modifyDrawShadow(Args args) {

        int scaledWidth = this.minecraft.getWindow().getGuiScaledWidth();
        Component component = args.get(1);
        int textWidth = this.minecraft.font.width(component);

        //int x = scaledWidth / 2 - textWidth / 2 - 100;
        //int y = 12 - 9;
        int x = 20;
        int y = 20 - 9;

        // X
        args.set(2, (float) x);

        // Y
        args.set(3, (float) y);
    }


}
