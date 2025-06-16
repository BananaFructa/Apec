package uk.co.hexeption.apec.mixins;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.MC;

@Mixin(Gui.class)
public class MixinGui implements MC {

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void renderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void renderHearts(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderExperienceBar(GuiGraphics guiGraphics, int i, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void renderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void renderArmor(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void renderFood(GuiGraphics guiGraphics, Player player, int i, int j, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }

        ci.cancel();
    }

    @Inject(method = "renderItemHotbar", at = @At("HEAD"))
    private void renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }

        var width = mc.getWindow().getGuiScaledWidth();

        var translationX = width / 2f - 90 - 5;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(translationX, -25, 100);
    }

    @Inject(method = "renderItemHotbar", at = @At("RETURN"))
    private void renderItemHotbarReturn(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }
        guiGraphics.pose().popPose();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {

        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }

        Apec.apecMenu.render(guiGraphics);

    }

}
