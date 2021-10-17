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

public class IBSoulFlow extends GUIComponent {

    private int SoulflowStringWidth = 0;

    public IBSoulFlow() {
        super(GUIComponentID.IB_SOULFLOW);
    }

    @Override
    public void init() {
        super.init();
        this.enabled = !ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_SOULFLOW_DISPLAY) && ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SOULFLOW);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;
        Vector2f pos = getCurrentAnchorPoint();
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        gi.drawTexturedModalRect((int) (pos.x * oneOverScale), pos.y * oneOverScale - 1, 110, 216, 9, 9);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        Vector2f pos = getCurrentAnchorPoint();
        String soulflowText = (UseIcons ? ps.Soulflow + "" : ps.Soulflow + "\u2e0e");
        ApecUtils.drawStylizedString(
                soulflowText,
                (int) ((pos.x + (UseIcons ? 11 : 0)) * oneOverScale),
                (int) (pos.y * oneOverScale),
                0x00AAAA
        );

        SoulflowStringWidth = mc.fontRendererObj.getStringWidth(soulflowText);
    }

    @SubscribeEvent
    public void onSettingChanged(ApecSettingChangedState event) {
        this.enabled = !ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_SOULFLOW_DISPLAY) && ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SOULFLOW);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(420*scale + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(SoulflowStringWidth + (UseIcons ? 11 : 0), 10*scale);
    }
}
