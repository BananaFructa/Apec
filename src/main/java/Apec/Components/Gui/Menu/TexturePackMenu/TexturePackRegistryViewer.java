package Apec.Components.Gui.Menu.TexturePackMenu;

import Apec.Component;
import Apec.ComponentId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

public class TexturePackRegistryViewer extends Component {

    private final Minecraft mc = Minecraft.getMinecraft();

    public TexturePackRegistryViewer() {
        super(ComponentId.TEXTURE_PACK_REGISTRY_VIEWER);
    }

    @Override
    protected void onEnable() {
        mc.displayGuiScreen(new TPRVGuiScreen());
        this.Toggle();
    }

    public static class TPRVGuiScreen extends GuiScreen {

        public List<TPData> loadedTexturePacks = new ArrayList<TPData>();

        public final int elementLength = 300;
        public final int elementHeight = 50;

        public TPRVGuiScreen() {
            loadedTexturePacks.add(new TPData("RNBW+ Resource Pack","rainbowcraft2","NULL","v0.5.0","","TRUE","NULL","RNBW+ v0.5.zip"));
        }

        @Override
        public void initGui() {
            super.initGui();
            final int buttonWidth = 50;
            final int buttonHeight = 13;
            ScaledResolution sr = new ScaledResolution(mc);
            int width = sr.getScaledWidth()/2;
            for (int i = 0;i < loadedTexturePacks.size();i++) {
                int cornerX = width+elementLength/2;
                int cornerY = (20 + elementHeight) + i * (elementHeight + 5);
                this.buttonList.add(new TPRVDownloadButton(0,cornerX - 5 - buttonWidth,cornerY - 5 - buttonHeight,buttonWidth,buttonHeight,loadedTexturePacks.get(i)));
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            ScaledResolution sr = new ScaledResolution(mc);
            drawRect(0,0,sr.getScaledWidth(),15, 0xff000000);
            mc.fontRendererObj.drawString("Apec Texture Pack Registry",2,3,0xffffffff);
            int width = sr.getScaledWidth()/2;
            for (int i = 0;i < loadedTexturePacks.size();i++) {
                TPData texturepack = loadedTexturePacks.get(i);

                drawRect(width-elementLength/2,20 + i * (elementHeight + 5),width + elementLength/2,(20 + elementHeight) + i * (elementHeight + 5),0xaa000000);

                int cornerX = width-elementLength/2;
                int cornerY = 20 + i * (elementHeight + 5);

                GlStateManager.pushMatrix();
                GlStateManager.scale(1.2f,1.2f,1);
                mc.fontRendererObj.drawString(texturepack.name,(int)((cornerX + 5)/1.2f),(int)((cornerY + 5)/1.2f),0xffffffff);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.9f,0.9f,1);
                mc.fontRendererObj.drawString("by " + texturepack.author,(int)((cornerX + 5)/0.9f),(int)((cornerY + 19)/0.9f),0xffffffff);
                GlStateManager.popMatrix();

                if (texturepack.requiresOptifine.equals("TRUE")) {
                    mc.fontRendererObj.drawString("\u00a7eRequires Optifine!",cornerX + 5,cornerY + 34,0xffffffff);
                }

                int x = width-elementLength/2;
                int y = 20 + i * (elementHeight + 5);
                for (int j = 0;j < elementLength/10;j++) {
                    drawLineComponent(x + j *10,y-1,x + (j+1)*10,y+1,mouseX,mouseY);
                    drawLineComponent(x + j *10,y-1 + elementHeight,x + (j+1)*10,y+1+ elementHeight,mouseX,mouseY);
                }
                for (int j = 0;j < elementHeight/10;j++) {
                    drawLineComponent(x-1,y+j*10,x+ 1,y+(j+1)*10,mouseX,mouseY);
                    drawLineComponent(x-1+ elementLength,y+j*10,x + 1 + elementLength,y+(j+1)*10,mouseX,mouseY);
                }

            }
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        private void drawLineComponent(int left,int top,int right,int bottom,int mX,int mY) {
            double range = 45;
            double dist =  Math.sqrt(Math.pow(left - mX,2) +  Math.pow(top - mY,2));
            if (dist > range) dist = range;
            drawRect(left,top,right,bottom,0xffffff | ((int)(0xff * ((range-dist)/range)) << 24));
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }

    }

}
