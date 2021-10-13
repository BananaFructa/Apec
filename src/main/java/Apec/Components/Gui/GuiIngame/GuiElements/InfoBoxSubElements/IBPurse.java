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

public class IBPurse extends GUIComponent {

    int PurseStringWidth = 0;

    public IBPurse() {
        super(GUIComponentID.IB_PURSE);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);

        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        Vector2f pos = getCurrentAnchorPoint();
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        gi.drawTexturedModalRect((int) (pos.x * oneOverScale), pos.y * oneOverScale - 1, 1, 216, 6, 9);

    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);

        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);

        String purseText = (UseIcons ? RemovePurseText(sd.Purse) : sd.Purse);
        Vector2f pos = getCurrentAnchorPoint();
        ApecUtils.drawStylizedString(
                purseText,
                (int) (pos.x + ((UseIcons ? 9 : 0) * oneOverScale)),
                (int) (pos.y * oneOverScale),
                0xffffff
        );

        PurseStringWidth = mc.fontRendererObj.getStringWidth(purseText);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(0 + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(PurseStringWidth  + (UseIcons ? 9 : 0)*scale, 10*scale);
    }

    private String RemovePurseText(String s) {
        if (ApecUtils.containedByCharSequence(s,"Purse: ")) {
            return ApecUtils.RemoveCharSequence("Purse: ",s);
        } else if (ApecUtils.containedByCharSequence(s,"Piggy: ")) {
            return ApecUtils.RemoveCharSequence("Piggy: ",s);
        }
        return "";
    }
}
