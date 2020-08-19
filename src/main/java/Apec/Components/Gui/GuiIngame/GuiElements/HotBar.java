package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataExtractor;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

public class HotBar extends GUIComponent {

    public HotBar() {
        super(GUIComponentID.HOT_BAR);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, sr, editingMode);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth()-183, g_sr.getScaledHeight() - 43);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(),new Vector2f(182*scale,22*scale));
    }
}
