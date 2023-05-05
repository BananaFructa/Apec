package org.apecce.apecce.mixins;

import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.apecce.apecce.ApecCE;
import org.apecce.apecce.events.ChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatListener.class)
public class MixinChatListener {

    @Inject(method = "handleSystemMessage", at = @At("HEAD"))
    private void handleSystemMessage(Component component, boolean bl, CallbackInfo ci) {
        ChatMessage chatMessage = new ChatMessage(component, bl);
        ApecCE.getInstance().getEventBus().post(chatMessage);
    }
}
