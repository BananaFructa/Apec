package org.apec.apec.gui.customization;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.apec.apec.MC;
import org.apec.apec.gui.Element;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CustomizationWidget extends Button implements MC {


    class SnapData {
        public boolean xSnap = false;
        public boolean ySnap = false;
        public Vector2f vector = new Vector2f(0, 0);
    }

    Vector2f startingPos;

    List<Integer> xSnapPoints = new ArrayList<>();
    List<Integer> ySnapPoints = new ArrayList<>();

    Element element;
    int subElementIndex = -1;
    public boolean isUserDragging = false;
    public Vector2f fineTuneOffset = new Vector2f(0, 0);
    private boolean lockY = false;

    public CustomizationWidget(Element element, List<Integer> xSnapPoints, List<Integer> ySnapPoints) {
        super(0, 0, 0, 0, Component.nullToEmpty(element.getClass().getName()), button -> {
        }, CustomizationWidget.DEFAULT_NARRATION);
        this.element = element;
        this.xSnapPoints = xSnapPoints;
        this.ySnapPoints = ySnapPoints;
    }

    public CustomizationWidget(Element element, int subElementIndex, List<Integer> xSnapPoints, List<Integer> ySnapPoints) {
        this(element, xSnapPoints, ySnapPoints);
        this.subElementIndex = subElementIndex;
    }

    public CustomizationWidget(Element element, int subElementIndex, List<Integer> xSnapPoints, List<Integer> ySnapPoints, boolean lockY) {
        this(element, subElementIndex, xSnapPoints, ySnapPoints);
        this.lockY = lockY;
    }

    @Override
    public void renderWidget(PoseStack poseStack, int i, int j, float f) {
        Window window = mc.getWindow();
        int scaledWidth = window.getGuiScaledWidth();
        int scaledHeight = window.getGuiScaledHeight();
        Vector2f sr = new Vector2f(scaledWidth, scaledHeight);

        Vector2f v, rv;

        v = this.element.getCurrentAnchorPoint();
        rv = this.element.getCurrentBoundingPoint();

        if (v.x < rv.x && v.y < rv.y) {
            this.setX((int) v.x);
            this.setY((int) v.y);
            this.width = (int) (rv.x - v.x);
            this.height = (int) (rv.y - v.y);
        } else if (v.x > rv.x && v.y > rv.y) {
            this.setX((int) rv.x);
            this.setY((int) rv.y);
            this.width = (int) (v.x - rv.x);
            this.height = (int) (v.y - rv.y);
        } else if (v.x > rv.x && v.y < rv.y) {
            this.setX((int) rv.x);
            this.setY((int) v.y);
            this.width = (int) (v.x - rv.x);
            this.height = (int) (rv.y - v.y);
        } else if (v.x < rv.x && v.y > rv.y) {
            this.setX((int) v.x);
            this.setY((int) rv.y);
            this.width = (int) (rv.x - v.x);
            this.height = (int) (v.y - rv.y);
        }

        if (this.isHovered) {
            fill(poseStack, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x3adddddd);

        } else {
            fill(poseStack, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x3a000000);
        }


    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        Window window = mc.getWindow();
        int scaledWidth = window.getGuiScaledWidth();
        int scaledHeight = window.getGuiScaledHeight();
        Vector2f sr = new Vector2f(scaledWidth, scaledHeight);
        if (isUserDragging) {
            Vector2f anchor = this.element.getCurrentAnchorPoint();

            boolean isSnappedToPositionX = false;
            boolean isSnappedToPositionY = false;

            SnapData SnapResult = IsSnapped((int) d, (int) e, sr, anchor);
            isSnappedToPositionX = SnapResult.xSnap;
            isSnappedToPositionY = SnapResult.ySnap;

            Vector2f Result = new Vector2f(
                    isSnappedToPositionX ? SnapResult.vector.x + fineTuneOffset.x : (int) d - anchor.x + fineTuneOffset.x + startingPos.x,
                    (isSnappedToPositionY ? SnapResult.vector.y + fineTuneOffset.y : (int) e - anchor.y + fineTuneOffset.y + startingPos.y) * (lockY ? 0 : 1)
            );
            this.element.setDeltaPosition(Result);
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    private SnapData IsSnapped(int mouseX, int mouseY, Vector2f sr, Vector2f anchor) {
        Vector2f vec = new Vector2f(0, 0);
        boolean isSnappedToPositionX = false, isSnappedToPositionY = false;

        SnapData sd = new SnapData();
        sd.xSnap = isSnappedToPositionX;
        sd.ySnap = isSnappedToPositionY;
        sd.vector = vec;
        return sd;
    }

    public void userStartedDragging(double mouseX, double mouseY) {
        startingPos = this.element.getCurrentAnchorPoint();

        startingPos.x -= mouseX;
        startingPos.y -= mouseY;
        this.isUserDragging = true;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        this.isUserDragging = false;
        return super.mouseReleased(d, e, i);
    }

    public void reset() {
        this.element.setDeltaPosition(new Vector2f(0, 0));
    }
}
