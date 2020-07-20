package Apec.Components.Gui.ContainerGuis;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Components.Gui.Menu.ApecMenu;
import Apec.Settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class AuctionHouseToggleButton extends GuiButton {

    boolean state = false;
    public AuctionHouseComponent.CategoryID categoryID;

    public AuctionHouseToggleButton(int buttonId, int x, int y, int widthIn, int heightIn, String s, AuctionHouseComponent.CategoryID categoryID) {
        super(buttonId, x, y, widthIn, heightIn, s);
        this.state = categoryID == ((AuctionHouseComponent)ApecMain.Instance.getComponent(ComponentId.AUCTION_HOUSE_MENU)).currentCategory;
        this.categoryID = categoryID;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            if (this.hovered && !this.state) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x1adddddd);
            if (this.state) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0xaa353535);

            this.mouseDragged(mc, mouseX, mouseY);

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2,  14737632);
        }
    }

    public void Toggle() {
        this.state = !this.state;
    }

    public boolean getState() {
        return this.state;
    }

}
