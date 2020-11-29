package Apec.Components.Gui.GuiIngame;

import Apec.*;
import Apec.Components.Gui.GuiIngame.GuiElements.*;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class GUIModifier extends Component {

    private  Minecraft mc = Minecraft.getMinecraft();


    public ArrayList<GUIComponent> GUIComponents = new ArrayList<GUIComponent>() {{
        add(new InfoBox()); // The block box with the things that used to be in the scoreboard
        add(new HpBar());
        add(new HpText());
        add(new MnBar());
        add(new MpText());
        add(new XpBar());
        add(new XpText());
        add(new AirBar());
        add(new AirText());
        add(new SkillBar()); // The bar that shows the skill progression
        add(new SkillText());
        add(new InventoryTraffic()); // The thing that shows the inventory traffic
        add(new ExtraInfo()); // The other things that used to be in the scoreboard
        //add(new BossHealthBar());
        add(new HotBar());
        add(new ToolTipText());
        add(new EventLister());
    }};

    public GUIModifier() {
        super(ComponentId.GUI_MODIFIER);
    }

    @Override
    public void init() {
        for (GUIComponent component : GUIComponents) {
            component.init();
        }
    }

    boolean alreadyAutoEnabled = false;
    boolean alreadyAutoDisabled = false;
    public boolean shouldTheGuiAppear = false;
    static boolean cancelNextHotbar = false;

    ApecGuiIngame Instance = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!ApecMain.Instance.dataExtractor.isInSkyblock) alreadyAutoEnabled = false;
        if (ApecMain.Instance.dataExtractor.isInSkyblock) alreadyAutoDisabled = false;
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.AUTO_ENABLE)) {
            if (ApecMain.Instance.dataExtractor.isInSkyblock && !this.getEnableState() && !alreadyAutoEnabled) {
                this.Toggle();
                alreadyAutoEnabled = true;
                ApecUtils.showMessage("[\u00A72Apec\u00A7f] Auto enabled the GUI Interface!");
            }
            if (!ApecMain.Instance.dataExtractor.isInSkyblock && this.getEnableState() && !alreadyAutoDisabled) {
                this.Toggle();
                alreadyAutoDisabled = true;
                ApecUtils.showMessage("[\u00A72Apec\u00A7f] Auto disabled the GUI Interface!");
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onTickLowest(TickEvent.ClientTickEvent event) {
        if (shouldTheGuiAppear && !(mc.ingameGUI instanceof ApecGuiIngame)) {
            if (Instance != null) mc.ingameGUI = Instance;
        }
    }

    public GUIComponent getGuiComponent(GUIComponentID guiComponentID) {
        for (GUIComponent component : GUIComponents) {
            if (component.gUiComponentID == guiComponentID) return component;
        }
        return null;
    }

    private boolean shouldBlockF3(GUIComponent component) {
        return ApecMain.Instance.settingsManager.getSettingState(SettingID.HIDE_IN_F3) && mc.gameSettings.showDebugInfo && component.getRealAnchorPoint().y < 150;
    }

    public void onRender(ScaledResolution sr) {
        DataExtractor.PlayerStats ps;
        DataExtractor.ScoreBoardData sd;
        DataExtractor.OtherData od;
        ps = ApecMain.Instance.dataExtractor.getPlayerStats();
        sd = ApecMain.Instance.dataExtractor.getScoreBoardData();
        od = ApecMain.Instance.dataExtractor.getOtherData();
        try {
            GlStateManager.enableBlend();
            GlStateManager.color(1,1,1,1);
            for (GUIComponent component : GUIComponents) {
                if (shouldBlockF3(component)) continue;
                GlStateManager.pushMatrix();
                component.drawTex(ps, sd, od, sr,mc.currentScreen instanceof CustomizationGui);
                GlStateManager.popMatrix();
            }
            for (GUIComponent component : GUIComponents) {
                if (shouldBlockF3(component)) continue;
                GlStateManager.pushMatrix();
                component.draw(ps, sd, od, sr,mc.currentScreen instanceof CustomizationGui);
                GlStateManager.popMatrix();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onEnable() {
        IChatComponent header = null,footer = null;
        GuiNewChat guiNewChat = null;
        List<ChatLine> chatLines = null;
        List<String> sentMessages = null;
        try {
            guiNewChat = (GuiNewChat) FieldUtils.readField((GuiIngame)this.mc.ingameGUI, ApecUtils.unObfedFieldNames.get("persistantChatGUI"), true);
            header = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("header"), true);
            footer = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("footer"), true);
        } catch (Exception e) {
            e.printStackTrace();
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error switching gui interfaces!");
        }

        boolean modeV = ApecMain.Instance.settingsManager.getSettingState(SettingID.OVERWRITE_GUI);

        if (modeV) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] Opening GUI! MODE = VANILLA");
            Instance = new ApecGuiIngameVanilla(mc);
            mc.ingameGUI = Instance;
        } else {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] Opening GUI! MODE = FORGE");
            Instance = new ApecGuiIngameForge(mc);
            mc.ingameGUI = Instance;
        }

        try {
            FieldUtils.writeField(this.mc.ingameGUI, ApecUtils.unObfedFieldNames.get("persistantChatGUI"), guiNewChat, true);
            mc.ingameGUI.getTabList().setHeader(header);
            mc.ingameGUI.getTabList().setFooter(footer);
            this.ApplyDeltas();
            if (!ApecMain.version.equals(ApecMain.Instance.newestVersion)) {
                ChatComponentText msg = new ChatComponentText("[\u00A72Apec\u00A7f] There is a new version of Apec available! Click on this message to go to the CurseForge page.");
                msg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.curseforge.com/minecraft/mc-mods/apec"));
                Minecraft.getMinecraft().thePlayer.addChatMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        shouldTheGuiAppear = true;
    }

    @Override
    protected void onDisable() {
        IChatComponent header = null,footer = null;
        GuiNewChat guiNewChat = null;
        try {
            guiNewChat = (GuiNewChat) FieldUtils.readField(this.mc.ingameGUI, ApecUtils.unObfedFieldNames.get("persistantChatGUI"), true);
            header = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("header"), true);
            footer = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("footer"), true);
        } catch (Exception e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error switching gui interfaces!");
        }

        mc.ingameGUI = new GuiIngameForge(mc);

        try {
            FieldUtils.writeField(this.mc.ingameGUI, ApecUtils.unObfedFieldNames.get("persistantChatGUI"), guiNewChat, true);
            mc.ingameGUI.getTabList().setHeader(header);
            mc.ingameGUI.getTabList().setFooter(footer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        shouldTheGuiAppear = false;
    }

    private void ApplyDeltas() {
        try {
            Scanner scanner = new Scanner(new File("config/Apec/GuiDeltas.txt"));
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                String[] tempSplit = s.split("#");
                int subComponent = -1;
                int idx = 0;
                if (tempSplit[0].contains("!")) {
                    String[] tempSplit_ = tempSplit[0].split("!");
                    idx = Integer.parseInt(tempSplit_[0]);
                    subComponent = Integer.parseInt(tempSplit_[1]);
                } else {
                    idx = Integer.parseInt(tempSplit[0]);
                }
                Vector2f delta = new Vector2f(Float.parseFloat(tempSplit[1].split("@")[0]),Float.parseFloat(tempSplit[1].split("@")[1]));
                float scale = 1f;
                if (tempSplit[1].split("@").length == 3) {
                    scale = Float.parseFloat(tempSplit[1].split("@")[2]);
                }
                if (subComponent == -1) {
                    getGuiComponent(GUIComponentID.values()[idx]).setDelta_position(delta);
                    getGuiComponent(GUIComponentID.values()[idx]).setScale(scale);
                } else {
                    getGuiComponent(GUIComponentID.values()[idx]).setSubElementDelta_position(delta,subComponent);
                }
            }
            scanner.close();
        } catch (IOException e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading GUI deltas!");
        }
    }

}
