package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Events.ApecSettingChangedState;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.TextComponent;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.MultiColorString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Vector2f;

public class MpText extends TextComponent {

    public MpText() {
        super(GUIComponentID.MP_TEXT);
    }

    @Override
    public void init() {
        super.init();
        this.enabled = ApecMain.Instance.settingsManager.getSettingState(SettingID.MP_TEXT);
    }

    private int stringWidth = 0;
    private boolean centered = false;
    private MultiColorString mainMccs = new MultiColorString();

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        Vector2f StatBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), oneOverScale);

        boolean showOp = ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_OP_BAR);

        String MPString = ps.Mp + "/" + ps.BaseMp;

        if (ps.Op != 0) {
            if (showOp) {
                String OPString = ps.Op + "/" + ps.BaseOp + " OP";
                mainMccs.setString(new String[]{OPString + " ", MPString + " MP"}, new int[]{0x1966AD, 0x1139bd});
            } else {
                int totalMp = ps.Op + ps.Mp;
                mainMccs.setString(new String[]{Integer.toString(totalMp), "/" + ps.BaseMp}, new int[]{0x1966AD, 0x1139bd});
            }
        } else {
            mainMccs.setString(new String[]{MPString}, new int[]{0x1139bd});
        }

        stringWidth = mainMccs.getStringWidth();
        mainMccs.setXY((int) (StatBar.x - stringWidth), (int) (StatBar.y - 10));
        mainMccs.render();

        GlStateManager.popMatrix();

    }

    @SubscribeEvent
    public void onSettingChanged(ApecSettingChangedState event) {
        if (event.settingID == SettingID.MP_TEXT) {
            this.enabled = event.state;
        }
    }
    
    @Override
    public Vector2f getAnchorPointPosition() {
        return centered ? this.guiModifier.applyGlobalChanges(this,new Vector2f((int) ((stringWidth * 0.5f)), 0)) : this.guiModifier.applyGlobalChanges(this,new Vector2f(g_sr.getScaledWidth() - 190 + 112 + 70, 34));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-stringWidth*scale,-11*scale);
    }

}
