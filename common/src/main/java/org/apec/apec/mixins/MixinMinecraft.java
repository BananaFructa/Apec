package org.apec.apec.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.apec.apec.Apec;
import org.apec.apec.events.ClientTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(GameConfig gameConfig, CallbackInfo ci) {
        if (Apec.getInstance().getModLoader() == Apec.ModLoader.FORGE) {
            // Forge uses another class for the gui
            return;
        }
//        this.gui = new GuiApec((Minecraft) (Object) this, this.getItemRenderer());
        Apec.getInstance().postInit(); // Post init after the window is created and rendering api is initialized
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        Apec.getInstance().getEventBus().post(new ClientTick());
    }

}
