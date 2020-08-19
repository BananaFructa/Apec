package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class SkillBar extends GUIComponent {

    public SkillBar () {
        super(GUIComponentID.SKILL_BAR);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        super.drawTex(ps,sd,od,sr,editingMode);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SKILL_XP) || editingMode) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
            Vector2f SkillBarPos = getAnchorPointPosition();

            SkillBarPos = ApecUtils.addVec(SkillBarPos, delta_position);

            if (ps.SkillIsShown) {
                float factor;
                if (ps.BaseSkillExp == 0) factor = 0;
                else factor = (float) ps.SkillExp / (float) ps.BaseSkillExp * 182f;
                if (ps.SkillInfo.contains("Rune")) {
                    gi.drawTexturedModalRect((int) SkillBarPos.x/scale, (int) SkillBarPos.y/scale, 0, 50, 182, 5);
                    gi.drawTexturedModalRect((int) SkillBarPos.x/scale, (int) SkillBarPos.y/scale, 0, 55, (int) factor, 5);
                } else {
                    gi.drawTexturedModalRect((int) SkillBarPos.x/scale, (int) SkillBarPos.y/scale, 0, 20, 182, 5);
                    gi.drawTexturedModalRect((int) SkillBarPos.x/scale, (int) SkillBarPos.y/scale, 0, 25, (int) factor, 5);
                }
            } else if (editingMode) {
                gi.drawTexturedModalRect((int) SkillBarPos.x/scale, (int) SkillBarPos.y/scale, 0, 25, 182, 5);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f((int) (g_sr.getScaledWidth() / 2 - 91), g_sr.getScaledHeight() - 30);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(),new Vector2f(182*scale,5*scale));
    }
}
