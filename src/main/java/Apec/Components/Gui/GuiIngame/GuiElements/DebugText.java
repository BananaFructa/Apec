package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Utils.ApecUtils;
import net.minecraft.client.gui.ScaledResolution;

public class DebugText extends GUIComponent {

    public DebugText() {
        super(GUIComponentID.DEBUG_TEXT);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        ApecUtils.drawOutlineWrappedText(ApecUtils.removeAllCodes(ps.toString()), 0, 0, 300, 0xffffffff);
    }
}
