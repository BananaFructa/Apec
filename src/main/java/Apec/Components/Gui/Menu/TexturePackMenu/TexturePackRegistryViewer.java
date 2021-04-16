package Apec.Components.Gui.Menu.TexturePackMenu;

import Apec.ApecMain;
import Apec.Components.Gui.Menu.MessageBox;
import Apec.Components.Gui.Menu.SettingsMenu.ApecMenu;
import Apec.DataInterpretation.ComponentSaveManager;
import Apec.Utils.ApecUtils;
import Apec.Component;
import Apec.ComponentId;
import Apec.Utils.ParameterizedRunnable;
import Apec.Utils.VersionChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TexturePackRegistryViewer extends Component {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public TexturePackRegistryViewer() {
        super(ComponentId.TEXTURE_PACK_REGISTRY_VIEWER);
    }

    public boolean hasShownMessge = false;

    @Override
    public void init() {
        if (DataToSave.containsKey(0)) {
            hasShownMessge = DataToSave.get(0).equals("true");
        }
    }

    public void disableMessageNextTime() {
        hasShownMessge = true;
        DataToSave.put(0,(hasShownMessge ? "true" : "false"));
        ApecMain.Instance.componentSaveManager.IssueSave();
    }

    @Override
    protected void onEnable() {
        if (!hasShownMessge) {
            disableMessageNextTime();
            mc.displayGuiScreen(new MessageBox(
                    "Before continuing we would like to state the fact that ANYONE can publish their texturepack in the Apec Texture Pack Registry," +
                            " so if you have your own texturepack we would be happy to publish it here.You can publish a texturepack by joining the Apec discord server" +
                            " and submitting a request in #texture-pack-registry-requests.",
                    new TPRVGuiScreen()));
        } else {
            mc.displayGuiScreen(new TPRVGuiScreen());
        }
        this.Toggle();
    }

    public static enum Actions {
        NEXT_PAGE,
        PREVIOUS_PAGE
    }

    private static final String databaseUrl = "https://cdn.jsdelivr.net/gh/BananaFructa/Apec-DATA@__TAG__/TexturePackRegistry/database-list.txt";
    private static final String iconsetUrl = "https://cdn.jsdelivr.net/gh/BananaFructa/Apec-DATA@__TAG__/TexturePackRegistry/Icons/";

    public static List<DownloadProcess> activeDownloads = new ArrayList<DownloadProcess>();
    public static HashMap<String,DownloadProcess> nameToDownloadProcess = new HashMap<String, DownloadProcess>();

    public static final Object threadLock = new Object();

    private static List<String> loadDataBases(String registryTag) {
        try {
            TPDataReader reader = new TPDataReader(new URL(ApecUtils.applyTagOnUrl(databaseUrl,registryTag)));
            return reader.readAllDatabaseUrls();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    private static List<TPData> getAllTexturePacksFromDataBase(String url,String registryTag) {
        try {
            url = ApecUtils.applyTagOnUrl(url,registryTag);
            TPDataReader reader = new TPDataReader(new URL(url));
            List<TPData> texturePacks = new ArrayList<TPData>();
            TPData tp;
            while((tp = reader.readNextTPData()) != null) {
                texturePacks.add(tp);
            }
            return texturePacks;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new ArrayList<TPData>();
        }
    }

    public static BufferedImage loadIcon(String iconName,String registryTag) {
        try {
            String fullUrl = ApecUtils.applyTagOnUrl(iconsetUrl + iconName,registryTag);
            return ImageIO.read(new URL(fullUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void startNewDownload(TPData data) {
        if (!data.downloadUrl.equals("NULL")) {
            try {
                final DownloadProcess downloadProcess = new DownloadProcess(new URL(data.downloadUrl),data.name,data.expectedFileName);
                downloadProcess.startDownload();
                activeDownloads.add(downloadProcess);
                nameToDownloadProcess.put(data.name,downloadProcess);
                downloadProcess.setCallback(new ParameterizedRunnable<Integer>() {
                    @Override
                    public void run(Integer... parameter) {
                        synchronized (threadLock) {
                            activeDownloads.remove(downloadProcess);
                            nameToDownloadProcess.remove(downloadProcess.tpname,downloadProcess);
                            if (mc.currentScreen instanceof TPRVGuiScreen) {
                                ((TPRVGuiScreen)mc.currentScreen).checkForAlreadyInstalledTPs();
                            }
                        }
                    }
                });
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public static class TPRVGuiScreen extends GuiScreen {

        private List<TPDisplayElement> elements = new ArrayList<TPDisplayElement>();
        int scrollOffset = 0;
        public boolean finishedLoading = false;

        public TRPVNavigationButton nextButton,previousButton;

        public List<String> dataBaseUrls;
        private String registryTag;
        private int totalPages = 0;
        private int currentPage = 0;

        public TPRVGuiScreen() {

        }

        @Override
        public void initGui() {
            super.initGui();
            if (elements.isEmpty()) {
                nextButton = new TRPVNavigationButton(Actions.NEXT_PAGE);
                previousButton = new TRPVNavigationButton(Actions.PREVIOUS_PAGE);
                this.buttonList.add(nextButton);
                this.buttonList.add(previousButton);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        registryTag = VersionChecker.getRegistryTag();
                        dataBaseUrls = loadDataBases(registryTag);
                        totalPages = dataBaseUrls.size();
                        if (dataBaseUrls.size() > 0) {
                            currentPage = 0;
                            loadDataset(0);
                        }
                    }
                }).start();
            }
        }

        @Override
        public void onResize(Minecraft mcIn, int p_175273_2_, int p_175273_3_) {
            this.mc = mcIn;
            this.itemRender = mc.getRenderItem();
            this.fontRendererObj = mc.fontRendererObj;
            this.width = p_175273_2_;
            this.height = p_175273_3_;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            synchronized (threadLock) {
                drawButtons(mouseX,mouseY,TPRVDropDownButton.class);
                ScaledResolution sr = new ScaledResolution(mc);
                int yOffset = 0;
                for (int i = 0;i < elements.size();i++) {
                    yOffset += elements.get(i).draw(20 + yOffset - scrollOffset,mouseX,mouseY,sr,mc);
                }
                int limit = getMaxScrollOffset(yOffset,sr);
                if (scrollOffset > limit && finishedLoading) scrollOffset = limit;
                drawButtons(mouseX,mouseY,TPRVDownloadButton.class);
                drawRect(0, 0, sr.getScaledWidth(), 15, 0xff000000);
                mc.fontRendererObj.drawString("Apec Texture Pack Registry", 2, 3, 0xffffffff);
                String pageText = (currentPage + 1) + "/" + totalPages;
                int pageTextWidth = mc.fontRendererObj.getStringWidth(pageText);
                int middle = sr.getScaledWidth()/2;
                previousButton.xPosition = middle - previousButton.width - 2 - pageTextWidth/2;
                mc.fontRendererObj.drawString(pageText,middle - pageTextWidth/2,3,0xffffffff);
                nextButton.xPosition = middle + pageTextWidth/2 + 2;
                drawButtons(mouseX,mouseY,TRPVNavigationButton.class);
            }
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            int i = Mouse.getEventDWheel();
            if (i < -1) {
                ScaledResolution sr = new ScaledResolution(mc);;
                int limit =  getMaxScrollOffset(sr);
                scrollOffset += (scrollOffset + 20 > limit ? -scrollOffset + limit : 20);
            } else if (i > 1) {
                scrollOffset -= (scrollOffset - 20 < 0 ? scrollOffset : 20);
            }
        }

        private void executeAction(Actions action) {
            switch (action) {
                case NEXT_PAGE:
                    if (currentPage < totalPages - 1) {
                        // currentPage is has the value 1 when on the first page
                        loadDataset(++currentPage);
                    }
                    break;
                case PREVIOUS_PAGE:
                    if (currentPage != 0) {
                        loadDataset(--currentPage);
                    }
                    break;
            }
        }

        public int getMaxScrollOffset(ScaledResolution sr) {
            return Math.max(getTotalHeight() - (sr.getScaledHeight() - 20), 0);
        }

        public int getMaxScrollOffset(int maxOffset,ScaledResolution sr) {
            return Math.max(maxOffset - (sr.getScaledHeight() - 20), 0);
        }

        private void loadDataset(final int index) {

            synchronized (threadLock) {
                unLoadTextureData();
                this.scrollOffset = 0;
                this.elements.clear();
                this.clearButtonsOfType(TPRVDownloadButton.class);
                this.clearButtonsOfType(TPRVDropDownButton.class);
            }

            final TexturePackRegistryViewer.TPRVGuiScreen instance = this;
            // Request thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    finishedLoading = false;
                    // Gets the text information about the texturepacks
                    try {
                        List<TPData> tps = getAllTexturePacksFromDataBase(dataBaseUrls.get(index), registryTag);

                        for (TPData tp : tps) {
                            synchronized (threadLock) {
                                TPRVDownloadButton button = new TPRVDownloadButton(0, 0, 0, tp);
                                buttonList.add(button);
                                TPDisplayElement element = new TPDisplayElement(tp, button, instance);
                                if (element.hasDescription) {
                                    TPRVDropDownButton dropDownButton = new TPRVDropDownButton(0,0,0,element);
                                    element.setDdbutton(dropDownButton);
                                    buttonList.add(dropDownButton);
                                }
                                elements.add(element);
                            }
                        }

                    } catch (Exception err) {
                        err.printStackTrace();
                    }

                    // Loads the icons
                    for (TPDisplayElement element : elements) {
                        synchronized (threadLock) {
                            try {
                                element.loadIcon(registryTag, mc);
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }
                    }

                    // Looks for already installed texturepacks
                    synchronized (threadLock) {
                        checkForAlreadyInstalledTPs();
                    }
                    finishedLoading = true;
                }
            }).start();
        }

        public void checkForAlreadyInstalledTPs() {
            for (TPDisplayElement element : elements) {
                element.checkIfInstalled();
            }
        }

        public int getTotalHeight() {
            int totalOffset = 0;
            for (TPDisplayElement element : elements) {
                totalOffset += element.getOffset();
            }
            return totalOffset;
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            if (mouseButton == 0) {
                for (GuiButton button : this.buttonList) {
                    if (button instanceof TPRVDownloadButton) {
                        if (button.mousePressed(mc, mouseX, mouseY)) {
                            ((TPRVDownloadButton) button).onClick();
                            break;
                        }
                    } else if (button instanceof TPRVDropDownButton) {
                        if (button.mousePressed(mc, mouseX, mouseY)) {
                            ((TPRVDropDownButton) button).toggleDescription();
                            break;
                        }
                    } else if (button instanceof TRPVNavigationButton) {
                        if (button.mousePressed(mc, mouseX, mouseY)) {
                            this.executeAction(((TRPVNavigationButton) button).action);
                            break;
                        }
                    }
                }
            }
        }

        private void drawButtons(int mouseX,int mouseY,Class<?> type) {
            for (int i = 0; i < this.buttonList.size(); ++i)
            {
                if (type.isInstance(this.buttonList.get(i))) ((GuiButton)this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY);
            }
        }

        private void clearButtonsOfType(Class<?> type) {
            List<GuiButton> toRemove = new ArrayList<GuiButton>();
            for (GuiButton button : this.buttonList) {
                if (type.isInstance(button)) toRemove.add(button);
            }
            this.buttonList.removeAll(toRemove);
        }

        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
            this.unLoadTextureData();
        }

        public void unLoadTextureData() {
            for (TPDisplayElement element : elements) {
                element.unLoadAll(mc);
            }
        }
    }

}