package Apec.Components.Gui.Menu;

import Apec.ApecMain;
import Apec.Component;
import Apec.ComponentId;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.Settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApecMenu extends Component {

    protected Minecraft mc = Minecraft.getMinecraft();
    public Integer page = 0;

    public ApecMenu() {
        super(ComponentId.SETTINGS_MENU);
    }

    public static String getStringFromAction (NavigationAction action) {
        switch (action) {
            case BACK:
                return "<";
            case NEXT:
                return ">";
            case OPEN_GUI_EDITING:
                return "Customize Gui";
            default:
                return "N/A";
        }
    }

    @Override
    protected void onEnable()  {
        ApecMenuGui apecMenuGui = new ApecMenuGui(page);
        mc.displayGuiScreen(apecMenuGui);
        this.Toggle();
    }


    public static class ApecMenuGui extends GuiScreen {

        Integer page;

        public ApecMenuGui(Integer pageCounter) {
            this.page = pageCounter;
        }

        @Override
        public void initGui() {
            super.initGui();
            this.loadAtPage(page);
            ScaledResolution sr = new ScaledResolution(mc);
            int h = sr.getScaledHeight()/2;
            int w = sr.getScaledWidth()/2;
            this.buttonList.add(new ApecMenuNavigationButton(0,w-265,h-130,20,250,NavigationAction.BACK));
            this.buttonList.add(new ApecMenuNavigationButton(0,w+245,h-130,20,250,NavigationAction.NEXT));
            this.buttonList.add(new ApecMenuNavigationButton(0,5,5,85,23,NavigationAction.OPEN_GUI_EDITING));
        }

        public void executeNavigation(NavigationAction action) {
            switch (action) {
                case BACK:
                    if (page > 0)
                    page = page - 1;
                    break;
                case NEXT:
                    if (page < ApecMain.Instance.settingsManager.settings.size() / 12)
                    page = page + 1;
                    break;
                case OPEN_GUI_EDITING:
                    mc.displayGuiScreen(new CustomizationGui());
                    break;
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            ScaledResolution sr = new ScaledResolution(mc);
            int h = sr.getScaledHeight()/2;
            int w = sr.getScaledWidth()/2;
            final int spaceBetweenLines = 60, spaceBetweenRows = 160;
            drawRect(w-245,h-130,w+245,h+120,0x990a0a0a);
            for (int i = page*12;i < ApecMain.Instance.settingsManager.settings.size() && i < (page+1)*12;i++) {
                Setting s = ApecMain.Instance.settingsManager.settings.get(i);
                boolean enabled = ApecMain.Instance.settingsManager.settings.get(i).enabled;

                int x = w-235+((i%12)%3)*160;
                int y = h-120 + spaceBetweenLines * ((i%12)/3);

                drawRectangleAt(x,y,15,5,mouseX,mouseY);

                GlStateManager.pushMatrix();
                GlStateManager.scale(1.1f,1.1f,1.1f);

                mc.fontRendererObj.drawString(s.name,(int)((x+7) / 1.1f),(int)((y+6)  / 1.1f), enabled ? 0x00ff00 : 0xff0000);

                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.8f,0.8f,0.8f);

                drawWrappedString(s.description,(int)((x+7) / 0.8f),(int)((y + 18) / 0.8f),0xffffff);

                GlStateManager.popMatrix();
            }
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        public void drawWrappedString(String s,int x,int y,int c) {
            final int widthToWrap = 110;
            ArrayList<String> lines = new ArrayList<String>();
            int il = 0;
            for (char ch : s.toCharArray()) {
                if (lines.size() == il) lines.add("");
                lines.set(il,lines.get(il).concat(String.valueOf(ch)));
                if (mc.fontRendererObj.getStringWidth(lines.get(il)) >= widthToWrap && ch == ' ') il++;
            }
            for (int i = 0;i < lines.size();i++) {
                mc.fontRendererObj.drawString(lines.get(i),x,y + (int)(i*10/0.8f),c);
            }
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }

        public void drawRectangleAt(int x,int y,int w,int h,int mX,int mY) {
            drawRect(x,y,x+w*10,y+h*10,0x99151515);
            for (int i = 0;i < w;i++) {
                drawLineComponent(x + i *10,y-1,x + (i+1)*10,y+1,mX,mY);
                drawLineComponent(x + i *10,y-1 + h*10,x + (i+1)*10,y+1+ h*10,mX,mY);
            }
            for (int i = 0;i < h;i++) {
                drawLineComponent(x-1,y+i*10,x+ 1,y+(i+1)*10,mX,mY);
                drawLineComponent(x-1+ w*10,y+i*10,x + 1 + w*10,y+(i+1)*10,mX,mY);
            }
        }

        private void drawLineComponent(int left,int top,int right,int bottom,int mX,int mY) {
            double range = 45;
            double dist =  Math.sqrt(Math.pow(left - mX,2) +  Math.pow(top - mY,2));
            if (dist > range) dist = range;
            drawRect(left,top,right,bottom,0xffffff | ((int)(0xff * ((range-dist)/range)) << 24));
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            boolean needsToReloadButtons = false;
            if (mouseButton == 0) for (net.minecraft.client.gui.GuiButton guiButton : this.buttonList) {
                if (guiButton instanceof ApecMenuButton)
                if (guiButton.mousePressed(mc, mouseX, mouseY)) ((ApecMenuButton) guiButton).ToggleSetting();
                if (guiButton instanceof ApecMenuNavigationButton)
                if (guiButton.mousePressed(mc, mouseX, mouseY)) {
                    ((ApecMenuNavigationButton)guiButton).ExecuteAction();
                    needsToReloadButtons = true;
                }
            }
            if (needsToReloadButtons) this.loadAtPage(page);
        }

        private void loadAtPage(int page) {
            ScaledResolution sr = new ScaledResolution(mc);
            int h = sr.getScaledHeight()/2;
            int w = sr.getScaledWidth()/2;
            List<GuiButton> buttonToRemove = new ArrayList<GuiButton>();
            for (GuiButton guiButton : this.buttonList) {
                if (guiButton instanceof ApecMenuButton)  buttonToRemove.add(guiButton);
            }
            for (GuiButton guiButton : buttonToRemove) {
                this.buttonList.remove(guiButton);
            }
            for (int i = page*12;i < ApecMain.Instance.settingsManager.settings.size() && i < (page+1)*12;i++) {
                buttonList.add(new ApecMenuButton(i,w-235+((i%12)%3)*160,h-120 + 60*((i%12)/3),15*10,5*10, ApecMain.Instance.settingsManager.settings.get(i)));
            }
        }

    }

}
