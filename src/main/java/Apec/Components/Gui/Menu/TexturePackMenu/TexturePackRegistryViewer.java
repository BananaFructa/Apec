package Apec.Components.Gui.Menu.TexturePackMenu;

import Apec.ApecMain;
import Apec.Components.Gui.Menu.MessageBox;
import Apec.Utils.ApecUtils;
import Apec.Component;
import Apec.ComponentId;
import Apec.Utils.ParameterizedRunnable;
import Apec.Utils.VersionChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
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
                            " and submitting a request in #tpr-requests.",
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

    public static boolean hasDownload(TPData data) {
        return nameToDownloadProcess.containsKey(data.name + (!data.tag.equals("NULL") ? data.tag : ""));
    }

    public static DownloadProcess getDownloadProcess(TPData data) {
        return nameToDownloadProcess.get(data.name + (!data.tag.equals("NULL") ? data.tag : ""));
    }

    public static void startNewDownload(TPData data) {
        if (!data.downloadUrl.equals("NULL")) {
            try {
                final DownloadProcess downloadProcess = new DownloadProcess(new URL(data.downloadUrl),data);
                downloadProcess.startDownload();
                activeDownloads.add(downloadProcess);
                nameToDownloadProcess.put(data.name + (!data.tag.equals("NULL") ? data.tag : ""),downloadProcess);
                downloadProcess.setCallback(new ParameterizedRunnable<Integer>() {
                    @Override
                    public void run(Integer parameter) {
                        synchronized (threadLock) {
                            activeDownloads.remove(downloadProcess);
                            nameToDownloadProcess.remove(downloadProcess.tpdata.name + (!downloadProcess.tpdata.tag.equals("NULL") ? downloadProcess.tpdata.tag : ""));
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
        private List<ITPDrawableElement> drawableElements = new ArrayList<ITPDrawableElement>();

        int scrollOffset = 0;
        public boolean finishedLoading = false;

        public TPRVNavigationButton nextButton,previousButton;

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
                nextButton = new TPRVNavigationButton(Actions.NEXT_PAGE);
                previousButton = new TPRVNavigationButton(Actions.PREVIOUS_PAGE);
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
                GlStateManager.pushMatrix();

                drawButtons(mouseX,mouseY,TPRVDropDownButton.class);

                ScaledResolution sr = new ScaledResolution(mc);
                int yOffset = 0;
                for (int i = 0;i < drawableElements.size();i++) {
                    yOffset += drawableElements.get(i).draw(20 + yOffset - scrollOffset,mouseX,mouseY,sr,mc);
                }

                int limit = getMaxScrollOffset(yOffset,sr);
                if (scrollOffset > limit && finishedLoading) scrollOffset = limit;

                drawButtons(mouseX,mouseY,TPRVDownloadButton.class);
                drawButtons(mouseX,mouseY,TPRVConnectionButton.class);

                drawRect(0, 0, sr.getScaledWidth(), 15, 0xff000000);
                String pageText = (currentPage + 1) + "/" + totalPages;
                int pageTextWidth = mc.fontRendererObj.getStringWidth(pageText);
                int middle = sr.getScaledWidth()/2;
                previousButton.xPosition = middle - previousButton.width - 2 - pageTextWidth/2;
                mc.fontRendererObj.drawString(pageText,middle - pageTextWidth/2,3,0xffffffff);
                nextButton.xPosition = middle + pageTextWidth/2 + 2;

                drawButtons(mouseX,mouseY, TPRVNavigationButton.class);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId,"gui/apecTprLogo.png"));
                float scaleFactor = 15f/28f;
                GlStateManager.scale(scaleFactor,scaleFactor,1);
                drawTexturedModalRect((int)(1/scaleFactor),0,0,0,214,28);
                GlStateManager.popMatrix();
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
                this.drawableElements.clear();
                this.clearButtonsOfType(TPRVDownloadButton.class);
                this.clearButtonsOfType(TPRVDropDownButton.class);
                this.clearButtonsOfType(TPRVTabButton.class);
                this.clearButtonsOfType(TPRVConnectionButton.class);
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

                                if (element.hasConnections) {
                                    String[] connections = tp.connections.split(" ");
                                    for (String connection : connections) {
                                        if (!connection.contains("^")) continue;
                                        String[] pair = connection.split("\\^");
                                        TPRVConnectionType type = TPRVConnectionType.parseConnection(pair[0]);
                                        TPRVConnectionButton cbutton = new TPRVConnectionButton(type,pair[1]);
                                        element.addConnectionButton(cbutton);
                                        buttonList.add(cbutton);
                                    }
                                }

                                elements.add(element);
                            }
                        }

                        HashMap<String, List<TPDisplayElement>> tagToElements = new HashMap<String, List<TPDisplayElement>>();
                        HashMap<Integer,String> reservedElementIndexes = new HashMap<Integer,String>(); // Used for remembering order after merging tagged elements
                        List<TPDisplayElement> unTaggedElements = new ArrayList<TPDisplayElement>();

                        int slotsUsedByMergedElements = 0;

                        for (int i = 0;i < elements.size();i++) {
                            TPDisplayElement element = elements.get(i);
                            if (!element.texturepack.tag.equals("NULL")) {
                                if (tagToElements.containsKey(element.texturepack.name)) {
                                    tagToElements.get(element.texturepack.name).add(element);
                                    slotsUsedByMergedElements++;
                                } else {
                                    tagToElements.put(element.texturepack.name,new ArrayList<TPDisplayElement>());
                                    tagToElements.get(element.texturepack.name).add(element);
                                    reservedElementIndexes.put(i - slotsUsedByMergedElements,element.texturepack.name);
                                }
                            } else {
                                unTaggedElements.add(element);
                            }
                        }

                        int indexOffset = 0;

                        for (int i = 0;i < unTaggedElements.size() + indexOffset;i++) {
                            if (reservedElementIndexes.containsKey(i)) {
                                TPDisplayElement[] elements = ApecUtils.listToArray(tagToElements.get(reservedElementIndexes.get(i)),TPDisplayElement[].class);
                                TPRVTabButton[] tabs = new TPRVTabButton[elements.length];
                                for (int j = 0;j < tabs.length;j++) {
                                    tabs[j] = new TPRVTabButton(elements[j].texturepack.tag,j);
                                    buttonList.add(tabs[j]);
                                }
                                drawableElements.add(new TPTaggedDisplayElement(elements,tabs));
                                indexOffset++;
                            } else {
                                drawableElements.add(unTaggedElements.get(i - indexOffset));
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
            for (ITPDrawableElement element : drawableElements) {
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
                    } else if (button instanceof TPRVNavigationButton) {
                        if (button.mousePressed(mc, mouseX, mouseY)) {
                            this.executeAction(((TPRVNavigationButton) button).action);
                            break;
                        }
                    } else if (button instanceof TPRVTabButton) {
                        if (button.mousePressed(mc,mouseX,mouseY)) {
                            ((TPRVTabButton)button).onClick();
                            break;
                        }
                    } else if (button instanceof TPRVConnectionButton) {
                        if (button.mousePressed(mc,mouseX,mouseY)) {
                            ((TPRVConnectionButton)button).onClick();
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