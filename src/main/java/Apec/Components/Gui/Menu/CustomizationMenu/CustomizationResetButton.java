package Apec.Components.Gui.Menu.CustomizationMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class CustomizationResetButton extends GuiButton {

    public CustomizationResetButton(int buttonId, int x, int y, int widthIn, int heightIn) {
        super(buttonId,x,y,widthIn,heightIn,"Reset All");
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if (this.hovered) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x1adddddd);
            drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x990a0a0a);
            this.mouseDragged(mc, mouseX, mouseY);
            FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
    }

}
