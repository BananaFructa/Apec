package org.apec.apec.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.ResourceLocation;
import org.apec.apec.gui.ApecTextures;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.skyblock.SkyBlockInfo;
import org.apec.apec.utils.ApecUtils;
import org.joml.Vector2f;

public class BottomBar extends Element {

    private int yOffset;

    public BottomBar() {
        super(ElementType.BOTTOM_BAR, 5);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        this.scaledResolution = scaledResolution;

        Vector2f bottomBarPos = getAnchorPointPosition();

        ResourceLocation bottomBarTexture = ApecTextures.BOTTOM_BAR.getResourceLocation();
        RenderSystem.setShaderTexture(0, bottomBarTexture);
        RenderSystem.enableBlend();

        int drawCount = (int) (scaledResolution.x / 256) + 1;
        for (int i = 0; i < drawCount; i++) {
            Gui.blit(poseStack, (int) bottomBarPos.x + i * 256, (int) bottomBarPos.y + yOffset, 0, 0, 256, 20, 256, 256);
            if (mc.options.guiScale().get() == 1) {
                Gui.blit(poseStack, (int) bottomBarPos.x + i * 256, (int) (bottomBarPos.y + 13 * scale + (int) yOffset), 0, 0, 256, 20, 256, 256);
            }
        }

    }

    @Override
    public void render(PoseStack poseStack, float delta) {

        if (mc.options.guiScale().get() == 0) {
            this.setScale(0.72f);
        } else if (mc.options.guiScale().get() == 3) {
            this.setScale(0.8f);
        } else if (mc.options.guiScale().get() == 2) {
            this.setScale(1f);
        } else if (mc.options.guiScale().get() == 1) {
            this.setScale(1.5f);
        }

        poseStack.scale(scale, scale, 1);
        boolean useIcons = false; //todo: make this a setting
        boolean animateBar = true; //todo: make this a setting

        boolean isChatOpen = mc.screen instanceof ChatScreen;
        float fps = mc.getFps();
        if (animateBar) {
            if (isChatOpen && yOffset < 40) {
                yOffset += 1 * (120f / fps);
            }
            if (!isChatOpen && yOffset > 0) {
                yOffset -= 1 * (120f / fps);
            }
        } else {
            yOffset = 0;
        }
        if (yOffset > 40) yOffset = 40;
        if (yOffset < 0) yOffset = 0;

        Vector2f BottomBarPos = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);
        BottomBarPos.y += yOffset + 6 * scale;

        String purse = SkyBlockInfo.getInstance().getScoreboard().purse().split(" ")[0];
        String purseValue = SkyBlockInfo.getInstance().getScoreboard().purse().split(" ")[1];
        String purseText = purse + ChatFormatting.GOLD + " " + purseValue;
        mc.font.draw(poseStack, purseText, (int) BottomBarPos.x + 10, (int) BottomBarPos.y, 0xffffff);

        String bits = SkyBlockInfo.getInstance().getScoreboard().bits().split(" ")[0];
        String bitsValue = SkyBlockInfo.getInstance().getScoreboard().bits().split(" ")[1];
        String bitsText = bits + ChatFormatting.AQUA + " " + bitsValue;
        mc.font.draw(poseStack, bitsText, (int) BottomBarPos.x + 100, (int) BottomBarPos.y, 0xffffff);

        String zone = SkyBlockInfo.getInstance().getScoreboard().zone();
        mc.font.draw(poseStack, zone, (int) BottomBarPos.x + 190, (int) BottomBarPos.y, 0x55FF55);

        String defence = String.valueOf(SkyBlockInfo.getInstance().getPlayerStats().defense()) + (useIcons ? "" : " Defense");
        mc.font.draw(poseStack, defence, (int) BottomBarPos.x + 280, (int) BottomBarPos.y, 0x55FF55);

        String date = SkyBlockInfo.getInstance().getScoreboard().date() + " " + SkyBlockInfo.getInstance().getScoreboard().Hour();
        mc.font.draw(poseStack, date, (int) scaledResolution.x - mc.font.width(date) - 10, (int) BottomBarPos.y, 0xffffff);

    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(0, scaledResolution.y - 20 * scale);
    }
}
