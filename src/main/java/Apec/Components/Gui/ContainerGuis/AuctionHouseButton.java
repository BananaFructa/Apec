package Apec.Components.Gui.ContainerGuis;

import Apec.ApecMain;
import Apec.ComponentId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class AuctionHouseButton extends GuiButton {

    public AuctionHouseComponent.Actions action;

    public AuctionHouseButton(int buttonId, int x, int y, int widthIn, int heightIn, AuctionHouseComponent.Actions action) {
        super(buttonId, x, y, widthIn, heightIn, "");
        this.action = action;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            switch (action) {
                case SEARCH:
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 226, 0, 15, 15);
                    break;
                case NEXT:
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 226, 15, 15, 15);
                    break;
                case BACK:
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 226, 30, 15, 15);
                    break;
                case CLOSE:
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 226, 45, 15, 15);
                    break;
            }

            if (this.hovered) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x1adddddd);

            this.mouseDragged(mc, mouseX, mouseY);

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2,  14737632);
        }
    }

}
