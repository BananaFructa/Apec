package org.apec.apec.gui.elements.air;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.utils.ApecUtils;
import org.joml.Vector2f;

public class AirText extends Element {

    public AirText() {
        super(ElementType.AIR_TEXT);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {

        if(mc.player.isUnderWater()) {
            float air = (mc.player.getAirSupply() / 300f) * 100f;
            if(air < 0) {
                air = 0;
            }
            String airText = (int) air + "% Air";
            ApecUtils.drawOutlineText(mc, poseStack, airText, (int) (scaledResolution.x - mc.font.width(airText) - 5), 62, 0x80ff20);
        }


    }
}
