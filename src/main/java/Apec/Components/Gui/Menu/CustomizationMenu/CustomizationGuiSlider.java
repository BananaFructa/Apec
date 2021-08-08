package Apec.Components.Gui.Menu.CustomizationMenu;

import Apec.Components.Gui.GuiIngame.GUIComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class CustomizationGuiSlider extends GuiButton {

    private GUIComponent guiComponent;
    private int slideValue = 0;
    boolean userDragging = false;
    int initialX  = 0, initialY = 0;
    float initialScale = 0;

    public CustomizationGuiSlider(int x, int y, GUIComponent guiComponent) {
        super(0,x,y,4,4,"");
        this.guiComponent = guiComponent;
        this.slideValue = (int)(100 * (this.guiComponent.getScale()/2f));
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            ScaledResolution sr = new ScaledResolution(mc);
            drawRect(xPosition,yPosition,xPosition+width,yPosition+width,0x8abbbbbb);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (this.hovered) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x1adddddd);
            this.mouseDragged(mc, mouseX, mouseY);
            Vector2f v = this.guiComponent.getCurrentBoundingPoint();
            this.xPosition = (int)v.x - 2;
            this.yPosition = (int)v.y - 2;
            if (userDragging) {
                changeVale(mouseX,mouseY);
            }
        }
    }

        public void changeVale(int mouseX,int mouseY) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        Vector2f realAnchor = this.guiComponent.getCurrentAnchorPoint();
        Vector2f boundingPoint = this.guiComponent.getCurrentBoundingPoint();
        int differenceX = (mouseX - this.initialX) * (realAnchor.x < boundingPoint.x ? 1 : -1);
        int differenceY = (mouseY - this.initialY) * (realAnchor.y < boundingPoint.y ? 1 : -1);

       /*if (difference < 0){
            slideValue = 0;
            this.guiComponent.setScale(slideValue/50f);
            return;
        }
        if (difference > 78){
            slideValue = 100;
            this.guiComponent.setScale(slideValue/50f);
            return;
        }*/
        slideValue = (int)(100f * ((float)(differenceX + differenceY)/(float)50));
        this.guiComponent.setScale(initialScale + slideValue/280f);
    }

    public void userStartedDragging(int mX,int mY) {
        this.userDragging = true;
        initialX = mX;
        initialY = mY;
        this.initialScale = this.guiComponent.getScale();
    }

    public void resetScale() {
        this.slideValue = 50;
        this.guiComponent.setScale(1);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
        userDragging = false;
    }
}
