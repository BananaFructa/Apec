package Apec.Components.Gui.GuiIngame.GuiElements.InfoBoxSubElements;

import Apec.ApecMain;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Events.ApecSettingChangedState;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Vector2f;

public class IBGemstonePowder extends GUIComponent {

    private int GemstonePowderStringWidth = 0;

    public IBGemstonePowder() {
        super(GUIComponentID.IB_GEMSTONE_POWEDER);
    }

    @Override
    public void init() {
        super.init();
        this.enabled = !ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_POWDER_DISPLAY) && ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_GEMSTONE_POWDER);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;
        Vector2f pos = getCurrentAnchorPoint();
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_GEMSTONE_POWDER)) {
            gi.drawTexturedModalRect((int) (pos.x * oneOverScale), pos.y * oneOverScale - 1, 100, 216, 9, 9);
        }

    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        String gemstonePowderText = (UseIcons ? ts.GemstonePowder : "\u1805" + ts.GemstonePowder);
        Vector2f pos = getCurrentAnchorPoint();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_GEMSTONE_POWDER)) {
            ApecUtils.drawStylizedString(
                    gemstonePowderText,
                    (int) ((pos.x + (UseIcons ? 11 : 0)) * oneOverScale),
                    (int) (pos.y * oneOverScale),
                    0xFF55FF
            );
        }

        GemstonePowderStringWidth = mc.fontRendererObj.getStringWidth(gemstonePowderText);
    }

    @SubscribeEvent
    public void onSettingChanged(ApecSettingChangedState event) {
        this.enabled = !ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_POWDER_DISPLAY) && ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_GEMSTONE_POWDER);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(420*scale + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(GemstonePowderStringWidth + (UseIcons ? 11 : 0), 10*scale);
    }
}
