package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.TextComponent;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.MultiColorString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class MpText extends TextComponent {

    public MpText() {
        super(GUIComponentID.MP_TEXT);
    }

    int stringWidth = 0;
    private MultiColorString mainMccs = new MultiColorString();

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.draw(ps,sd,od,sr,editingMode);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.MP_TEXT)) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            Vector2f StatBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),oneOverScale);

            boolean showOp = ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_OP_BAR);

            String MPString = ps.Mp + "/" + ps.BaseMp + " MP";

            if (ps.Op != 0) {
                if (showOp) {
                    String OPString = ps.Op + "/" + ps.BaseOp + " OP";
                    mainMccs.setString(new String[]{OPString + " ", MPString + " MP"}, new int[]{ 0x1966AD, 0x1139bd });
                } else {
                    int totalMp = ps.Op + ps.Mp;
                    mainMccs.setString(new String[]{Integer.toString(totalMp),"/" + ps.BaseMp}, new int[]{ 0x1966AD, 0x1139bd });
                }
            } else {
                mainMccs.setString(new String[]{MPString}, new int[]{ 0x1139bd });
            }

            stringWidth = mainMccs.getStringWidth();
            mainMccs.setXY((int) (StatBar.x - stringWidth), (int) (StatBar.y - 10));
            mainMccs.render();

            GlStateManager.popMatrix();
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.guiModifier.applyGlobalChanges(this,new Vector2f(g_sr.getScaledWidth() - 190 + 112 + 70, 34));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-stringWidth*scale,-11*scale);
    }

}
