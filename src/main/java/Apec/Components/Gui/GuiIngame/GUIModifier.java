package Apec.Components.Gui.GuiIngame;

import Apec.*;
import Apec.Components.Gui.GuiIngame.GuiElements.*;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.util.vector.Vector2f;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;


public class GUIModifier extends Component {

    private Minecraft mc = Minecraft.getMinecraft();

    public static GUIModifier Instance;

    boolean alreadyAutoEnabled = false;
    boolean alreadyAutoDisabled = true;
    public boolean shouldTheGuiAppear = false;

    ApecGuiIngameForge AGIInstance = null;

    final List<RenderGameOverlayEvent.ElementType> EventsToCancel = new ArrayList<RenderGameOverlayEvent.ElementType>() {{
        add(FOOD);
        add(HEALTH);
        add(ARMOR);
        add(AIR);
        add(EXPERIENCE);
    }};

    /**
     * All components that have to be redered
     */
    public List<GUIComponent> GUIComponents = new ArrayList<GUIComponent>() {{
        add(new InfoBox()); // The block box with the things that used to be in the scoreboard
        add(new HpBar());
        add(new HpText());
        add(new MpBar());
        add(new MpText());
        add(new XpBar());
        add(new XpText());
        add(new AirBar());
        add(new AirText());
        add(new SkillBar()); // The bar that shows the skill progression
        add(new SkillText());
        add(new DefenceText());
        add(new InventoryTraffic()); // The thing that shows the inventory traffic
        add(new ExtraInfo()); // The other things that used to be in the scoreboard
        //add(new BossHealthBar());
        add(new HotBar());
        add(new HorseJumpBar());
        add(new ToolTipText());
        add(new EventLister());
        add(new AbilityText());
        add(new BossBar());
        //add(new DebugText());// Dont ship this
        add(new RiftTimer());
        add(new GameModeText());
        add(new KuudraSetBonusText());
    }};

    public GUIModifier() {
        super(ComponentId.GUI_MODIFIER);
        Instance = this;
    }

    @Override
    public void init() {
        for (GUIComponent component : GUIComponents) {
            component.init();
        }
    }

