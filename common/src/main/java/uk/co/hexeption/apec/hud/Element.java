package uk.co.hexeption.apec.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.utils.ApecUtils;

public class Element implements MC {

    protected ApecMenu menu;

    protected ElementType type;

    protected Vector2f deltaPosition = new Vector2f(0, 0);
    protected List<Vector2f> subElementDeltaPositions = new ArrayList<>();

    protected float scale = 1;
    protected boolean scalable = true;

    public Element(ElementType type) {
        this.type = type;
    }

    public Element(ElementType type, int SubElementCount) {
        this(type);
        for (int i = 0; i < SubElementCount; i++) {
            this.subElementDeltaPositions.add(new Vector2f(0, 0));
        }
    }


    public void init(ApecMenu menu) {
        this.menu = menu;
    }

    public void drawText(GuiGraphics graphics, boolean editMode) {

    }

    public void tick() {

    }


    public Vector2f getAnchorPointPosition() {
        return new Vector2f(0, 0);
    }

    public Vector2f getCurrentAnchorPoint() {
        return ApecUtils.addVec(getAnchorPointPosition(), getDeltaPosition());
    }

    public Vector2f getDeltaPosition() {
        return deltaPosition;
    }

    public Vector2f getCurrentBoundingPoint() {
        return ApecUtils.addVec(getCurrentAnchorPoint(), getBoundingPoint());
    }

    public Vector2f getBoundingPoint() {
        return new Vector2f(0, 0);
    }

    public void setDeltaPosition(Vector2f vector2f) {
        this.deltaPosition = vector2f;
    }

    public List<Vector2f> getSubElementsAnchorPoints() {
        return new ArrayList<Vector2f>();
    }

    public List<Vector2f> getSubElementsCurrentAnchorPoints() {
        return ApecUtils.addVecListToList(getSubElementsAnchorPoints(), getSubElementDeltaPositions());
    }

    public List<Vector2f> getSubElementDeltaPositions() {
        return subElementDeltaPositions;
    }

    public void setSubElementDeltaPosition(int index, Vector2f vector2f) {
        this.subElementDeltaPositions.set(index, vector2f);
    }

    public Vector2f getSubElementDeltaPosition(int index) {
        return subElementDeltaPositions.get(index);
    }

    public List<Vector2f> getSubElementsBoundingPoints() {
        return new ArrayList<Vector2f>();
    }

    public boolean hasSubComponents() {
        return subComponentCount() != 0;
    }

    public int subComponentCount() {
        return this.subElementDeltaPositions.size();
    }

    public void resetDeltaPositions() {
        this.deltaPosition = new Vector2f(0, 0);
        for (int i = 0; i < this.subElementDeltaPositions.size(); i++) {
            this.subElementDeltaPositions.set(i, new Vector2f(0, 0));
        }
    }

    public boolean isScalable() {
        return scalable;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public ElementType getType() {
        return type;
    }
}
