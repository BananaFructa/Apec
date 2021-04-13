package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class TPRVDownloadButton extends GuiButton {

    private TPData correspondingData;

    public TPRVDownloadButton(int buttonId, int x, int y,int width,int height, TPData data) {
        super(buttonId, x, y,width,height, "Download");
        this.correspondingData = data;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            drawRect(xPosition,yPosition,xPosition+width,yPosition+height,0xff00910a);
            this.mouseDragged(mc, mouseX, mouseY);
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.9,0.9,1);
            this.drawCenteredString(fontrenderer, this.displayString, (int)((this.xPosition + this.width / 2)/0.9f), (int)((this.yPosition + (this.height - 8) / 2)/0.9f), 0xffffffff);
            GlStateManager.popMatrix();
        }
    }
}
