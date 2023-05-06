package org.apec.apec.mixins;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.apec.apec.Apec;
import org.apec.apec.events.Render2D;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinInGameHud {


    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(PoseStack poseStack, float f, CallbackInfo ci) {
        Window window = this.minecraft.getWindow();
        Render2D event = new Render2D(window.getGuiScaledWidth(), window.getGuiScaledHeight(), poseStack, f);
        Apec.getInstance().getEventBus().post(event);
    }

}
