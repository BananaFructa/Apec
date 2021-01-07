package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.ComponentId;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

public class HotBar extends GUIComponent {

    public HotBar() {
        super(GUIComponentID.HOT_BAR);
    }

    @Override
    public void editInit() {
        this.scalable = !ApecMain.Instance.settingsManager.getSettingState(SettingID.COMPATIBILITY_5ZIG);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, sr, editingMode);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        // The scale of the bottom bar is used so that the hotbar remains at the same distance from it on different gui scales
        return new Vector2f(g_sr.getScaledWidth()-183, g_sr.getScaledHeight() - 43 +20*(1- ((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.INFO_BOX).scale));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(),new Vector2f(182*scale,22*scale));
    }
}
