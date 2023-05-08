package org.apec.apec.gui.elements.air;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import org.apec.apec.gui.ApecTextures;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.joml.Vector2f;

public class AirBar extends Element {

    public AirBar() {
        super(ElementType.AIR_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        int width = (int) (scaledResolution.x - 190);

        poseStack.pushPose();
        ApecTextures status_bar = ApecTextures.STATUS_BAR;
        RenderSystem.setShaderTexture(0, status_bar.getResourceLocation());

        if (mc.player.isUnderWater()) {

            float air = mc.player.getAirSupply() * 0.00333f;
            if (air < 0) {
                air = 0;
            }

            // Empty Bar
            Gui.blit(poseStack, width, 72, 0, 40, 182, 5, status_bar.getWidth(), status_bar.getHeight());

            // Full Bar
            Gui.blit(poseStack, width, 72, 0, 45, (int) (air * 182f), 5, status_bar.getWidth(), status_bar.getHeight());
            poseStack.popPose();
        }
    }
}
