package uk.co.hexeption.apec.hud.elements.xp;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector2f;
import uk.co.hexeption.apec.hud.ApecTextures;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.utils.ApecUtils;

public class XPBar extends Element {

    public XPBar() {
        super(ElementType.XP_BAR);
    }

    @Override
    public void drawText(GuiGraphics graphics, boolean editMode) {

        ApecTextures xpBarTexture = ApecTextures.STATUS_BAR;

        Vector2f statBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);
        int width = (int) statBar.x;
        int height = (int) statBar.y;

        // Empty Bar
        graphics.blit(RenderType::guiTexturedOverlay, xpBarTexture.getResourceLocation(), width, height, 0, 30, 182, 5, xpBarTexture.getWidth(), xpBarTexture.getHeight());

        // Full Bar
        graphics.blit(RenderType::guiTexturedOverlay, xpBarTexture.getResourceLocation(), width, height, 0, 35, (int) (mc.player.experienceProgress * 182f), 5, xpBarTexture.getWidth(), xpBarTexture.getHeight());
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.menu.applyGlobalChanges(this, new Vector2f(mc.getWindow().getGuiScaledWidth() - 190, 53));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(182 * scale, 5 * scale);
    }
}
