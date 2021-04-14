package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class TPDisplayElement {

    private TPData texturepack;
    private DynamicTexture iconTexture;
    private ResourceLocation icon;
    private TPRVDownloadButton dbutton;
    private TPRVDropDownButton ddbutton;
    private TexturePackRegistryViewer.TPRVGuiScreen parent;

    public final int elementLength = 300;
    public final int elementHeight = 50;
    public final int downloadSectionHeight = 10;
    public final int dropDownButtonHeight = 10;

    public int descriptionHeight = 0;

    public boolean hasDescription;
    public boolean hasDownload;
    public boolean showDescription;

    public TPDisplayElement(TPData texturepack,TPRVDownloadButton button,TexturePackRegistryViewer.TPRVGuiScreen parent) {
        this.texturepack = texturepack;
        this.parent = parent;
        this.dbutton = button;
        this.hasDescription = !texturepack.description.equals("NULL");
        if (hasDescription) {
            descriptionHeight = 10 + this.texturepack.description.split("\n").length * 10;
        }
    }

    //TODO:add installed feature
    //TODO:add scrolling
    //TODO:add paging

    public int draw(int y, int mouseX, int mouseY, ScaledResolution sr, Minecraft mc) {
        int offset = 0;

        int width = sr.getScaledWidth() / 2;

        int cornerXLeft = width - elementLength / 2;
        int cornerXRight = width + elementLength / 2;
        int cornerYTop = y;
        int cornerYBottom = elementHeight + y;


        hasDownload = TexturePackRegistryViewer.nameToDownloadProcess.containsKey(texturepack.name);
        if (hasDownload) {
            DownloadProcess process = TexturePackRegistryViewer.nameToDownloadProcess.get(texturepack.name);
            parent.drawRect(cornerXLeft, cornerYTop, cornerXRight, cornerYTop + downloadSectionHeight, 0xaa000000);
            parent.drawRect(cornerXLeft + 2,cornerYTop + 2,(int)(cornerXLeft + 2 + ((cornerXRight - cornerXLeft - 4) * process.getProgress())),cornerYTop + downloadSectionHeight - 2,0xff00910a);
            cornerYTop += downloadSectionHeight;
            cornerYBottom += downloadSectionHeight;
            offset += downloadSectionHeight;
        }

        this.dbutton.xPosition = cornerXRight - 5 - dbutton.width;
        this.dbutton.yPosition = cornerYBottom - 5 - dbutton.height;

        parent.drawRect(cornerXLeft, cornerYTop, cornerXRight, cornerYBottom, 0xaa000000);

        if (hasDescription) {
            offset += dropDownButtonHeight;
            parent.drawRect(cornerXLeft,cornerYBottom - 1,cornerXRight,cornerYBottom,0xffffffff);
            this.ddbutton.xPosition = cornerXLeft;
            this.ddbutton.yPosition = cornerYBottom;
            if (this.showDescription) {
                offset += descriptionHeight;
                parent.drawRect(cornerXLeft,cornerYBottom + dropDownButtonHeight,cornerXRight,cornerYBottom + dropDownButtonHeight + descriptionHeight,0x991f1f1f);
                String[] descriptionLines = this.texturepack.description.split("\n");
                for (int i = 0; i < descriptionLines.length;i++) {
                    mc.fontRendererObj.drawString(descriptionLines[i],cornerXLeft + 5,cornerYBottom + dropDownButtonHeight + 5 + i * 10,0xffffffff);
                }
            }
        }

        boolean hasIcon = !texturepack.iconUrl.equals("NULL") && icon != null;
        int xShift = (hasIcon ? 50 : 0);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.2f, 1.2f, 1);
        mc.fontRendererObj.drawString(texturepack.name, (int) ((cornerXLeft + 5 + xShift) / 1.2f), (int) ((cornerYTop + 5) / 1.2f), 0xffffffff);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.9f, 0.9f, 1);
        mc.fontRendererObj.drawString("by " + texturepack.author, (int) ((cornerXLeft + 5 + xShift) / 0.9f), (int) ((cornerYTop + 19) / 0.9f), 0xffffffff);
        GlStateManager.popMatrix();

        if (hasIcon) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.1875f,0.1875f,1); // scale by that to get a 48x48 image
            mc.renderEngine.bindTexture(icon);
            parent.drawTexturedModalRect((int)((cornerXLeft + 1)/0.1875f),(int)((cornerYTop + 1)/0.1875f),0,0,256,256);
            GlStateManager.popMatrix();
        }

        if (texturepack.requiresOptifine.equals("TRUE")) {
            mc.fontRendererObj.drawString("\u00a7eRequires Optifine!", cornerXLeft + 5 + xShift, cornerYTop + 34, 0xffffffff);
        }

        if (!texturepack.version.equals("NULL")) {
            mc.fontRendererObj.drawString(texturepack.version, cornerXRight - 5 - mc.fontRendererObj.getStringWidth(texturepack.version), cornerYTop + 5, 0xffffffff);
        }

        int boxBoundLength = elementLength;
        int boxBoundHeight = elementHeight + (this.hasDescription ? this.dropDownButtonHeight : 0) +
                                             (this.hasDownload ? this.downloadSectionHeight : 0) +
                                             (this.showDescription && this.hasDescription ? this.descriptionHeight : 0);
        int boxBoundXLeft = cornerXLeft;
        int boxBoundYTop = cornerYTop + (this.hasDownload ? -this.downloadSectionHeight : 0);

        for (int j = 0; j < boxBoundLength / 10; j++) {
            drawLineComponent(boxBoundXLeft + j * 10, boxBoundYTop - 1, boxBoundXLeft + (j + 1) * 10, boxBoundYTop + 1, mouseX, mouseY);
            drawLineComponent(boxBoundXLeft + j * 10, boxBoundYTop - 1 + boxBoundHeight, boxBoundXLeft + (j + 1) * 10, boxBoundYTop + 1 + boxBoundHeight, mouseX, mouseY);
        }
        for (int j = 0; j < boxBoundHeight / 10; j++) {
            drawLineComponent(boxBoundXLeft - 1, boxBoundYTop + j * 10, boxBoundXLeft + 1, boxBoundYTop + (j + 1) * 10, mouseX, mouseY);
            drawLineComponent(boxBoundXLeft - 1 +boxBoundLength, boxBoundYTop + j * 10, boxBoundXLeft + 1 + boxBoundLength, boxBoundYTop + (j + 1) * 10, mouseX, mouseY);
        }

        return offset;
    }

    public void setDdbutton(TPRVDropDownButton button) {
        this.ddbutton = button;
    }

    private void drawLineComponent(int left,int top,int right,int bottom,int mX,int mY) {
        double range = 45*45;
        double dist =  Math.pow(left - mX,2) +  Math.pow(top - mY,2);
        if (dist > range) dist = range;
        parent.drawRect(left,top,right,bottom,0xffffff | ((int)(0xff * ((range-dist)/range)) << 24));
    }

    public void loadIcon(final String registryTag,final Minecraft mc) {
        if (!texturepack.iconUrl.equals("NULL") && icon == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final BufferedImage image = TexturePackRegistryViewer.loadIcon(texturepack.iconUrl, registryTag);
                    mc.addScheduledTask(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (TexturePackRegistryViewer.threadLock) {
                                iconTexture = new DynamicTexture(image);
                                icon = mc.renderEngine.getDynamicTextureLocation(texturepack.iconUrl.replace(".png", ""), iconTexture);
                            }
                        }
                    });
                }
            }).start();
        }
    }

    public void unLoadAll(final Minecraft mc) {
        mc.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                synchronized (TexturePackRegistryViewer.threadLock) {
                    iconTexture.deleteGlTexture();
                }
            }
        });
    }

    public void toggleDescritption() {
        this.showDescription = !this.showDescription;
    }

}
