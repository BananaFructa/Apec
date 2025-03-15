package uk.co.hexeption.apec.mixins;

import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.settings.menu.SettingsMenu;


@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler implements MC {

    @Inject(method = "keyPress", at = @At(value = "HEAD"))
    private void keyPress(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (i == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            mc.setScreen(new SettingsMenu(0));
        }
    }
}
