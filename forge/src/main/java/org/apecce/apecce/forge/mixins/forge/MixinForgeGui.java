package org.apecce.apecce.forge.mixins.forge;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.apecce.apecce.ApecCE;
import org.apecce.apecce.events.Render2D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class MixinForgeGui {

    @Shadow
    public abstract Minecraft getMinecraft();

    @Inject(method = "render", at = @At("HEAD"))
    public void render(PoseStack poseStack, float f, CallbackInfo ci) {
        Window window = this.getMinecraft().getWindow();
        Render2D event = new Render2D(window.getGuiScaledWidth(), window.getGuiScaledHeight(), poseStack, f);
        ApecCE.getInstance().getEventBus().post(event);
    }

}
