package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class TRPVNavigationButton extends GuiButton {

    public TexturePackRegistryViewer.Actions action;

    public TRPVNavigationButton(TexturePackRegistryViewer.Actions action) {
        super(0,0,0,0,15, "");
        this.action = action;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            if (hovered) {
                drawRect(xPosition,yPosition,xPosition+width,yPosition+height,0x3adddddd);
            }

            switch (action) {
                case NEXT_PAGE:
                    this.displayString = "Next >";
                    break;
                case PREVIOUS_PAGE:
                    this.displayString = "< Back";
                    break;
            }

            this.width = mc.fontRendererObj.getStringWidth(this.displayString) + 2;

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2,this.yPosition + (this.height - 8) / 2,0xffffffff);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

}
