package uk.co.hexeption.apec.hud.elements.mana;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.utils.ApecUtils;
import uk.co.hexeption.apec.utils.MultiColourText;

public class MPText extends Element {

    private final MultiColourText mct = new MultiColourText();
    private int stringWidth = 0;

    public MPText() {
        super(ElementType.MP_TEXT);
    }

    @Override
    public void drawText(GuiGraphics graphics, boolean editMode) {
        int mp = Apec.SKYBLOCK_INFO.getPlayerStats().mana();
        int base_mp = Apec.SKYBLOCK_INFO.getPlayerStats().base_mana();
        int overflow = Apec.SKYBLOCK_INFO.getPlayerStats().overflow();
        int base_overflow = Apec.SKYBLOCK_INFO.getPlayerStats().base_overflow();

        boolean showOverFlow = true;

        Vector2f statBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);
        int width = (int) statBar.x;
        int height = (int) statBar.y;

        String mpText = mp + "/" + base_mp + " MP";

        if (overflow != 0) {
            if (showOverFlow) {
                String OPString = overflow + "/" + base_overflow + " OP";
                mct.setString(new String[]{OPString + " ", mpText}, new int[]{0x1966AD, 0x1139bd});
            } else {
                int totalMp = overflow + mp;
                mct.setString(new String[]{Integer.toString(totalMp), "/" + base_mp}, new int[]{0x1966AD, 0x1139bd});
            }
        } else {
            mct.setString(new String[]{mpText}, new int[]{0x1139bd});
        }

        stringWidth = mct.width();
        mct.setXY((int) (width - stringWidth), (int) (height - 10));
        mct.render(graphics);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.menu.applyGlobalChanges(this, new Vector2f(mc.getWindow().getGuiScaledWidth() - 190 + 112 + 70, 34));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-stringWidth * scale, -11 * scale);
    }
}
