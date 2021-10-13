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

public class PIGemstone extends GUIComponent {

    private int gemstoneStringWidth = 0;

    public PIGemstone() {
        super(GUIComponentID.PI_GEMSTONE);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_POWDER_DISPLAY) || editingMode) {
            GlStateManager.scale(scale, scale, scale);
            Vector2f pos = getCurrentAnchorPoint();

            if(ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_GEMSTONE_POWDER)&& !ts.GemstonePowder.isEmpty() || editingMode){
                String gemstonePowderText = "\u1805" + ts.GemstonePowder;
                gemstoneStringWidth = mc.fontRendererObj.getStringWidth(gemstonePowderText);
                ApecUtils.drawStylizedString(gemstonePowderText, (int) (pos.x * oneOverScale - gemstoneStringWidth), (int) (pos.y * oneOverScale), 0xFF55FF);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth() - 8, 132);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-gemstoneStringWidth*scale,-11*scale);
    }

}
