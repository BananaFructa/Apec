package Apec.Components.Gui.GuiIngame;

import Apec.*;
import Apec.Components.Gui.GuiIngame.GuiElements.*;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.Sys;

import java.lang.reflect.Field;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;


public class GUIModifier extends Component {

    public enum GUiComponentID {
        STAT_BARS,
        SKILL_BARS,
        BOTTOM_BAR,
        INV_SUBTRACT,
        EXTRA_SCR_INF,
        BOSS_HEALTH_BAR,
        EVENT_DISPLAY
    }

    private  Minecraft mc = Minecraft.getMinecraft();

    private List<GUiComponentID> compenetsToBlockInF3 = new ArrayList<GUiComponentID>() {{
        add(GUiComponentID.STAT_BARS);
        add(GUiComponentID.INV_SUBTRACT);
        add(GUiComponentID.EXTRA_SCR_INF);
        add(GUiComponentID.EVENT_DISPLAY);
    }};

    ArrayList<GUIComponent> GUIComponents = new ArrayList<GUIComponent>() {{
        add(new StatusBars()); // The bars that display the hp, mana, and and air
        add(new SkillBar()); // The bar that shows the skill progression
        add(new InventoryTraffic()); // The thing that shows the inventory traffic
        add(new InfoBox()); // The block box with the things that used to be in the scoreboard
        add(new ExtraInfo()); // The other things that used to be in the scoreboard
        //add(new BossHealthBar());
        add(new EventLister());
    }};

    public GUIModifier() {
        super(ComponentId.GUI_MODIFIER);
    }

    boolean alreadyAutoEnabled = false;
    boolean alreadyAutoDisabled = false;

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

    private boolean shouldBlockF3(GUiComponentID gUiComponentID) {
        return ApecMain.Instance.settingsManager.getSettingState(SettingID.HIDE_IN_F3) && mc.gameSettings.showDebugInfo && this.compenetsToBlockInF3.contains(gUiComponentID);
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
            for (GUIComponent component : GUIComponents) {
                if (shouldBlockF3(component.gUiComponentID)) continue;
                component.drawTex(ps, sd, od, sr);
            }
            for (GUIComponent component : GUIComponents) {
                if (shouldBlockF3(component.gUiComponentID)) continue;
                component.draw(ps, sd, od, sr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onEnable() {
        if (!ApecMain.version.equals(ApecMain.Instance.newestVersion)) {
            ChatComponentText msg = new ChatComponentText("[\u00A72Apec\u00A7f] There is a new version of Apec available! Click on this message to go to the CurseForge page.");
            msg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.curseforge.com/minecraft/mc-mods/apec"));
            Minecraft.getMinecraft().thePlayer.addChatMessage(msg);
        }
        IChatComponent header = null,footer = null;
        GuiNewChat guiNewChat = null;
        List<ChatLine> chatLines = null;
        try {
            guiNewChat = (GuiNewChat) FieldUtils.readField((GuiIngame)this.mc.ingameGUI, ApecUtils.unObfedFieldNames.get("persistantChatGUI"), true);
            chatLines = (List<ChatLine>)FieldUtils.readDeclaredField(guiNewChat,ApecUtils.unObfedFieldNames.get("chatMessages"),true);
            header = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("header"), true);
            footer = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("footer"), true);
        } catch (Exception e) {
            e.printStackTrace();
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error switching gui interfaces!");
        }

        boolean modeV = ApecMain.Instance.settingsManager.getSettingState(SettingID.OVERWRITE_GUI);

        if (modeV) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] Opening GUI! MODE = VANILLA");
            mc.ingameGUI = new ApecGuiIngameVanilla(mc);
        } else {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] Opening GUI! MODE = FORGE");
            mc.ingameGUI = new ApecGuiIngameForge(mc);
        }

        try {
            mc.ingameGUI.getTabList().setHeader(header);
            mc.ingameGUI.getTabList().setFooter(footer);
            if (guiNewChat != null && chatLines != null) {
               if (modeV) ((ApecGuiIngameVanilla) mc.ingameGUI).setChatData(chatLines);
               else ((ApecGuiIngameForge) mc.ingameGUI).setChatData(chatLines);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDisable() {
        IChatComponent header = null,footer = null;
        GuiNewChat guiNewChat = null;
        List<ChatLine> chatLines = null;
        try {
            guiNewChat = (GuiNewChat) FieldUtils.readField((GuiIngame)this.mc.ingameGUI, ApecUtils.unObfedFieldNames.get("persistantChatGUI"), true);
            chatLines = (List<ChatLine>)FieldUtils.readDeclaredField(guiNewChat,ApecUtils.unObfedFieldNames.get("chatMessages"),true);
            header = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("header"), true);
            footer = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("footer"), true);
        } catch (Exception e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error switching gui interfaces!");
        }

        mc.ingameGUI = new GuiIngameForge(mc);

        try {
            //FieldUtils.writeDeclaredField(FieldUtils.readField((GuiIngame)this.mc.ingameGUI, ApecUtils.unObfedFieldNames.get("persistantChatGUI"), true), "field_146253_i", chatLines, true);
            GuiNewChat _guiNewChat = (GuiNewChat) FieldUtils.readField((GuiIngame)this.mc.ingameGUI, ApecUtils.unObfedFieldNames.get("persistantChatGUI"), true);
            for (int i = chatLines.size() - 1;i > -1;i--) {
                _guiNewChat.printChatMessage(chatLines.get(i).getChatComponent());
            }
            mc.ingameGUI.getTabList().setHeader(header);
            mc.ingameGUI.getTabList().setFooter(footer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
