package Apec.Components.Gui.GuiIngame.GuiElements.PowderInfoSubElements;

import Apec.ApecMain;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class PIMithril extends GUIComponent {

    private int mithrilStringWidth = 0;

    public PIMithril() {
        super(GUIComponentID.PI_MITHRIL);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_POWDER_DISPLAY) || editingMode) {
            GlStateManager.scale(scale, scale, scale);
            Vector2f pos = getCurrentAnchorPoint();

            if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_MITHRIL_POWDER) && !ts.MithrilPowder.isEmpty() || editingMode) {
                String mithrilPowderText = "\u1805" + ts.MithrilPowder;
                mithrilStringWidth = mc.fontRendererObj.getStringWidth(mithrilPowderText);
                ApecUtils.drawStylizedString(mithrilPowderText, (int) (pos.x * oneOverScale - mithrilStringWidth), (int) (pos.y * oneOverScale), 0x00AA00);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth() - 8, 112);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-mithrilStringWidth*scale,11*scale);
    }
}
