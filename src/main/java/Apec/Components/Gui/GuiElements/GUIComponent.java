package Apec.Components.Gui.GuiElements;

import Apec.Components.Gui.GUIModifier;
import Apec.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

public class GUIComponent {

    protected Minecraft mc = Minecraft.getMinecraft();

    Vector2f delta_position = new Vector2f(0,0);
    GUIModifier.GUiComponentID gUiComponentID;

    public GUIComponent(GUIModifier.GUiComponentID gUiComponentID) {
        this.gUiComponentID = gUiComponentID;
    }

    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, ScaledResolution sr) {

    }

    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, ScaledResolution sr) {

    }

    public void setDelta_position(Vector2f dp) {
        delta_position = dp;
    }

}
