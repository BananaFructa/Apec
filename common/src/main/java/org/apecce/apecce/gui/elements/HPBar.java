package org.apecce.apecce.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apecce.apecce.ApecCE;
import org.apecce.apecce.gui.Element;
import org.apecce.apecce.gui.ElementType;
import org.apecce.apecce.skyblock.SkyBlockInfo;
import org.joml.Vector2f;

public class HPBar extends Element {

    public HPBar() {
        super(ElementType.HP_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
//        ApecCE.getInstance().getLogger().info("Is on skyblock: " + SkyBlockInfo.getInstance().isOnSkyblock());
        mc.font.drawShadow(poseStack, "Shard: " + SkyBlockInfo.getInstance().getScoreboard().serverShard(), 0, 30, 0xFFFFFF);
    }
}
