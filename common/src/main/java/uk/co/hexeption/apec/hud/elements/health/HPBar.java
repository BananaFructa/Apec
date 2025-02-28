package uk.co.hexeption.apec.hud.elements.health;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.hud.ApecTextures;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.utils.ApecUtils;

public class HPBar extends Element {
    public HPBar() {
        super(ElementType.HP_BAR);
    }

    @Override
    public void drawText(GuiGraphics graphics, Vector2f scaledResolution, boolean editMode) {
        int hp = Apec.SKYBLOCK_INFO.getPlayerStats().hp();
        int base_hp = Apec.SKYBLOCK_INFO.getPlayerStats().base_hp();
        int ap = Apec.SKYBLOCK_INFO.getPlayerStats().absorption();
        int base_ap = Apec.SKYBLOCK_INFO.getPlayerStats().base_absorption();

        float hpFactor = (hp > base_hp) ? 1 : (float) hp / (float) base_hp;

        ApecTextures hpBarTexture = ApecTextures.STATUS_BAR;
        Vector2f statBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);
        int width = (int) statBar.x;
        int height = (int) statBar.y;

        boolean showAPBar = false;
        if (showAPBar) {
            // Empty Bar
            graphics.blit(RenderType::guiTexturedOverlay, hpBarTexture.getResourceLocation(), width, height, 0, 60, 182, 5, hpBarTexture.getWidth(), hpBarTexture.getHeight());

            // AP Bar
            graphics.blit(RenderType::guiTexturedOverlay, hpBarTexture.getResourceLocation(), width, height, 0, 65, (int) (((float) ap / (float) base_ap) * 49f), 5, hpBarTexture.getWidth(), hpBarTexture.getHeight());

            // HP Bar
            graphics.blit(RenderType::guiTexturedOverlay, hpBarTexture.getResourceLocation(), width + 51, height, 51, 65, (int) (hpFactor * 131f), 5, hpBarTexture.getWidth(), hpBarTexture.getHeight());
        } else {
            // Empty Bar
            graphics.blit(RenderType::guiTexturedOverlay, hpBarTexture.getResourceLocation(), width, height, 0, 0, 182, 5, hpBarTexture.getWidth(), hpBarTexture.getHeight());

            // Full Bar
            graphics.blit(RenderType::guiTexturedOverlay, hpBarTexture.getResourceLocation(), width, height, 0, 5, (int) (hpFactor * 182f), 5, hpBarTexture.getWidth(), hpBarTexture.getHeight());
        }

    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return menu.applyGlobalChanges(this, new Vector2f(scaledResolution.x - 190, 15));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(182 * scale, 5 * scale);
    }

}
