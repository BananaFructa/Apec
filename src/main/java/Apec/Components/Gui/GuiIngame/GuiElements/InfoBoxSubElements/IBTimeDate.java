package Apec.Components.Gui.GuiIngame.GuiElements.InfoBoxSubElements;

import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

public class IBTimeDate extends GUIComponent {

    int TimeStringLength = 0;

    public IBTimeDate() {
        super(GUIComponentID.IB_TIME_DATE);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        Vector2f pos = getCurrentAnchorPoint();
        TimeStringLength = mc.fontRendererObj.getStringWidth((sd.Date + " " + sd.Hour));

        mc.fontRendererObj.drawString(
                sd.Date + " " + sd.Hour,
                (int) (pos.x * oneOverScale) - TimeStringLength,
                (int) (pos.y * oneOverScale), 0xffffff, false
        );

    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f((g_sr.getScaledWidth() - 20), 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-TimeStringLength, 10*scale);
    }
}
