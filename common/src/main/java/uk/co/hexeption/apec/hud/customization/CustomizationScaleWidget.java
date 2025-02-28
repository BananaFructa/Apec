package uk.co.hexeption.apec.hud.customization;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.hud.Element;

public class CustomizationScaleWidget extends Button implements MC {

    public Element element;
    private int slideValue = 0;
    public boolean isUserDragging = false;
    public int initialX = 0;
    public int initialY = 0;
    public float initialScale = 0;

    protected CustomizationScaleWidget(Element element, int x, int y) {
        super(x, y, 4, 4, Component.nullToEmpty(element.getClass().getName()), button -> {
        }, CustomizationWidget.DEFAULT_NARRATION);

        this.element = element;
        this.slideValue = (int) (100 * (this.element.getScale() / 2f));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        if (this.isHovered) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x1adddddd);
        } else {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x8abbbbbb);
        }

        Vector2f v = this.element.getCurrentBoundingPoint();
        this.setX((int)v.x - 2);
        this.setY((int)v.y - 2);
    }

    public void userStartedDragging(double mouseX, double mouseY) {
        this.isUserDragging = true;
        initialX = (int) mouseX;
        initialY = (int) mouseY;
        this.initialScale = this.element.getScale();
    }

    public void resetScale() {
        this.slideValue = 50;
        this.element.setScale(1);
    }


    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if(isUserDragging){
            Vector2f realAnchor = this.element.getCurrentAnchorPoint();
            Vector2f boundingPoint = this.element.getCurrentBoundingPoint();
            int differenceX = (int) ((d - this.initialX) * (realAnchor.x < boundingPoint.x ? 1 : -1));
            int differenceY = (int) ((e - this.initialY) * (realAnchor.y < boundingPoint.y ? 1 : -1));

            slideValue = (int)(100f * ((float)(differenceX + differenceY)/(float)50));
            this.element.setScale(initialScale + slideValue/280f);
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        this.isUserDragging = false;
        return super.mouseReleased(d, e, i);
    }

}
