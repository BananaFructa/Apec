package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Utils.ApecUtils;
import org.lwjgl.util.vector.Vector2f;

public class BossBar extends GUIComponent {

    public BossBar() {
        super(GUIComponentID.BOSS_HEALTH);
        this.scalable = false;
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth()/2f-183f/2f,1);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getCurrentAnchorPoint(),new Vector2f(183,13));
    }
}