    public void CustomizationMenuOpened() {
        for (GUIComponent component : GUIComponents) {
            component.editInit();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        // The script for the auto enabling
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
        // This is for the compatibility with sidebar mod
        if (shouldTheGuiAppear && !(mc.ingameGUI instanceof ApecGuiIngameForge)) {
            if (AGIInstance != null) mc.ingameGUI = AGIInstance;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderHelmet(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.HELMET && getEnableState()) {
            GuiIngameForge.renderObjective = false;
            onRender(event.resolution);
        }

    }

    /**
     * @param guiComponentID = Gui Component Id
     * @return Returns the component with the matching component id, null if none is found
     */
    public <T extends GUIComponent> T getGuiComponent(GUIComponentID guiComponentID) {
        for (GUIComponent component : GUIComponents) {
            if (component.gUiComponentID == guiComponentID) return (T) component;
        }
        return null;
    }

    /**
     * @param component = Inout gui component
     * @return Returns true if a component should not be rendered wile F3 menu is opened, false if otherwise
     */
    private boolean shouldBlockF3(GUIComponent component) {
        return ApecMain.Instance.settingsManager.getSettingState(SettingID.HIDE_IN_F3) &&
                mc.gameSettings.showDebugInfo && component.getCurrentAnchorPoint().y < 150;
    }

    /**
     * @brief Main function for rendering
     */
    public void onRender(ScaledResolution sr) {
        DataExtractor.PlayerStats ps;
        DataExtractor.ScoreBoardData sd;
        DataExtractor.OtherData od;
        ps = ApecMain.Instance.dataExtractor.getPlayerStats();
        sd = ApecMain.Instance.dataExtractor.getScoreBoardData();
        od = ApecMain.Instance.dataExtractor.getOtherData();
        try {
            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, 1);
            // Draws the screen components
            for (GUIComponent component : GUIComponents) {
                if (shouldBlockF3(component)) continue;
                GlStateManager.pushMatrix();
                component.drawTex(ps, sd, od, sr, mc.currentScreen instanceof CustomizationGui);
                GlStateManager.popMatrix();
            }
            for (GUIComponent component : GUIComponents) {
                if (shouldBlockF3(component)) continue;
                GlStateManager.pushMatrix();
                component.draw(ps, sd, od, sr, mc.currentScreen instanceof CustomizationGui);
                GlStateManager.popMatrix();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param from = Old gui
     * @param to   = New gui
     * @brief Switches important data from a guiingame object to another
     */
    private void SwitchDataBetweenGuis(GuiIngame from, GuiIngame to) {
        try {
            IChatComponent header, footer;
            GuiNewChat guiNewChat;
            GuiStreamIndicator guiStreamIndicator;
            Integer updateCounter;
            RenderGameOverlayEvent event;

            guiNewChat = ApecUtils.readDeclaredField(GuiIngame.class, from, "persistantChatGUI");
            guiStreamIndicator = ApecUtils.readDeclaredField(GuiIngame.class, from, "streamIndicator");
            updateCounter = ApecUtils.readDeclaredField(GuiIngame.class, from, "updateCounter");
            event = ApecUtils.readDeclaredField(GuiIngameForge.class, from, "eventParent");

            ApecUtils.writeDeclaredField(GuiIngame.class, to, "persistantChatGUI", guiNewChat);
            ApecUtils.writeDeclaredField(GuiIngame.class, to, "streamIndicator", guiStreamIndicator);
            ApecUtils.writeDeclaredField(GuiIngame.class, to, "updateCounter", updateCounter);
            ApecUtils.writeDeclaredField(GuiIngameForge.class, to, "eventParent", event);

            GuiPlayerTabOverlay tab = (GuiPlayerTabOverlay) ApecUtils.readDeclaredField(GuiIngame.class, from, "overlayPlayerList");
            ApecUtils.writeDeclaredField(GuiPlayerTabOverlay.class, tab, "guiIngame", to);
            ApecUtils.writeDeclaredField(GuiIngame.class, to, "overlayPlayerList", tab);

        } catch (Exception err) {
            err.printStackTrace();
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error switching gui interfaces!");
        }
    }

    @Override
    protected void onEnable() {

        AGIInstance = new ApecGuiIngameForge(mc);
        SwitchDataBetweenGuis(mc.ingameGUI, AGIInstance);
        mc.ingameGUI = AGIInstance;

        this.ApplyDeltas();

        if (!ApecMain.version.equals(ApecMain.Instance.newestVersion)) {
            ChatComponentText msg = new ChatComponentText("[\u00A72Apec\u00A7f] There is a new version of Apec available (" + ApecMain.Instance.newestVersion + ")! Click on this message to go to the CurseForge page.");
            msg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/apec"));
            msg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to go to the CurseForge page")));
            Minecraft.getMinecraft().thePlayer.addChatMessage(msg);
        }

        shouldTheGuiAppear = true;
    }

    @Override
    protected void onDisable() {

        GuiIngame normalInterface = new GuiIngameForge(mc);
        SwitchDataBetweenGuis(mc.ingameGUI, normalInterface);
        mc.ingameGUI = normalInterface;

        GuiIngameForge.renderObjective = true;

        shouldTheGuiAppear = false;
    }

    public Vector2f applyGlobalChanges(GUIComponent component, Vector2f anchorPoint) {
        boolean isbbUp = ApecMain.Instance.settingsManager.getSettingState(SettingID.BB_ON_TOP);
        if (isbbUp && component.getDeltaPosition().length() == 0)
            anchorPoint = ApecUtils.addVec(anchorPoint, new Vector2f(0, 20));
        return anchorPoint;
    }

    /**
     * @brief Sets the delta positions of each gui element
     */
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
                Vector2f delta = new Vector2f(Float.parseFloat(tempSplit[1].split("@")[0]), Float.parseFloat(tempSplit[1].split("@")[1]));
                float scale = 1f;
                if (tempSplit[1].split("@").length == 3) {
                    scale = Float.parseFloat(tempSplit[1].split("@")[2]);
                }
                if (subComponent == -1) {
                    getGuiComponent(GUIComponentID.values()[idx]).setDeltaPosition(delta);
                    getGuiComponent(GUIComponentID.values()[idx]).setScale(scale);
                } else {
                    getGuiComponent(GUIComponentID.values()[idx]).setSubElementDeltaPosition(delta, subComponent);
                }
            }
            scanner.close();
        } catch (Exception e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading GUI deltas!");
            // Delete the file if it is corrupted
            try {
                new File("config/Apec/GuiDeltas.txt").delete();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

}
