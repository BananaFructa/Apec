package Apec.Components.Gui.GuiElements;

import Apec.ApecUtils;
import Apec.Components.Gui.ApecGuiIngame;
import Apec.Components.Gui.GUIModifier;
import Apec.Components.Gui.GuiElements.GUIComponent;
import Apec.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

public class SkillBar extends GUIComponent {

    public SkillBar () {
        super(GUIModifier.GUiComponentID.SKILL_BARS);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, ScaledResolution sr) {
        ApecGuiIngame agi = (ApecGuiIngame) Minecraft.getMinecraft().ingameGUI;
        Vector2f SkillBarPos = new Vector2f((int)(sr.getScaledWidth() / 2 - 91), sr.getScaledHeight() - 30);

        SkillBarPos = ApecUtils.addVec(SkillBarPos,delta_position);

        if (ps.SkillIsShown) {
            float factor;
            if (ps.BaseSkillExp == 0) factor = 0;
            else factor = (float) ps.SkillExp / (float) ps.BaseSkillExp * 182f;
            if (ps.SkillInfo.contains("Rune")) {
                agi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 50, 182, 5);
                agi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 55, (int)factor, 5);
            } else {
                agi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 20, 182, 5);
                agi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 25, (int)factor, 5);
            }
        }
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, ScaledResolution sr) {
        ApecGuiIngame agi = (ApecGuiIngame) Minecraft.getMinecraft().ingameGUI;
        if (ps.SkillIsShown) {
            if (ps.SkillInfo.contains("Rune")) {
                agi.drawThiccBorderString(ps.SkillInfo, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(ps.SkillInfo) / 2 + (int) delta_position.x, sr.getScaledHeight() - 40 + (int) delta_position.x, 0x6B3694);
            } else {
                agi.drawThiccBorderString(ps.SkillInfo, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(ps.SkillInfo) / 2 + (int) delta_position.x, sr.getScaledHeight() - 40 + (int) delta_position.y, 0x4ca7a8);
            }
        }
    }

}
