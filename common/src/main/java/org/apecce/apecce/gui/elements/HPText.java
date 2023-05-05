package org.apecce.apecce.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apecce.apecce.gui.Element;
import org.apecce.apecce.gui.ElementType;
import org.apecce.apecce.skyblock.SkyBlockInfo;
import org.apecce.apecce.utils.ApecUtils;
import org.joml.Vector2f;

public class HPText extends Element {

    public HPText() {
        super(ElementType.HP_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {

        String hp = SkyBlockInfo.getInstance().getPlayerStats().hp() + "/" + SkyBlockInfo.getInstance().getPlayerStats().base_hp() + " HP";

        int width = (int) (scaledResolution.x - mc.font.width(hp) - 5);
        ApecUtils.drawOutlineText(mc, poseStack, hp, width, 5, 0xd10808);
    }
}
