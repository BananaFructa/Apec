package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.ComponentId;
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
            Vector2f posH = hotBar.getRealAnchorPoint();
            x = (int) (posH.x/hotBar.scale) + 1;
            y = (int) (posH.y/hotBar.scale) - 10;
        } else {
            x = (- mc.fontRendererObj.getStringWidth(CurrentText) / 2f) - 92/hotBar.scale + g_sr.getScaledWidth()/hotBar.scale;
            y = g_sr.getScaledHeight()/scale - 67/hotBar.scale;
            x += hotBar.delta_position.x/hotBar.scale;
            y += hotBar.delta_position.y/hotBar.scale;
        }
        return new Vector2f(x,y);
    }

    @Override
    public Vector2f getBoundingPoint() {
        float x = 0f;
        float y = 0f;
        x = mc.fontRendererObj.getStringWidth(CurrentText);
        y = mc.fontRendererObj.FONT_HEIGHT * hotBar.scale;
        return ApecUtils.addVec(getRealAnchorPoint(), new Vector2f(x, y));
    }

}
