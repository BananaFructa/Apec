package org.apec.apec.gui.elements.air;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import org.apec.apec.gui.ApecTextures;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.utils.ApecUtils;
import org.joml.Vector2f;

public class AirBar extends Element {

    public AirBar() {
        super(ElementType.AIR_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        this.scaledResolution = scaledResolution;
        int width = (int) (scaledResolution.x - 190);

        poseStack.pushPose();
        ApecTextures status_bar = ApecTextures.STATUS_BAR;
        RenderSystem.setShaderTexture(0, status_bar.getResourceLocation());
        poseStack.scale(scale, scale, scale);

        if (mc.player.isUnderWater()) {

            float air = mc.player.getAirSupply() * 0.00333f;
            if (air < 0) {
                air = 0;
            }

            Vector2f airBar = ApecUtils.scalarMultiply(this.getCurrentAnchorPoint(), 1f / scale);

            // Empty Bar
            Gui.blit(poseStack, (int) airBar.x, (int) airBar.y, 0, 40, 182, 5, status_bar.getWidth(), status_bar.getHeight());

            // Full Bar
            Gui.blit(poseStack, (int) airBar.x, (int) airBar.y, 0, 45, (int) (air * 182f), 5, status_bar.getWidth(), status_bar.getHeight());
            poseStack.popPose();
        }
    }


    @Override
    public Vector2f getAnchorPointPosition() {
        return this.manager.applyGlobalChanges(this, new Vector2f((scaledResolution.x - 190), 72));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(182 * scale, 5 * scale);
    }

}
