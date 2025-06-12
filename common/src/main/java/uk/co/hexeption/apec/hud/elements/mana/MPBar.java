package uk.co.hexeption.apec.hud.elements.mana;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.hud.ApecTextures;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.utils.ApecUtils;

public class MPBar extends Element {

    public MPBar() {
        super(ElementType.MP_BAR);
    }

    @Override
    public void drawText(GuiGraphics graphics, boolean editMode) {

        int mp = Apec.SKYBLOCK_INFO.getPlayerStats().mana();
        int base_mp = Apec.SKYBLOCK_INFO.getPlayerStats().base_mana();
        int overflow = Apec.SKYBLOCK_INFO.getPlayerStats().overflow();
        int base_overflow = Apec.SKYBLOCK_INFO.getPlayerStats().base_overflow();

        float mpFactor = mp > base_mp ? 1 : (float) mp / (float) base_mp;

        boolean showOverflowMana = true;

        ApecTextures mpBarTexture = ApecTextures.STATUS_BAR;
        Vector2f statBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);
        int width = (int) statBar.x;
        int height = (int) statBar.y;

        if (overflow != 0 && showOverflowMana) {
            float opFactor = overflow > base_overflow ? 1 : (float) overflow / (float) base_overflow;

            // Empty Bar
            graphics.blit(RenderType::guiTexturedOverlay, mpBarTexture.getResourceLocation(), width, height, 0, 70, 182, 5, mpBarTexture.getWidth(), mpBarTexture.getHeight());

            // Overflow Bar
            graphics.blit(RenderType::guiTexturedOverlay, mpBarTexture.getResourceLocation(), width, height, 0, 75, (int) (opFactor * 49f), 5, mpBarTexture.getWidth(), mpBarTexture.getHeight());

            // MP Bar
            graphics.blit(RenderType::guiTexturedOverlay, mpBarTexture.getResourceLocation(), width + 51, height, 51, 75, (int) (mpFactor * 131f), 5, mpBarTexture.getWidth(), mpBarTexture.getHeight());
        } else {
            // Empty Bar
            graphics.blit(RenderType::guiTexturedOverlay, mpBarTexture.getResourceLocation(), width, height, 0, 10, 182, 5, mpBarTexture.getWidth(), mpBarTexture.getHeight());

            // Full Bar
            graphics.blit(RenderType::guiTexturedOverlay, mpBarTexture.getResourceLocation(), width, height, 0, 15, (int) (mpFactor * 182f), 5, mpBarTexture.getWidth(), mpBarTexture.getHeight());
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.menu.applyGlobalChanges(this,new Vector2f(mc.getWindow().getGuiScaledWidth() - 190, 34));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(182*scale,5*scale);
    }
}
