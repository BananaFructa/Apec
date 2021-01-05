package Apec.Components.Gui.Menu.SettingsMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class ApecMenuNavigationButton extends GuiButton {

    NavigationAction action;

    public ApecMenuNavigationButton(int buttonId, int x, int y, int widthIn, int heightIn,NavigationAction action) {
        super(buttonId,x,y,widthIn,heightIn,ApecMenu.getStringFromAction(action));
        this.action = action;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x99151515);
            if (this.hovered) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x1adddddd);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;
            this.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
    }

    public void ExecuteAction () {
        ((ApecMenu.ApecMenuGui)Minecraft.getMinecraft().currentScreen).executeNavigation(this.action);
    }
}
