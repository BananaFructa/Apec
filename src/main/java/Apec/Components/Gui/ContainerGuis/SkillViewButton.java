package Apec.Components.Gui.ContainerGuis;

import Apec.ApecMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class SkillViewButton extends GuiButton {

    public SkillViewComponent.Actions action;

    public SkillViewButton(int buttonId, int x, int y, int widthIn, int heightIn, SkillViewComponent.Actions action) {
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
                case BACK:
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 226, 30, 15, 15);
                    break;
                case CLOSE:
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 226, 45, 15, 15);
                    break;
                case PAGE_CHANGE:
                    List<String> pageLines = ((SkillViewGui) mc.currentScreen).getPageText();
                    for (int i = 0; i < pageLines.size(); i++) {
                        fontrenderer.drawString(pageLines.get(i), xPosition + 5, yPosition + 5 + i * 10, 0xffffff);
                    }
                    break;
            }

            if (this.hovered) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x1adddddd);

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

}
