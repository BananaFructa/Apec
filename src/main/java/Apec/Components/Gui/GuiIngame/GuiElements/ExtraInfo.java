package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.TextComponent;
import Apec.DataInterpretation.DataExtractor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class ExtraInfo extends TextComponent {

    public ExtraInfo() {
        super(GUIComponentID.EXTRA_INFO);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr,boolean editingMode) {
        super.draw(ps,sd,od,ts,sr,editingMode);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        Vector2f ExtraScoreInfo = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),oneOverScale);

        ArrayList<String> ei = new ArrayList<String>();
        ei.addAll(sd.ExtraInfo);
        ei.addAll(od.ExtraInfo);
        if (editingMode && ei.isEmpty()) {
            for (int i = 0;i < 10;i++)
            ei.add("Something");
        }

        if (!ei.isEmpty()) {
            for (int i = 0;i < ei.size();i++) {
                ApecUtils.drawStylizedString(ei.get(i), (int)(ExtraScoreInfo.x), (int) (ExtraScoreInfo.y + i * 11), 0x0ffffff);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.guiModifier.applyGlobalChanges(this,new Vector2f(5,85));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(55*scale,100*scale);
    }
}
