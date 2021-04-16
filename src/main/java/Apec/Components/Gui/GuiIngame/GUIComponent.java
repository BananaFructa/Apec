package Apec.Components.Gui.GuiIngame;

import Apec.Utils.ApecUtils;
import Apec.DataInterpretation.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class GUIComponent {

    protected Minecraft mc = Minecraft.getMinecraft();

    protected Vector2f delta_position = new Vector2f(0, 0);
    protected List<Vector2f> subComponentDeltas = new ArrayList<Vector2f>();
    protected List<Integer> DisabledSubComponents = new ArrayList<Integer>();
    protected float scale = 1;
    protected float oneOverScale = 1;
    public GUIComponentID gUiComponentID;
    protected ScaledResolution g_sr;

    public boolean scalable = true;

    public GUIComponent(GUIComponentID gUiComponentID) {
        this.gUiComponentID = gUiComponentID;
        MinecraftForge.EVENT_BUS.register(this);
        g_sr = new ScaledResolution(mc);
    }

    public GUIComponent(GUIComponentID gUiComponentID, int SubElementCount) {
        this(gUiComponentID);
        for (int i = 0; i < SubElementCount; i++) {
            this.subComponentDeltas.add(new Vector2f(0, 0));
        }
    }

    protected void DisableSubComponent(int s) {
        DisabledSubComponents.add(s);
    }

    protected void EnableSubComponent(int s) {
        int DisabledCount = DisabledSubComponents.size();
        for (int i = 0; i < DisabledCount; i++) {
            if (DisabledSubComponents.get(i) == s) {
                DisabledSubComponents.remove(i);
                break;
            }
        }
    }

    public boolean IsSubcomponentDisabled(int s) {
        return DisabledSubComponents.contains(s);
    }

    public GUIComponent(GUIComponentID gUiComponentID, boolean scalable) {
        this(gUiComponentID);
        this.scalable = scalable;
    }

    /**
     * For texture drawing
     */
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        g_sr = sr;
    }

    /**
     * For rendering text
     */
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        g_sr = sr;
    }

    /**
     * Is called before the editing menu is opened
     */
    public void editInit() {

    }

    /**
     * Initialize function
     */
    public void init() {

    }

    /**
     * Sets the delta position vector
     */
    public void setDelta_position(Vector2f dp) {
        delta_position = dp;
    }

    /**
     * The initial position point
     */
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(0, 0);
    }

    /**
     * The point at which the object is currently situated
     */
    public Vector2f getCurrentAnchorPoint() {
        return ApecUtils.addVec(getAnchorPointPosition(), getDeltaPosition());
    }

    /**
     * Sets the scale of the element
     */
    public void setScale(float s) {
        this.scale = s;
        this.oneOverScale = 1f/scale;
    }

    /**
     * Gets the distance vector between the anchor position and the current position
     */
    public Vector2f getDeltaPosition() {
        return this.delta_position;
    }

    /**
     * Gives the distance vector between the position of the element and the position of the point that describes the rectangle in which the element is confined
     */
    public Vector2f getBoundingPoint() {
        return new Vector2f(0, 0);
    }

    /**
     * Gets the scale of the element
     */
    public float getScale() {
        return this.scale;
    }

    public float getOneOverScale() {
        return oneOverScale;
    }

    public List<Vector2f> getSubElementsAnchorPoints() {
        return new ArrayList<Vector2f>();
    }

    public List<Vector2f> getSubElementsRealAnchorPoints() {
        return ApecUtils.AddVecListToList(getSubElementsAnchorPoints(), getSubElementsDeltaPositions());
    }

    public List<Vector2f> getSubElementsBoundingPoints() {
        return new ArrayList<Vector2f>();
    }

    public List<Vector2f> getSubElementsDeltaPositions() {
        return this.subComponentDeltas;
    }

    public void setSubElementDeltaPosition(Vector2f dp, int id) {
        this.subComponentDeltas.set(id, dp);
    }

    public boolean hasSubComponents() {
        return subComponentCount() != 0;
    }

    public int subComponentCount() {
        return this.subComponentDeltas.size();
    }

    public void resetDeltaPositions() {
        this.delta_position = new Vector2f(0, 0);
        for (int i = 0; i < this.subComponentDeltas.size(); i++) {
            this.subComponentDeltas.set(i, new Vector2f(0, 0));
        }
    }


}
