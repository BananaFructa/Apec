package Apec.Components.Gui.Menu.CustomizationMenu;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GuiElements.GUIComponent;
import Apec.Settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

public class CustomizationGuiSlider extends GuiButton {

    private GUIComponent guiComponent;
    private int slideValue = 0;
    boolean userDragging = false;

    public CustomizationGuiSlider(int x, int y, GUIComponent guiComponent) {
        super(0,x,y,78,10,"");
        this.guiComponent = guiComponent;
        this.slideValue = (int)(100 * (this.guiComponent.getScale()/2f));
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            ScaledResolution sr = new ScaledResolution(mc);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
            drawTexturedModalRect(xPosition,yPosition,183,78,74,10);
            drawTexturedModalRect(xPosition + (int)(76f * slideValue/100),yPosition,181,78,2,10);
            if (this.hovered) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x1adddddd);
            this.mouseDragged(mc, mouseX, mouseY);
            Vector2f v = ApecUtils.addVec(this.guiComponent.getAnchorPointPosition(new ScaledResolution(mc)),this.guiComponent.getDelta_position());
            if (v.x + 7 + this.width > sr.getScaledWidth()) {
                 this.xPosition = (int)v.x - 85;
            } else {
                this.xPosition = (int) v.x + 7;
            }
            this.yPosition = (int)v.y - 5;
            if (userDragging) {
                changeVale(mouseX);
            }
        }
    }

    public void changeVale(int mouseX) {
        int difference = mouseX - this.xPosition;
        if (difference < 0){
            slideValue = 0;
            this.guiComponent.setScale(slideValue/50f);
            return;
        }
        if (difference > 78){
            slideValue = 100;
            this.guiComponent.setScale(slideValue/50f);
            return;
        }
        slideValue = (int)(100f * ((float)difference/(float)this.width));
        this.guiComponent.setScale(slideValue/50f);
    }

    public void userStartedDragging() {
        this.userDragging = true;
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
