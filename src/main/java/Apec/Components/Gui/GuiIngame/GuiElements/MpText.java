package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class MpText extends GUIComponent {

    public MpText() {
        super(GUIComponentID.MP_TEXT);
    }

    int stringWidth = 0;

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.draw(ps,sd,od,sr,editingMode);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.MP_TEXT)) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            Vector2f StatBar = this.getAnchorPointPosition();

            StatBar = ApecUtils.addVec(StatBar, delta_position);

            String MPString = ps.Mp + "/" + ps.BaseMp + " MP";
            ApecUtils.drawThiccBorderString(MPString, (int) (StatBar.x / scale - mc.fontRendererObj.getStringWidth(MPString)), (int) (StatBar.y / scale - 10), 0x1139bd);
            stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(MPString);

            GlStateManager.popMatrix();
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth() - 190 + 112 + 70, 34);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(),new Vector2f(-stringWidth*scale,-11*scale));
    }

}
