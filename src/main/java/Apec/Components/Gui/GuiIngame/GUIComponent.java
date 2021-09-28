package Apec.Components.Gui.GuiIngame;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Utils.ApecUtils;
import Apec.DataInterpretation.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class GUIComponent {

    /**
     * How a position of an element is dictated:
     * Each element has 3 vectors:
     *      - the anchor position : the default position of the element
     *      - the delta position : the difference vector between where the element should be and the anchor position
     *      - the bounding point : the difference vector between the sum of the anchor position and delta position and the position of another point which represents
     *      the other corner of the rectangle that encapsulates the gui component when the customization gui is opened
     */

    protected Minecraft mc = Minecraft.getMinecraft();

    protected GUIModifier guiModifier;

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
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        g_sr = sr;
    }

    /**
     * For rendering text
     */
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
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
        guiModifier = ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER);
    }

    /**
     * Sets the delta position vector
     */
    public void setDeltaPosition(Vector2f dp) {
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
    public Vector2f getCurrentBoundingPoint() {
        return ApecUtils.addVec(getCurrentAnchorPoint(),getBoundingPoint());
    }

    public Vector2f getBoundingPoint() {
        return new Vector2f(0, 0);
    }

    /**
     * @return the scale of the element
     */
    public float getScale() {
        return this.scale;
    }

    /**
     * @return 1 / the scale of the element
     */
    public float getOneOverScale() {
        return oneOverScale;
    }

    /**
     * @return A list of the anchor points of the sub elements
     */

    public List<Vector2f> getSubElementsAnchorPoints() {
        return new ArrayList<Vector2f>();
    }

    /**
     * @return A list of the current anchor points of the sub elements
     */
    public List<Vector2f> getSubElementsCurrentAnchorPoints() {
        return ApecUtils.AddVecListToList(getSubElementsAnchorPoints(), getSubElementsDeltaPositions());
    }

    /**
     * @return A list of the bounding points of the sub elements
     */
    public List<Vector2f> getSubElementsBoundingPoints() {
        return new ArrayList<Vector2f>();
    }

    /**
     * @return A list of the delta positions of the sub elements
     */
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
