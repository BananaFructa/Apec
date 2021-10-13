package Apec.Components.Gui.GuiIngame;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Utils.ApecUtils;
import Apec.DataInterpretation.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.vector.Vector2f;
import scala.tools.nsc.SubComponent;

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
    protected GUIComponent parent = null;

    protected Vector2f delta_position = new Vector2f(0, 0);
    protected List<Vector2f> subComponentDeltas = new ArrayList<Vector2f>();
    protected List<Integer> DisabledSubComponents = new ArrayList<Integer>();
    protected float scale = 1;
    protected float oneOverScale = 1;
    public GUIComponentID gUiComponentID;
    protected ScaledResolution g_sr;

    public boolean scalable = true;
    public boolean editable = true;
    public boolean enabled = true;

    protected List<GUIComponent> subComponents = new ArrayList<GUIComponent>();

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

    public GUIComponent(GUIComponentID gUiComponentID, boolean scalable) {
        this(gUiComponentID);
        this.scalable = scalable;
    }

    /**
     * For texture drawing
     */
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        g_sr = sr;

        for (GUIComponent subComp : subComponents) subComp.drawTex(ps, sd, od, ts, sr, editingMode);
    }

    /**
     * For rendering text
     */
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        g_sr = sr;

        for (GUIComponent subComp : subComponents) subComp.draw(ps, sd, od, ts, sr, editingMode);
    }

    /**
     * Is called before the editing menu is opened
     */
    public void editInit() {
        for (GUIComponent subComp : subComponents) {
            subComp.editInit();
        }
    }

    /**
     * Initialize function
     */
    public void init() {
        guiModifier = ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER);
        for (GUIComponent subComp : subComponents) {
            subComp.init();
        }
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
        return ApecUtils.addVec(ApecUtils.addVec(getAnchorPointPosition(), getDeltaPosition()),this.getTotalParentOffset());
    }

    /**
     * Sets the scale of the element
     */
    public void setScale(float s) {
        this.scale = s;
        this.oneOverScale = 1f/scale;
        for (GUIComponent subComp : subComponents) {
            subComp.setScale(s);
        }
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

    public List<GUIComponent> getSubComponentList() { return subComponents; }


    public void addSubComponent(GUIComponent child) {
        child.parent = this;
        subComponents.add(child);
    }

    public boolean hasSubComponents() {
        return !this.subComponents.isEmpty();
    }

    public int subComponentCount() {
        return this.subComponents.size();
    }

    public Vector2f getTotalParentOffset() {
        if (this.parent != null) {
            return ApecUtils.addVec(this.parent.getCurrentAnchorPoint(),this.parent.getTotalParentOffset());
        } else {
            return new Vector2f(0,0);
        }
    }

    public void resetDeltaPositions() {
        this.delta_position = new Vector2f(0, 0);
        for (GUIComponent subComb : subComponents) {
            subComb.resetDeltaPositions();
        }
    }


}
