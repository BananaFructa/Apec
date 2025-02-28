package uk.co.hexeption.apec.hud.elements.xp;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.utils.ApecUtils;

public class XPText extends Element {

    private int stringWidth = 0;

    public XPText() {
        super(ElementType.XP_TEXT);
    }

    @Override
    public void drawText(GuiGraphics graphics, Vector2f scaledResolution, boolean editMode) {
        String xpText = "Lvl " + mc.player.experienceLevel + " XP";

        Vector2f statBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);
        int width = (int) statBar.x;
        int height = (int) statBar.y;

        ApecUtils.drawOutlineText(mc, graphics, xpText,  (int) (width - mc.font.width(xpText)), (int) (height - 10), 0x80ff20);
        stringWidth = mc.font.width(xpText);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.menu.applyGlobalChanges(this, new Vector2f(scaledResolution.x - 190 + 112 + 70, 53));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-stringWidth * scale, -11 * scale);
    }
}
