package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class TPRVDropDownButton extends GuiButton {

    private TPDisplayElement parent;

    public TPRVDropDownButton(int buttonId, int x, int y, TPDisplayElement parent) {
        super(buttonId, x, y,parent.elementLength,parent.dropDownButtonHeight, "Download");
        this.parent = parent;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            drawRect(xPosition,yPosition,xPosition+width,yPosition+height,0xaa000000);
            if (hovered) {
                drawRect(xPosition,yPosition,xPosition+width,yPosition+height,0x3adddddd);
            }
            if (parent.showDescription) this.displayString = "Hide description";
            else this.displayString = "Show description";
            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2,this.yPosition + (this.height - 8) / 2,0xffffffff);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    public void toggleDescription() {
        parent.toggleDescritption();
    }

}
