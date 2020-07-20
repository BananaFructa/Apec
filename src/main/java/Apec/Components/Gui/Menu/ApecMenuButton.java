package Apec.Components.Gui.Menu;

import Apec.Settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class ApecMenuButton extends GuiButton {

    Setting setting;

    public ApecMenuButton(int buttonId, int x, int y, int widthIn, int heightIn,Setting setting) {
        super(buttonId,x,y,widthIn,heightIn,"");
        this.setting = setting;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if (this.hovered) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x1adddddd);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    public void ToggleSetting () {
        setting.Toggle();
    }

}
