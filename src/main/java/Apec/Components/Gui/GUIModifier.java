package Apec.Components.Gui;

import Apec.*;
import Apec.Components.Gui.GuiElements.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.GuiIngameForge;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.ArrayList;


public class GUIModifier extends Component {

    public enum GUiComponentID {
        STAT_BARS,
        SKILL_BARS,
        BOTTOM_BAR,
        INV_SUBTRACT,
        EXTRA_SCR_INF
    }

    private  Minecraft mc = Minecraft.getMinecraft();

    ArrayList<GUIComponent> GUIComponents = new ArrayList() {{
        add(new StatusBars()); // The bars that display the hp, mana, and and air
        add(new SkillBar()); // The bar that shows the skill progression
        add(new InventoryTraffic()); // The thing that shows the inventory traffic
        add(new InfoBox()); // The block box with the things that used to be in the scoreboard
        add(new ExtraInfo()); // The other things that used to be in the scoreboard
    }};

    public GuiIngame normalGuiIngame;
    public ApecGuiIngame apecGuiIngame;

    public GUIModifier() {
        super(ComponentId.GUI_MODIFIER);
    }

    public void onRender(ScaledResolution sr) {
        DataExtractor.PlayerStats ps;
        DataExtractor.ScoreBoardData sd;
        ps = ApecMain.Instance.dataExtractor.ProcessPlayerStats();
        sd = ApecMain.Instance.dataExtractor.ProcessScoreBoardData();
        try {
            for (GUIComponent component : GUIComponents) {
                component.drawTex(ps, sd, sr);
            }
            for (GUIComponent component : GUIComponents) {
                component.draw(ps, sd, sr);
            }
        } catch (Exception e) { }
    }

    @Override
    protected void onEnable() {
        IChatComponent header = null,footer = null;
        try {
            header = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("header"), true);
            footer = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("footer"), true);
        } catch (Exception e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error switching gui interfaces!");
        }

        mc.ingameGUI = new ApecGuiIngame(mc);

        try {
            mc.ingameGUI.getTabList().setHeader(header);
            mc.ingameGUI.getTabList().setFooter(footer);
        } catch (Exception e) { }
    }

    @Override
    protected void onDisable() {
        IChatComponent header = null,footer = null;
        try {
            header = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("header"), true);
            footer = (IChatComponent) FieldUtils.readField(this.mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("footer"), true);
        } catch (Exception e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error switching gui interfaces!");
        }

        mc.ingameGUI = new GuiIngameForge(mc);

        try {
            mc.ingameGUI.getTabList().setHeader(header);
            mc.ingameGUI.getTabList().setFooter(footer);
        } catch (Exception e) { }
    }

}
