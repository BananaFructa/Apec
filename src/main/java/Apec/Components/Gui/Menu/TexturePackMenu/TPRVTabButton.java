package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class TPRVTabButton extends GuiButton {

    public boolean selected = false;
    public int id = -1;
    public TPTaggedDisplayElement parent;

    public TPRVTabButton(String text,int id) {
        super(0,0,0,50,10, text);
        this.id = id;
    }

    public void setParent (TPTaggedDisplayElement parent) {
        this.parent = parent;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            drawRect(xPosition,yPosition,xPosition+width,yPosition+height,(selected ? 0xaa000000 : 0x99303030));

            if (hovered) {
                drawRect(xPosition,yPosition,xPosition+width,yPosition+height,0x3adddddd);
            }

            if (selected) {
                drawRect(xPosition-1,yPosition + height,xPosition,yPosition,0xffffffff);
                drawRect(xPosition+width-1,yPosition + height,xPosition+width,yPosition,0xffffffff);
                drawRect(xPosition,yPosition-1,xPosition + width,yPosition,0xffffffff);
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.9,0.9,1);
            this.drawCenteredString(fontrenderer, this.displayString, (int)((this.xPosition + this.width / 2)/0.9f),(int)((this.yPosition + (this.height - 8) / 2 + 1)/0.9f),0xffffffff);
            GlStateManager.popMatrix();
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    public void onClick() {
        parent.setCurrent(id);
        this.selected = true;
    }

}
