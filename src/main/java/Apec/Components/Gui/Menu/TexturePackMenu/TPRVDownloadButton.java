package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.io.File;

public class TPRVDownloadButton extends GuiButton {

    private TPData correspondingData;
    private boolean alreadyInstalled = false;
    private boolean isInstalling = false;

    public TPRVDownloadButton(int buttonId, int x, int y, TPData data) {
        super(buttonId, x, y,50,13, "Download");
        this.correspondingData = data;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            isInstalling = TexturePackRegistryViewer.nameToDownloadProcess.containsKey(correspondingData.name + (!correspondingData.tag.equals("NULL") ? correspondingData.tag : ""));

            if (!alreadyInstalled && !isInstalling) {
                this.displayString = "Download";
                drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xff00910a);
            } else if (alreadyInstalled) {
                if (hovered) {
                    drawRect(xPosition, yPosition, xPosition + width, yPosition + height,0xffc70808);
                    this.displayString = "Uninstall";
                } else {
                    this.displayString = "Installed";
                    drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xff696969); // nice
                }
            } else if (isInstalling) {
                this.displayString = "Installing...";
                drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xff265ebf);
            }

            if (hovered) {
                drawRect(xPosition,yPosition,xPosition+width,yPosition+height,0x3adddddd);
            }
            this.mouseDragged(mc, mouseX, mouseY);
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.9,0.9,1);
            this.drawCenteredString(fontrenderer, this.displayString, (int)((this.xPosition + this.width / 2)/0.9f), (int)((this.yPosition + (this.height - 8) / 2)/0.9f), 0xffffffff);
            GlStateManager.popMatrix();
        }
    }

    public void onClick() {
        if (this.visible) {
            if (!alreadyInstalled && !isInstalling) {
                TexturePackRegistryViewer.startNewDownload(correspondingData);
            }
            if (alreadyInstalled && hovered) {
                if (new File("resourcepacks/" + correspondingData.expectedFileName).exists()) {
                    new File("resourcepacks/" + correspondingData.expectedFileName).delete();
                    checkIfInstalled();
                }
            }
        }
    }

    public void checkIfInstalled() {
        this.alreadyInstalled = new File("resourcepacks/"+correspondingData.expectedFileName).exists() &&
                                !TexturePackRegistryViewer.nameToDownloadProcess.containsKey(correspondingData.name);
    }
}
