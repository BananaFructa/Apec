package org.apecce.apecce.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import org.apecce.apecce.gui.ApecTextures;
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
        int width = (int) (scaledResolution.x - 190);

        int hp = SkyBlockInfo.getInstance().getPlayerStats().hp();
        int base_hp = SkyBlockInfo.getInstance().getPlayerStats().base_hp();

        float hpFactor = (hp > base_hp) ? 1 : (float) hp / (float) base_hp;

        poseStack.pushPose();
        ApecTextures status_bar = ApecTextures.STATUS_BAR;
        RenderSystem.setShaderTexture(0, status_bar.getResourceLocation());
        // Empty Bar
        Gui.blit(poseStack, width, 15, 0, 0, 182, 5, status_bar.getWidth(), status_bar.getHeight());

        // Full Bar
        Gui.blit(poseStack, width, 15, 0, 5, (int) (hpFactor * 182f), 5, status_bar.getWidth(), status_bar.getHeight());
        poseStack.popPose();
    }
}
