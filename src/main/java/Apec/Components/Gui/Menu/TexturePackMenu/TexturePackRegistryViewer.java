package Apec.Components.Gui.Menu.TexturePackMenu;

import Apec.ApecUtils;
import Apec.Component;
import Apec.ComponentId;
import Apec.VersionChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
                DownloadProcess downloadProcess = new DownloadProcess(new URL(data.downloadUrl),data.name,data.expectedFileName);
                downloadProcess.startDownload();
                activeDownloads.add(downloadProcess);
                nameToDownloadProcess.put(data.name,downloadProcess);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public static void removeDownloadProcess(DownloadProcess process) {
        synchronized (threadLock) {
            activeDownloads.remove(process);
            nameToDownloadProcess.remove(process.tpname,process);
        }
    }

    public static class TPRVGuiScreen extends GuiScreen {

        private List<TPDisplayElement> elements = new ArrayList<TPDisplayElement>();

        public final int elementHeight = 50;

        public List<String> dataBaseUrls;
        private String registryTag;

        public TPRVGuiScreen() {

        }

        @Override
        public void initGui() {
            super.initGui();
            if (elements.isEmpty()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        registryTag = VersionChecker.getRegistryTag();
                        dataBaseUrls = loadDataBases(registryTag);
                        if (dataBaseUrls.size() > 0) {
                            loadDataset(0);
                        }
                    }
                }).start();
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            synchronized (threadLock) {
                drawButtons(mouseX,mouseY,TPRVDropDownButton.class);
                ScaledResolution sr = new ScaledResolution(mc);
                drawRect(0, 0, sr.getScaledWidth(), 15, 0xff000000);
                mc.fontRendererObj.drawString("Apec Texture Pack Registry", 2, 3, 0xffffffff);
                int yOffset = 0;
                for (int i = 0;i < elements.size();i++) {
                    yOffset += elements.get(i).draw(20 + i * (elementHeight + 5) + yOffset,mouseX,mouseY,sr,mc);
                }
                drawButtons(mouseX,mouseY,TPRVDownloadButton.class);
            }
        }

        private void loadDataset(final int index) {
            //TODO: clear all
            synchronized (threadLock) {
                this.buttonList.clear();
            }

            final TexturePackRegistryViewer.TPRVGuiScreen instace = this;
            // Request thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<TPData> tps = getAllTexturePacksFromDataBase(dataBaseUrls.get(index), registryTag);

                        for (TPData tp : tps) {
                            synchronized (threadLock) {
                                TPRVDownloadButton button = new TPRVDownloadButton(0, 0, 0, tp);
                                buttonList.add(button);
                                TPDisplayElement element = new TPDisplayElement(tp, button, instace);
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

                    for (TPDisplayElement element : elements) {
                        synchronized (threadLock) {
                            try {
                                element.loadIcon(registryTag, mc);
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
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
                        if (button.mousePressed(mc,mouseX,mouseY)) {
                            ((TPRVDownloadButton)button).startDownload();
                            break;
                        }
                    } else if (button instanceof TPRVDropDownButton) {
                        if (button.mousePressed(mc,mouseX,mouseY)) {
                            ((TPRVDropDownButton)button).toggleDescription();
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
    }

}