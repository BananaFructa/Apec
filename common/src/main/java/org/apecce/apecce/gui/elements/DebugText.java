package org.apecce.apecce.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apecce.apecce.gui.Element;
import org.apecce.apecce.gui.ElementType;
import org.apecce.apecce.skyblock.SkyBlockInfo;
import org.apecce.apecce.utils.ApecUtils;
import org.joml.Vector2f;

import java.awt.*;

public class DebugText extends Element {
    public DebugText() {
        super(ElementType.DEBUG_TEXT);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        if (SkyBlockInfo.getInstance().getScoreboard() != null) {
            ApecUtils.drawOutlineWrappedText(mc, poseStack, SkyBlockInfo.getInstance().getScoreboard().toString(), 2, 30, 300, new Color(170, 84, 255, 255).getRGB());
        }
    }
}
