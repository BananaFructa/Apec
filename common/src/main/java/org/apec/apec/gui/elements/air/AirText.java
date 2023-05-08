package org.apec.apec.gui.elements.air;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.utils.ApecUtils;
import org.joml.Vector2f;

public class AirText extends Element {

    private int airTextWidth;

    public AirText() {
        super(ElementType.AIR_TEXT);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        this.scaledResolution = scaledResolution;

        if (mc.player.isUnderWater()) {
            float air = (mc.player.getAirSupply() / 300f) * 100f;
            if (air < 0) {
                air = 0;
            }

            Vector2f airtextPos = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);

            String airText = (int) air + "% Air";
            this.airTextWidth = mc.font.width(airText);
            ApecUtils.drawOutlineText(mc, poseStack, airText, (int) (airtextPos.x - airTextWidth), (int) airtextPos.y - 10, 0x8ba6b2);
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return manager.applyGlobalChanges(this, new Vector2f(scaledResolution.x - 190 + 112 + 70, 72));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-airTextWidth * scale, -11 * scale);
    }
}
