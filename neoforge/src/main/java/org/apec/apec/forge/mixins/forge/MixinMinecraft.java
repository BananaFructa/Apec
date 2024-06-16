package org.apec.apec.forge.mixins.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.apec.apec.Apec;
import org.apec.apec.forge.gui.GuiApecForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    @Final
    @Mutable
    public Gui gui;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(CallbackInfo ci) {
        this.gui = new GuiApecForge((Minecraft) (Object) this);
        Apec.getInstance().postInit(); // Post init after the window is created and rendering api is initialized
    }
}
