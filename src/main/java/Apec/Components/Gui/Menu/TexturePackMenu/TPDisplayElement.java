package Apec.Components.Gui.Menu.TexturePackMenu;

import Apec.Utils.ApecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TPDisplayElement implements ITPDrawableElement {

    protected TPData texturepack;
    protected DynamicTexture iconTexture;
    protected ResourceLocation icon;
    protected TPRVDownloadButton dbutton;
    protected TPRVDropDownButton ddbutton;
    protected TexturePackRegistryViewer.TPRVGuiScreen parent;

    public final int elementLength = 300;
    public final int elementHeight = 50;
    public final int downloadSectionHeight = 10;
    public final int dropDownButtonHeight = 10;

    public int descriptionHeight = 0;

    public boolean hasDescription;
    public boolean hasDownload;
    public boolean showDescription;

    public boolean reqOptifine;
    public boolean reqNEU;

    public String[] description;
    public boolean visible = true;
    private boolean renderUpperGlow = true;

    public TPDisplayElement() {

    }

    public TPDisplayElement(TPData texturepack,TPRVDownloadButton button,TexturePackRegistryViewer.TPRVGuiScreen parent) {
        this.texturepack = texturepack;
        this.parent = parent;
        this.dbutton = button;
        this.hasDescription = !texturepack.description.equals("NULL");
        this.reqOptifine = texturepack.requiresOptifine.equals("TRUE");
        this.reqNEU = texturepack.requiresNeu.equals("TRUE");
        if (hasDescription) {
            List<String> lines = new ArrayList<String>();
            String[] nonFormattedLines = this.texturepack.description.split("\n");
            for (String s : nonFormattedLines) {
                List<String> formattedLine = ApecUtils.stringToSizedArray(Minecraft.getMinecraft(),s,elementLength - 20);
                lines.addAll(formattedLine);
            }
            Object[] arr = lines.toArray();
            description = Arrays.copyOf(arr,arr.length,String[].class);
            descriptionHeight = 10 + description.length * 10;
        }
    }

    //TODO:add paging

    public int draw(int y, int mouseX, int mouseY, ScaledResolution sr, Minecraft mc) {
        if (visible) {
            int _offset = getOffset();
            if (y > sr.getScaledHeight() || y + _offset < 0) {
                if (this.hasDescription) this.ddbutton.visible = false;
                this.dbutton.visible = false;
                return _offset;
            }

            if (this.hasDescription) this.ddbutton.visible = true;
            this.dbutton.visible = true;

            int width = sr.getScaledWidth() / 2;

            int cornerXLeft = width - elementLength / 2;
            int cornerXRight = width + elementLength / 2;
            int cornerYTop = y;
            int cornerYBottom = elementHeight + y;


            hasDownload = TexturePackRegistryViewer.hasDownload(texturepack);
            if (hasDownload) {
                DownloadProcess process = TexturePackRegistryViewer.getDownloadProcess(texturepack);
                parent.drawRect(cornerXLeft, cornerYTop, cornerXRight, cornerYTop + downloadSectionHeight, 0xaa000000);
                parent.drawRect(cornerXLeft + 3, cornerYTop + 3, (int) (cornerXLeft + 3 + ((cornerXRight - cornerXLeft - 6) * process.getProgress())), cornerYTop + downloadSectionHeight - 3, 0xff00910a);
                cornerYTop += downloadSectionHeight;
                cornerYBottom += downloadSectionHeight;
            }

            this.dbutton.xPosition = cornerXRight - 5 - dbutton.width;
            this.dbutton.yPosition = cornerYBottom - 5 - dbutton.height;

            parent.drawRect(cornerXLeft, cornerYTop, cornerXRight, cornerYBottom, 0xaa000000);

            if (hasDescription) {
                parent.drawRect(cornerXLeft, cornerYBottom - 1, cornerXRight, cornerYBottom, 0xffffffff);
                this.ddbutton.xPosition = cornerXLeft;
                this.ddbutton.yPosition = cornerYBottom;
                if (this.showDescription) {
                    parent.drawRect(cornerXLeft, cornerYBottom + dropDownButtonHeight, cornerXRight, cornerYBottom + dropDownButtonHeight + descriptionHeight, 0x991f1f1f);
                    for (int i = 0; i < description.length; i++) {
                        mc.fontRendererObj.drawString(description[i], cornerXLeft + 5, cornerYBottom + dropDownButtonHeight + 5 + i * 10, 0xffffffff);
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
                GlStateManager.scale(0.1875f, 0.1875f, 1); // scale by that to get a 48x48 image
                mc.renderEngine.bindTexture(icon);
                parent.drawTexturedModalRect((int) ((cornerXLeft + 1) / 0.1875f), (int) ((cornerYTop + 1) / 0.1875f), 0, 0, 256, 256);
                GlStateManager.popMatrix();
            }

            String reqText = null;

            if (reqOptifine && !reqNEU) {
                reqText = "\u00a7eRequires Optifine!";
            } else if (!reqOptifine && reqNEU) {
                reqText = "\u00a7eRequires NEU!";
            } else if (reqOptifine && reqNEU) {
                reqText = "\u00a7eRequires Optifine and NEU!";
            }

            if (reqOptifine || reqNEU) {
                mc.fontRendererObj.drawString(reqText, cornerXLeft + 5 + xShift, cornerYTop + 34, 0xffffffff);
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
                if (renderUpperGlow) {
                    drawLineComponent(boxBoundXLeft + j * 10, boxBoundYTop - 1, boxBoundXLeft + (j + 1) * 10, boxBoundYTop + 1, mouseX, mouseY);
                }
                drawLineComponent(boxBoundXLeft + j * 10, boxBoundYTop - 1 + boxBoundHeight, boxBoundXLeft + (j + 1) * 10, boxBoundYTop + 1 + boxBoundHeight, mouseX, mouseY);
            }
            for (int j = 0; j < boxBoundHeight / 10; j++) {
                drawLineComponent(boxBoundXLeft - 1, boxBoundYTop + j * 10, boxBoundXLeft + 1, boxBoundYTop + (j + 1) * 10, mouseX, mouseY);
                drawLineComponent(boxBoundXLeft - 1 + boxBoundLength, boxBoundYTop + j * 10, boxBoundXLeft + 1 + boxBoundLength, boxBoundYTop + (j + 1) * 10, mouseX, mouseY);
            }

        }

        return getOffset();
    }

    /** Used i case is part of a tab menu */
    public void removeUpperBorderGlow() {
        renderUpperGlow = false;
    }

    public void setDdbutton(TPRVDropDownButton button) {
        this.ddbutton = button;
    }

    public int getOffset() {
        return 5 + this.elementHeight + (this.hasDownload ? downloadSectionHeight : 0) + (this.hasDescription ? dropDownButtonHeight : 0) + (this.showDescription ? descriptionHeight : 0);
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
        if (iconTexture != null) {
            iconTexture.deleteGlTexture();
            iconTexture = null;
            icon = null;
        }
    }

    public void toggleDescritption() {
        this.showDescription = !this.showDescription;
    }

    public void checkIfInstalled() {
        this.dbutton.checkIfInstalled();
    }

    public void hide() {
        visible = false;
        dbutton.visible = false;
        dbutton.xPosition = 99999;
        if (hasDescription) {
            ddbutton.visible = false;
            ddbutton.xPosition = 99999;
        }
    }

    public void show() {
        visible = true;
        dbutton.visible = true;
        if (hasDescription) {
            ddbutton.visible = true;
        }
    }

}
