package Apec.Components.Gui.Menu.SettingsMenu;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Component;
import Apec.ComponentId;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.Settings.Setting;
import Apec.Settings.SettingsManager;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

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
            case SEARCH:
                return "Open Seach Bar";
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
        GuiTextField SearchBox;
        ApecMenuNavigationButton GuiCustomizationButton;
        ApecMenuNavigationButton ShowSearchBoxButton;
        boolean ShowSearchBox = false;

        /** Separate list of settings so it can be used for sorting search results */
        List<Setting> Settings = new ArrayList<Setting>();

        int SearchBoxAnimationStartingWidth = 30;
        /** Used for the sole reason that the width is stored as an int and decimal increments are needed for it to be smooth*/
        float WidthSearchBoxAnimation = SearchBoxAnimationStartingWidth;
        float XPosCustomizationButtonAnimation = -90;

        public ApecMenuGui(Integer pageCounter) {
            this.page = pageCounter;
        }

        @Override
        public void initGui() {
            super.initGui();
            ScaledResolution sr = new ScaledResolution(mc);
            int h = sr.getScaledHeight()/2;
            int w = sr.getScaledWidth()/2;
            this.buttonList.add(new ApecMenuNavigationButton(0,w-265,h-130,20,250,NavigationAction.BACK));
            this.buttonList.add(new ApecMenuNavigationButton(0,w+245,h-130,20,250,NavigationAction.NEXT));
            GuiCustomizationButton = new ApecMenuNavigationButton(0,-90,5,85,23,NavigationAction.OPEN_GUI_EDITING);
            this.buttonList.add(GuiCustomizationButton);
            ShowSearchBoxButton = new ApecMenuNavigationButton(0,w-265,h-145,120,15,NavigationAction.SEARCH);
            this.buttonList.add(ShowSearchBoxButton);
            SearchBox = new GuiTextField(0,mc.fontRendererObj,w-265,h-150,SearchBoxAnimationStartingWidth,15);//150

            // Initiating the setting list
            Settings.addAll(ApecMain.Instance.settingsManager.settings);
            this.loadAtPage(page);

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
                case SEARCH:
                    OpenSearchBox();
                    break;
            }
        }

        private void OpenSearchBox() {
            ShowSearchBox = true;
            ShowSearchBoxButton.visible = false;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            ScaledResolution sr = new ScaledResolution(mc);
            int h = sr.getScaledHeight()/2;
            int w = sr.getScaledWidth()/2;
            final int spaceBetweenLines = 60, spaceBetweenRows = 160;
            drawRect(w-245,h-130,w+245,h+120,0x990a0a0a);
            for (int i = page*12;i < Settings.size() && i < (page+1)*12;i++) {
                Setting s = Settings.get(i);
                boolean enabled = Settings.get(i).enabled;

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
            if (ShowSearchBox) {
                if(SearchBox.width < 150) {
                    float fps = Minecraft.getDebugFPS();
                    float DeltaTime = 120.0f/fps;
                    WidthSearchBoxAnimation += 3f * DeltaTime;
                    SearchBox.width = (int) WidthSearchBoxAnimation;
                }
                if (SearchBox.width > 150) SearchBox.width = 150;
                SearchBox.drawTextBox();
            }
            if (GuiCustomizationButton.xPosition < 5) {
                float fps = Minecraft.getDebugFPS();
                float DeltaTime = 120.0f/fps;
                XPosCustomizationButtonAnimation += 4f * DeltaTime;
                GuiCustomizationButton.xPosition = (int)XPosCustomizationButtonAnimation;
            }
            if (GuiCustomizationButton.xPosition > 5) GuiCustomizationButton.xPosition = 5;
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

        private void SearchFor(String searchTerm) {
            if (searchTerm.length() != 0) {
                Integer[] Scores = new Integer[Settings.size()];
                int SettingCount = Settings.size();
                String[] SearchWords = searchTerm.split(" ");
                for (int i = 0; i < SettingCount; i++) {
                    String[] SettingWords = SettingsManager.settingData.get(Settings.get(i).settingID).getFirst().toLowerCase().split(" ");

                    int TotalScore = 0;
                    for (int w = 0;w < SearchWords.length;w++) {
                        char[] SearchTermC = SearchWords[w].toCharArray();
                        if (SearchTermC.length == 0) continue;
                        for (int j = 0; j < SettingWords.length; j++) {
                            char[] Word = SettingWords[j].toCharArray();
                            if (Word.length == 0) continue;
                            int Score = 0;
                            int CharactersFound = 0;
                            boolean LastChracterVald = false;
                            for (int l = 0; l < Word.length; l++) {
                                if (Word[l] == SearchTermC[CharactersFound]) {
                                    // Give a large score to names that match portions of the exact sequence
                                    Score += 1 + (LastChracterVald ? 99 : 0);
                                    // Gives a bonus if it's the start
                                    if (l == 0) {
                                        Score += Math.max(10 - j, 0);
                                    }
                                    CharactersFound++;
                                    LastChracterVald = true;
                                } else {
                                    LastChracterVald = false;
                                    Score--;
                                }
                                if (CharactersFound == SearchTermC.length) break;
                            }
                            TotalScore += Score;
                        }
                    }
                    Scores[i] = TotalScore;
                }

                ApecUtils.bubbleSort(Lists.newArrayList(Scores), Settings);

            } else {
                // Resets the order
                Settings.clear();
                Settings.addAll(ApecMain.Instance.settingsManager.settings);
            }
            page = 0;
            loadAtPage(page);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            if (ShowSearchBox) {
                this.SearchBox.textboxKeyTyped(typedChar,keyCode);
                SearchFor(this.SearchBox.getText().toLowerCase());
            }
        }

        @Override
        public void updateScreen() {
            if (ShowSearchBox) this.SearchBox.updateCursorCounter();
            super.updateScreen();
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
            if (ShowSearchBox) SearchBox.mouseClicked(mouseX,mouseY,mouseButton);
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
            for (int i = page*12;i < Settings.size() && i < (page+1)*12;i++) {
                buttonList.add(new ApecMenuButton(i,w-235+((i%12)%3)*160,h-120 + 60*((i%12)/3),15*10,5*10, Settings.get(i)));
            }
        }

    }

}