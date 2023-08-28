package org.apec.apec.mixins;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import org.apec.apec.Apec;
import org.apec.apec.events.Render2D;
import org.apec.apec.skyblock.SkyBlockInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinInGameHud extends GuiComponent {


    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int screenWidth;

    @Shadow
    protected abstract void renderHotbar(float f, PoseStack poseStack);


    @Shadow protected abstract void renderPlayerHealth(PoseStack poseStack);

    @Shadow public abstract void renderExperienceBar(PoseStack poseStack, int i);

    @Inject(method = "render", at = @At("HEAD"))
    private void render(PoseStack poseStack, float f, CallbackInfo ci) {
        Window window = this.minecraft.getWindow();
        Render2D event = new Render2D(window.getGuiScaledWidth(), window.getGuiScaledHeight(), poseStack, f);
        Apec.getInstance().getEventBus().post(event);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void _renderHotbar(Gui gui, float f, PoseStack poseStack) {
        if(SkyBlockInfo.getInstance().isOnSkyblock()) {
            poseStack.pushPose();
            poseStack.translate((float) (this.screenWidth / 2) - 92, -20, 0.0F);
            this.renderHotbar(f, poseStack);
            poseStack.popPose();
        } else {
            this.renderHotbar(f, poseStack);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void _renderPlayerHealth(Gui gui, PoseStack poseStack) {
       if(!SkyBlockInfo.getInstance().isOnSkyblock()) {
           // Only render the player health if we are not on skyblock
           this.renderPlayerHealth(poseStack);
       }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"))
    private void _renderExperienceBar(Gui gui, PoseStack poseStack, int i) {
        if(!SkyBlockInfo.getInstance().isOnSkyblock()) {
            // Only render the experience bar if we are not on skyblock
            this.renderExperienceBar(poseStack, i);
        }
    }
}
