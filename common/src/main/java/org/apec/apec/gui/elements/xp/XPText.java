package org.apec.apec.gui.elements.xp;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.utils.ApecUtils;
import org.joml.Vector2f;

public class XPText extends Element {

    public XPText() {
        super(ElementType.XP_TEXT);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {

        String xpText = "Lvl " + mc.player.experienceLevel + " XP";

        ApecUtils.drawOutlineText(mc, poseStack, xpText, (int) (scaledResolution.x - mc.font.width(xpText) - 5), 43, 0x80ff20);

    }
}
