package org.apec.apec.gui.elements.health;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import org.apec.apec.gui.ApecTextures;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.skyblock.SkyBlockInfo;
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
        int ap = SkyBlockInfo.getInstance().getPlayerStats().absorption();
        int base_ap = SkyBlockInfo.getInstance().getPlayerStats().base_absorption();

        float hpFactor = (hp > base_hp) ? 1 : (float) hp / (float) base_hp;

        poseStack.pushPose();
        ApecTextures status_bar = ApecTextures.STATUS_BAR;
        RenderSystem.setShaderTexture(0, status_bar.getResourceLocation());

        boolean showAPBar = false;
        if (showAPBar) {
            // Empty Bar
            Gui.blit(poseStack, width, 15, 0, 60, 182, 5, status_bar.getWidth(), status_bar.getHeight());

            // AP Bar
            Gui.blit(poseStack, width, 15, 0, 65, (int) (((float) ap / (float) base_ap) * 49f), 5, status_bar.getWidth(), status_bar.getHeight());

            // HP Bar
            Gui.blit(poseStack, width + 51, 15, 51, 65, (int) (hpFactor * 131f), 5, status_bar.getWidth(), status_bar.getHeight());
        } else {
            // Empty Bar
            Gui.blit(poseStack, width, 15, 0, 0, 182, 5, status_bar.getWidth(), status_bar.getHeight());

            // Full Bar
            Gui.blit(poseStack, width, 15, 0, 5, (int) (hpFactor * 182f), 5, status_bar.getWidth(), status_bar.getHeight());
        }

        poseStack.popPose();
    }
}
