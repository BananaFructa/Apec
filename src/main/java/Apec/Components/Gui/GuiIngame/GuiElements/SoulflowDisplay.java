package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Events.ApecSettingChangedState;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.TextComponent;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Vector2f;

public class SoulflowDisplay extends TextComponent {

    public SoulflowDisplay () {
        super(GUIComponentID.SOULFLOW_TEXT);
    }

    @Override
    public void init() {
        super.init();
        this.enabled = ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SOULFLOW) && ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_SOULFLOW_DISPLAY);
    }

    private int soulflowStringWidth = 0;
    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr,boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);

        Vector2f StatBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), oneOverScale);

        String soulflowString = ps.Soulflow + "\u2e0e";

        soulflowStringWidth = mc.fontRendererObj.getStringWidth(soulflowString);
        if (ps.Soulflow > -1 || editingMode) {
            ApecUtils.drawStylizedString(soulflowString, (int) (StatBar.x - soulflowStringWidth), (int) (StatBar.y - 10), 0x00AAAA);
        }
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onSettingChanged(ApecSettingChangedState event) {
        this.enabled = ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SOULFLOW) && ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_SOULFLOW_DISPLAY);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return guiModifier.applyGlobalChanges(this,new Vector2f(g_sr.getScaledWidth() - 8, 92));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-soulflowStringWidth*scale,-11*scale);
    }

}
