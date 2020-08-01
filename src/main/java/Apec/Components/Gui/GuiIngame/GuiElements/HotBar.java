package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

public class HotBar extends GUIComponent {

    public HotBar() {
        super(GUIComponentID.HOT_BAR);
    }

    @Override
    public Vector2f getAnchorPointPosition(ScaledResolution sr) {
        return new Vector2f(sr.getScaledWidth()-183, sr.getScaledHeight() - 43);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(new ScaledResolution(mc)),new Vector2f(182,22));
    }
}
