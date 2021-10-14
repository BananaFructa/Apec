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

public class IBCritDamage extends GUIComponent {

    public IBCritDamage() {
        super(GUIComponentID.IB_CRIT_DMG);
    }

    private int CritDamageStringWidth = 0;

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;
        Vector2f pos = getCurrentAnchorPoint();
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CRIT_DAMAGE) || editingMode) {
            gi.drawTexturedModalRect((int) (pos.x * oneOverScale),  pos.y * oneOverScale - 1, 70, 216, 9, 9);
        }
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        Vector2f pos = getCurrentAnchorPoint();
        String critDamageText = (UseIcons ? ts.CritDamage : "\u2620" + ts.CritDamage);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CRIT_DAMAGE) || editingMode) {
            ApecUtils.drawStylizedString(
                    critDamageText,
                    (int) ((pos.x + (UseIcons ? 11 : 0)) * oneOverScale),
                    (int) (pos.y * oneOverScale),
                    0x5555FF
            );
        }
        CritDamageStringWidth = mc.fontRendererObj.getStringWidth(critDamageText);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(480*scale + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(CritDamageStringWidth + (UseIcons ? 11 : 0), 10*scale);
    }
}
