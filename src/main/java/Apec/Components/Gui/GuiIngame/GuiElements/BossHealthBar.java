package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecUtils;
import Apec.BossId;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

/** This is not used for now */

public class BossHealthBar extends GUIComponent {

    public BossHealthBar() {
        super(GUIComponentID.INVALID);
    }

    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        if (od.bossData.bossId != BossId.NONE && od.bossData.bossId != null) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;

            DataExtractor.BossData bossData = od.bossData;

            if (bossData.bossId == BossId.MAGMA_BOSS) {
                float factor;
                if (bossData.BaseHp != 0) {
                    factor = (float) bossData.Hp / (float) bossData.BaseHp;
                } else {
                    factor = 0f;
                }
                gi.drawTexturedModalRect(sr.getScaledWidth() / 2 - 102, 14, 0, 151, 204, 23);
                gi.drawTexturedModalRect(sr.getScaledWidth() / 2 - 102, 14, 0, 174, (int) (204 * factor), 23);
            }
        }
    }

    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od,ScaledResolution sr,boolean editingMode) {
        if (od.bossData.bossId != BossId.NONE && od.bossData.bossId != null) {
            DataExtractor.BossData bossData = od.bossData;

            if (bossData.bossId == BossId.MAGMA_BOSS) {
                ApecUtils.drawThiccBorderString("Magma Cream Boss   " + bossData.Hp + "/" + bossData.BaseHp + " HP", sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth("Magma Cream Boss   " + bossData.Hp + "/" + bossData.BaseHp + " HP") / 2, 5, 0xad0e0e);
            }
        }

    }

}
