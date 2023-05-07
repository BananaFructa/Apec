package org.apec.apec.gui.elements.mana;

import com.mojang.blaze3d.vertex.PoseStack;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.skyblock.SkyBlockInfo;
import org.apec.apec.utils.MultiColourText;
import org.joml.Vector2f;

public class MPText extends Element {

    private final MultiColourText mct = new MultiColourText();

    public MPText() {
        super(ElementType.MP_TEXT);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {

        int mp = SkyBlockInfo.getInstance().getPlayerStats().mana();
        int base_mp = SkyBlockInfo.getInstance().getPlayerStats().base_mana();
        int overflow = SkyBlockInfo.getInstance().getPlayerStats().overflow();
        int base_overflow = SkyBlockInfo.getInstance().getPlayerStats().base_overflow();

        boolean showOverFlow = true;

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

        mct.setXY((int) (scaledResolution.x - mct.width() - 5), 25);
        mct.render(poseStack);
    }
}
