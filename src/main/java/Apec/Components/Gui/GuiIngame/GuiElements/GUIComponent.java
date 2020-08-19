package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.vector.Vector2f;

public class GUIComponent {

    protected Minecraft mc = Minecraft.getMinecraft();

    protected Vector2f delta_position = new Vector2f(0,0);
    protected float scale = 1;
    public GUIComponentID gUiComponentID;
    protected ScaledResolution g_sr;

    public GUIComponent(GUIComponentID gUiComponentID) {
        this.gUiComponentID = gUiComponentID;
        MinecraftForge.EVENT_BUS.register(this);
        g_sr = new ScaledResolution(mc);
    }

    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
       g_sr = sr;
    }

    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od,ScaledResolution sr,boolean editingMode) {
       g_sr = sr;
    }

    public void setDelta_position(Vector2f dp) {
        delta_position = dp;
    }

    public Vector2f getAnchorPointPosition () {
        return new Vector2f(0,0);
    }

    public Vector2f getRealAnchorPoint() {
        return ApecUtils.addVec(getAnchorPointPosition(),getDelta_position());
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
