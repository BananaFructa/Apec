package org.apec.apec.gui.elements.xp;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import org.apec.apec.gui.ApecTextures;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.joml.Vector2f;

public class XPBar extends Element {
    public XPBar() {
        super(ElementType.XP_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        int width = (int) (scaledResolution.x - 190);

        poseStack.pushPose();
        ApecTextures status_bar = ApecTextures.STATUS_BAR;
        RenderSystem.setShaderTexture(0, status_bar.getResourceLocation());

        // Empty Bar
        Gui.blit(poseStack, width, 53, 0, 30, 182, 5, status_bar.getWidth(), status_bar.getHeight());

        // Full Bar
        Gui.blit(poseStack, width, 53, 0, 35, (int) (mc.player.experienceProgress * 182f), 5, status_bar.getWidth(), status_bar.getHeight());
        poseStack.popPose();
    }
}
