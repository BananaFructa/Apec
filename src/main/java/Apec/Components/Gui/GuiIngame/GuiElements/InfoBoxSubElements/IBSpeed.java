package Apec.Components.Gui.GuiIngame.GuiElements.InfoBoxSubElements;

import Apec.ApecMain;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

public class IBSpeed extends GUIComponent {

    int SpeedStringWidth = 0;

    public IBSpeed() {
        super(GUIComponentID.IB_SPEED);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;
        Vector2f pos = getCurrentAnchorPoint();
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SPEED) || editingMode) {
            gi.drawTexturedModalRect((int) (pos.x * oneOverScale),  pos.y * oneOverScale - 1, 40, 216, 9, 9);
        }
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        Vector2f pos = getCurrentAnchorPoint();
        String speedText = (UseIcons ? ts.Speed : "\u2726" + ts.Speed);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SPEED) || editingMode) {
            ApecUtils.drawStylizedString(
                    speedText,
                    (int) ((pos.x + (UseIcons ? 11 : 0)) * oneOverScale),
                    (int) (pos.y * oneOverScale),
                    0xffffff
            );
        }
        SpeedStringWidth = mc.fontRendererObj.getStringWidth(speedText);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(400*scale + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(SpeedStringWidth + (UseIcons ? 11 : 0), 10*scale);
    }

}
