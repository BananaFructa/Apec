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

    int gemstoneWidth = 0, mithrilWidth = 0;

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr,boolean editingMode) {
        super.draw(ps,sd,od,ts,sr,editingMode);
        GlStateManager.pushMatrix();
        
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.POWDER_DISPLAY) && ApecUtils.isInDwarvenMines(sd.Zone)) {
            GlStateManager.scale(scale, scale, scale);
            String gemstonePowder = "\u2727: " + (String) (ts.GemstonePowder != null && !ts.GemstonePowder.isEmpty() ? ts.GemstonePowder : "0");
            String mithrilPowder = "\u1805: " + (String) (ts.MithrilPowder != null && !ts.GemstonePowder.isEmpty() ? ts.MithrilPowder : "0");

            mithrilWidth = mc.fontRendererObj.getStringWidth(mithrilPowder);
            gemstoneWidth = mc.fontRendererObj.getStringWidth(gemstonePowder);

            ApecUtils.drawThiccBorderString(mithrilPowder, (int) (subComponentDeltas.get(0).getX() - mithrilWidth), (int) (subComponentDeltas.get(0).getY() - 10), 0x00AA00);
            ApecUtils.drawThiccBorderString(gemstonePowder, (int) (subComponentDeltas.get(1).getX() - gemstoneWidth), (int) (subComponentDeltas.get(1).getY() - 20), 0xFF55FF);
            
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
            add(new Vector2f(0, -1*scale));
            add(new Vector2f(0, -11*scale));
        }};
    }

    @Override
    public List<Vector2f> getSubElementsBoundingPoints() {
        List<Vector2f> RelativeVectors = new ArrayList<Vector2f>(2) {{
            add(new Vector2f(-mithrilWidth*scale,-11*scale));
            add(new Vector2f(-gemstoneWidth*scale,-11*scale));
        }};
        return ApecUtils.AddVecListToList(RelativeVectors, getSubElementsCurrentAnchorPoints());
    }

}
