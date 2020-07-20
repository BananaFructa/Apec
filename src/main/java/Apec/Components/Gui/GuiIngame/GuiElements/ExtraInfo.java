package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class ExtraInfo extends GUIComponent {

    public ExtraInfo() {
        super(GUIModifier.GUiComponentID.EXTRA_SCR_INF);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr) {
        Vector2f ExtraScoreInfo = new Vector2f(5,85);

        ExtraScoreInfo = ApecUtils.addVec(ExtraScoreInfo,delta_position);

        ArrayList<String> ei = new ArrayList<String>();
        ei.addAll(sd.ExtraInfo);
        ei.addAll(od.ExtraInfo);

        if (!ei.isEmpty()) {
            for (int i = 0;i < ei.size();i++) {
                ApecUtils.drawThiccBorderString(ei.get(i), (int) ExtraScoreInfo.x, (int) ExtraScoreInfo.y + i * 11, 0x0ffffff);
            }
        }
    }
}
