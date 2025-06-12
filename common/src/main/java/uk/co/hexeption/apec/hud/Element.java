package uk.co.hexeption.apec.hud;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.utils.ApecUtils;

public class Element implements MC {

    protected ApecMenu menu;

    protected ElementType type;

    protected Vector2f position;
    protected Vector2f deltaPosition = new Vector2f(0, 0);

    protected float scale = 1;
    protected boolean scalable = true;

    public Element(ElementType type) {
        this.type = type;
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
