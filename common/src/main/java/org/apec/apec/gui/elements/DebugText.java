package org.apec.apec.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.skyblock.SkyBlockInfo;
import org.apec.apec.utils.ApecUtils;
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
            ApecUtils.drawOutlineWrappedText(mc, poseStack, SkyBlockInfo.getInstance().getPlayerStats().toString(), 2, 80, 300, new Color(84, 255, 170, 255).getRGB());
        }
    }
}
