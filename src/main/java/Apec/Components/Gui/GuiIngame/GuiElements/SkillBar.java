package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

public class SkillBar extends GUIComponent {

    public SkillBar () {
        super(GUIModifier.GUiComponentID.SKILL_BARS);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SKILL_XP)) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
            Vector2f SkillBarPos = new Vector2f((int) (sr.getScaledWidth() / 2 - 91), sr.getScaledHeight() - 30);

            SkillBarPos = ApecUtils.addVec(SkillBarPos, delta_position);

            if (ps.SkillIsShown) {
                float factor;
                if (ps.BaseSkillExp == 0) factor = 0;
                else factor = (float) ps.SkillExp / (float) ps.BaseSkillExp * 182f;
                if (ps.SkillInfo.contains("Rune")) {
                    gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 50, 182, 5);
                    gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 55, (int) factor, 5);
                } else {
                    gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 20, 182, 5);
                    gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 25, (int) factor, 5);
                }
            }
        }
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SKILL_XP)) {
            if (ps.SkillIsShown) {
                if (ps.SkillInfo.contains("Rune")) {
                    ApecUtils.drawThiccBorderString(ps.SkillInfo, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(ps.SkillInfo) / 2 + (int) delta_position.x, sr.getScaledHeight() - 40 + (int) delta_position.x, 0x6B3694);
                } else {
                    ApecUtils.drawThiccBorderString(ps.SkillInfo, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(ps.SkillInfo) / 2 + (int) delta_position.x, sr.getScaledHeight() - 40 + (int) delta_position.y, 0x4ca7a8);
                }
            }
        }
    }

}
