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
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SKILL_XP) || editingMode) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
            Vector2f SkillBarPos = getAnchorPointPosition(sr);

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
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        Vector2f p = this.getAnchorPointPosition(sr);
        p = ApecUtils.addVec(p,this.delta_position);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SKILL_XP) || editingMode) {
            if (ps.SkillIsShown) {
                if (ps.SkillInfo.contains("Rune")) {
                    ApecUtils.drawThiccBorderString(ps.SkillInfo, (int)(p.x/scale + 91 - mc.fontRendererObj.getStringWidth(ps.SkillInfo) / 2), (int)(p.y/scale - 10), 0x6B3694);
                } else {
                    ApecUtils.drawThiccBorderString(ps.SkillInfo, (int)(p.x/scale + 91 - mc.fontRendererObj.getStringWidth(ps.SkillInfo) / 2), (int)(p.y/scale - 10), 0x4ca7a8);
                }
            } else if (editingMode) {
                ApecUtils.drawThiccBorderString("+0.0 Farming (0/0)", (int)(p.x/scale + 91 - mc.fontRendererObj.getStringWidth("+0.0 Farming (0/0)") / 2), (int)(p.y/scale - 10), 0x4ca7a8);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition(ScaledResolution sr) {
        return new Vector2f((int) (sr.getScaledWidth() / 2 - 91), sr.getScaledHeight() - 30);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(new ScaledResolution(mc)),new Vector2f(182*scale,5*scale));
    }
}
