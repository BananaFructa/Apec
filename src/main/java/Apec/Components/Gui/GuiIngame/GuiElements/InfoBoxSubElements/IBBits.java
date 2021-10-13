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

public class IBBits extends GUIComponent {

    private int BitsLength = 0;

    public IBBits () {
        super(GUIComponentID.IB_BITS);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        if (!UseIcons) return;
        Vector2f pos = getCurrentAnchorPoint();
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        gi.drawTexturedModalRect((int) (pos.x * oneOverScale), pos.y * oneOverScale - 1, 8, 216, 5, 9);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);

        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);

        String bitText = (UseIcons ? ApecUtils.RemoveCharSequence("Bits: ",sd.Bits) : sd.Bits);
        Vector2f pos = getCurrentAnchorPoint();

        ApecUtils.drawStylizedString(
                bitText,
                (int) (pos.x +  (UseIcons ? 9 : 0) * oneOverScale),
                (int) (pos.y * oneOverScale),
                0xffffff
        );

        BitsLength = mc.fontRendererObj.getStringWidth(bitText);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(120*scale + 20 * scale, 6*scale);
    }

    @Override
    public Vector2f getBoundingPoint() {
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        return new Vector2f(BitsLength + (UseIcons ? 9 : 0)*scale, 10*scale);
    }
}
