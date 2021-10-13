package Apec.Components.Gui.GuiIngame.GuiElements.InfoBoxSubElements;

import Apec.ApecMain;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

public class IBMithrilPowder extends GUIComponent {

    private int MithrilPowderStringWidth = 0;

    public IBMithrilPowder() {
        super(GUIComponentID.IB_MITHRIL_POWDER);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;
        Vector2f pos = getCurrentAnchorPoint();
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        if(!ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_POWDER_DISPLAY)) {
            if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_MITHRIL_POWDER) || editingMode) {
                gi.drawTexturedModalRect((int) (pos.x * oneOverScale), pos.y * oneOverScale - 1, 90, 216, 9, 9);
            }
        }
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        String mithrilPowderText = (UseIcons ? ts.MithrilPowder : "\u1805" + ts.MithrilPowder);
        Vector2f pos = getCurrentAnchorPoint();
        if(!ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_POWDER_DISPLAY)) {
            if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_MITHRIL_POWDER) || editingMode) {
                ApecUtils.drawStylizedString(
                        mithrilPowderText,
                        (int) ((pos.x + (UseIcons ? 11 : 0)) * oneOverScale),
                        (int) (pos.y  * oneOverScale),
                        0x00AA00
                );
            }
        }
        MithrilPowderStringWidth = mc.fontRendererObj.getStringWidth(mithrilPowderText);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(600*scale + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(MithrilPowderStringWidth + (UseIcons ? 11 : 0), 10*scale);
    }

}
