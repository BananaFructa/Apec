package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.vector.Vector2f;

public class GUIComponent {

    protected Minecraft mc = Minecraft.getMinecraft();

    protected Vector2f delta_position = new Vector2f(0,0);
    protected float scale = 1;
    public GUIComponentID gUiComponentID;

    public GUIComponent(GUIComponentID gUiComponentID) {
        this.gUiComponentID = gUiComponentID;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {

    }

    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od,ScaledResolution sr,boolean editingMode) {

    }

    public void setDelta_position(Vector2f dp) {
        delta_position = dp;
    }

    public Vector2f getAnchorPointPosition (ScaledResolution sr) {
        return new Vector2f(0,0);
    }

    public Vector2f getRealAnchorPoint(ScaledResolution sr) {
        return ApecUtils.addVec(getAnchorPointPosition(new ScaledResolution(mc)),getDelta_position());
    }

    public void setScale(float s) {
        this.scale = s;
    }

    public Vector2f getDelta_position() {
        return this.delta_position;
    }

    public Vector2f getBoundingPoint() {
        return new Vector2f(0,0);
    }

    public float getScale() {
        return this.scale;
    }

}
