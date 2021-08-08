package Apec.Components.Gui.Menu.TexturePackMenu;

import Apec.ApecMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.net.URI;

public class TPRVConnectionButton extends GuiButton {

    TPRVConnectionType mode;
    String urlString;

    public TPRVConnectionButton(TPRVConnectionType mode, String urlString) {
        super(0,0,0,14,14, "");
        this.mode = mode;
        this.urlString = urlString;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            String logoType = "web";

            switch (mode) {
                case DISCORD:
                    logoType = "dd";
                    break;
                case TWITTER:
                    logoType = "tw";
                    break;
                case YOUTUBE:
                    logoType = "yt";
                    break;
                case WEB_PAGE:
                    logoType = "web";
                    break;
                case FORUM_PAGE:
                    logoType = "hy";
                    break;
            }

            GlStateManager.pushMatrix();

            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId,"gui/logos/" + logoType + "_logo.png"));
            float scale = 14f/256f;

            GlStateManager.scale(scale,scale,1);

            mc.currentScreen.drawTexturedModalRect(this.xPosition/scale,this.yPosition/scale,0,0,256,256);

            GlStateManager.popMatrix();

            if (hovered) {
                drawRect(xPosition,yPosition,xPosition+width,yPosition+height,0x3adddddd);
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.9,0.9,1);
            this.drawCenteredString(fontrenderer, this.displayString, (int)((this.xPosition + this.width / 2)/0.9f),(int)((this.yPosition + (this.height - 8) / 2 + 1)/0.9f),0xffffffff);
            GlStateManager.popMatrix();
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    public void onClick() {
        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new Object[]{new URI(urlString)});
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

}
