package org.apecce.apecce.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apecce.apecce.gui.Element;
import org.apecce.apecce.gui.ElementType;
import org.joml.Vector2f;

public class HPBar extends Element {

    public HPBar() {
        super(ElementType.HP_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        mc.font.drawShadow(poseStack, "HP: " + mc.player.getHealth(), 0, 30, 0xFFFFFF);
    }
}
