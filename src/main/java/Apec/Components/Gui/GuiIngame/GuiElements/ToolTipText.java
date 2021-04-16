package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.ComponentId;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Settings.SettingID;
import org.lwjgl.util.vector.Vector2f;

public class ToolTipText extends GUIComponent {

    public HotBar hotBar;
    public String CurrentText = "";

     public ToolTipText() {
         super(GUIComponentID.TOOL_TIP_TEXT,false);
     }

    @Override
    public void init() {
        hotBar = (HotBar) ((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.HOT_BAR);
    }

    public void SetText(String text) {
         this.CurrentText = text;
     }

    @Override
    public Vector2f getAnchorPointPosition() {
         float x = 0f;
         float y = 0f;
        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.ITEM_HIGHLIGHT_TEXT)) {
            Vector2f posH = ApecUtils.scalarMultiply( hotBar.getCurrentAnchorPoint(),hotBar.getOneOverScale());
            x = (int) (posH.x) + 1;
            y = (int) (posH.y) - 10;
        } else {
            x = (- mc.fontRendererObj.getStringWidth(CurrentText) * 0.5f) - 92 + g_sr.getScaledWidth()*hotBar.getOneOverScale();
            y = g_sr.getScaledHeight()*oneOverScale - 67*hotBar.getOneOverScale();
            x += hotBar.getDeltaPosition().x*hotBar.getScale();
            y += hotBar.getDeltaPosition().y*hotBar.getOneOverScale();
        }
        return new Vector2f(x,y);
    }

    @Override
    public Vector2f getBoundingPoint() {
        float x = 0f;
        float y = 0f;
        x = mc.fontRendererObj.getStringWidth(CurrentText);
        y = mc.fontRendererObj.FONT_HEIGHT * hotBar.getScale();
        return ApecUtils.addVec(getCurrentAnchorPoint(), new Vector2f(x, y));
    }

}
