package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

public class PowderInfo extends GUIComponent {

    public PowderInfo () {
        super(GUIComponentID.POWDER_DISPLAY,2);
    }

    int gemstoneStringWidth = 0, mithrilStringWidth = 0;

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr,boolean editingMode) {
        super.draw(ps,sd,od,ts,sr,editingMode);
        GlStateManager.pushMatrix();
        
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_POWDER_DISPLAY) && (ApecUtils.isInDwarvenMines(sd.Zone) || editingMode)) {
            GlStateManager.scale(scale, scale, scale);
            
            if(ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_MITHRIL_POWDER)){
                String mithrilPowderText = "\u1805" + ts.MithrilPowder;
                mithrilStringWidth = mc.fontRendererObj.getStringWidth(mithrilPowderText);
                ApecUtils.drawThiccBorderString(mithrilPowderText, (int) (100 + subComponentDeltas.get(0).getX() - mithrilStringWidth), (int) (subComponentDeltas.get(0).getY() - 10), 0x00AA00);
            }

            if(ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_GEMSTONE_POWDER)){
                String gemstonePowderText = "\u1805" + ts.GemstonePowder;
                gemstoneStringWidth = mc.fontRendererObj.getStringWidth(gemstonePowderText);
                ApecUtils.drawThiccBorderString(gemstonePowderText, (int) (100 + subComponentDeltas.get(1).getX() - gemstoneStringWidth), (int) (subComponentDeltas.get(1).getY() - 20), 0xFF55FF);
            }

        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(0, 0);
    }

    @Override
    public List<Vector2f> getSubElementsAnchorPoints() {
        return new ArrayList<Vector2f>() {{
            add(new Vector2f(100, -1*scale));
            add(new Vector2f(100, -11*scale));
        }};
    }

    @Override
    public List<Vector2f> getSubElementsBoundingPoints() {
        List<Vector2f> RelativeVectors = new ArrayList<Vector2f>(2) {{
            add(new Vector2f(-mithrilStringWidth*scale,-11*scale));
            add(new Vector2f(-gemstoneStringWidth*scale,-11*scale));
        }};
        return ApecUtils.AddVecListToList(RelativeVectors, getSubElementsCurrentAnchorPoints());
    }

}
