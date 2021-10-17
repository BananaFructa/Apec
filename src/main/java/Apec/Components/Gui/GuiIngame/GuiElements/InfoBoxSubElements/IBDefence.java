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

public class IBDefence extends GUIComponent {

    private int DefenceStringWidth = 0;

    public IBDefence() {
        super(GUIComponentID.IB_DEF);
    }

    @Override
    public void init() {
        super.init();
        this.enabled = !ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;
        Vector2f pos = getCurrentAnchorPoint();
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        gi.drawTexturedModalRect((int) (pos.x * oneOverScale), pos.y * oneOverScale - 1, 32, 215, 7, 10);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        String defenceText = (UseIcons ? "\u00a7a" + ps.Defence : "\u00a7a" + ps.Defence + " Defence");
        Vector2f pos = getCurrentAnchorPoint();
        DefenceStringWidth = mc.fontRendererObj.getStringWidth(defenceText);
        ApecUtils.drawStylizedString(
                defenceText,
                (int) (pos.x + (UseIcons ? 10 : 0) * oneOverScale),
                (int) (pos.y * oneOverScale),
                0xffffff
        );
    }

    @SubscribeEvent
    public void onSettingChanged(ApecSettingChangedState event) {
        if (event.settingID == SettingID.USE_DEFENCE_OUT_OF_BB) {
            this.enabled = !event.state;
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(360*scale + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(DefenceStringWidth + (UseIcons ? 10 : 0)*scale, 10*scale);
    }

}
