package org.apec.apec.gui.elements.mana;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import org.apec.apec.gui.ApecTextures;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.skyblock.SkyBlockInfo;
import org.joml.Vector2f;

public class MPBar extends Element {

    public MPBar() {
        super(ElementType.MP_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        int width = (int) (scaledResolution.x - 190);

        int mp = SkyBlockInfo.getInstance().getPlayerStats().mana();
        int base_mp = SkyBlockInfo.getInstance().getPlayerStats().base_mana();
        int overflow = SkyBlockInfo.getInstance().getPlayerStats().overflow();
        int base_overflow = SkyBlockInfo.getInstance().getPlayerStats().base_overflow();

        float mpFactor = mp > base_mp ? 1 : (float) mp / (float) base_mp;

        boolean showOverflowMana = true;

        poseStack.pushPose();
        ApecTextures status_bar = ApecTextures.STATUS_BAR;
        RenderSystem.setShaderTexture(0, status_bar.getResourceLocation());

        if (overflow != 0 && showOverflowMana) {
            float opFactor = overflow > base_overflow ? 1 : (float) overflow / (float) base_overflow;

            // Empty Bar
            Gui.blit(poseStack, width, 34, 0, 70, 182, 5, status_bar.getWidth(), status_bar.getHeight());

            // Overflow Bar
            Gui.blit(poseStack, width, 34, 0, 75, (int) (opFactor * 49f), 5, status_bar.getWidth(), status_bar.getHeight());

            // MP Bar
            Gui.blit(poseStack, width + 51, 34, 51, 75, (int) (mpFactor * 131f), 5, status_bar.getWidth(), status_bar.getHeight());
        } else {
            // Empty Bar
            Gui.blit(poseStack, width, 34, 0, 10, 182, 5, status_bar.getWidth(), status_bar.getHeight());

            // Full Bar
            Gui.blit(poseStack, width, 34, 0, 15, (int) (mpFactor * 182f), 5, status_bar.getWidth(), status_bar.getHeight());
        }

        poseStack.popPose();

    }
}
