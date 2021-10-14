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

public class IBZone extends GUIComponent {

    private int zoneAddX = 0;
    private int ZoneStringWidth = 0;

    public IBZone() {
        super(GUIComponentID.IB_ZONE);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);

        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;

        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        Vector2f pos = getCurrentAnchorPoint();

        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));

        if (ApecMain.Instance.dataExtractor.isInTheCatacombs) {
            gi.drawTexturedModalRect((int) (pos.x * oneOverScale - 1), pos.y*oneOverScale -1, 24, 216, 7, 8);
        } else {
            gi.drawTexturedModalRect((int) (pos.x * oneOverScale), pos.y *oneOverScale -1, 14, 216, 9, 9);
        }
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);

        boolean inTheCatacombs = ApecMain.Instance.dataExtractor.isInTheCatacombs;

        zoneAddX = (inTheCatacombs ? 5 : 9) ;
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        String zoneText = (UseIcons ? ApecUtils.RemoveCharSequence("\u23E3", sd.Zone) : sd.Zone);
        Vector2f pos = getCurrentAnchorPoint();

        ApecUtils.drawStylizedString(
                zoneText,
                (int) ((pos.x + (UseIcons ? zoneAddX : 0)) * oneOverScale),
                (int) (pos.y * oneOverScale),
                0xffffff
        );

        ZoneStringWidth = mc.fontRendererObj.getStringWidth(zoneText);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(220*scale + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(ZoneStringWidth + (UseIcons ? zoneAddX : 0)*scale, 10*scale);
    }
}
