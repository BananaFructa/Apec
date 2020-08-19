package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class SkillText extends GUIComponent {

    public SkillText() {
        super(GUIComponentID.SKILL_TEXT);
    }

    int stringWidth = 0;

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.draw(ps,sd,od,sr,editingMode);
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SKILL_TEXT)) {
            GlStateManager.scale(scale, scale, scale);
            Vector2f p = this.getAnchorPointPosition();
            p = ApecUtils.addVec(p, this.delta_position);
            if (ps.SkillIsShown) {
                stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(ps.SkillInfo);
                if (ps.SkillInfo.contains("Rune")) {
                    ApecUtils.drawThiccBorderString(ps.SkillInfo, (int) (p.x / scale), (int) (p.y / scale - 10), 0x6B3694);
                } else {
                    ApecUtils.drawThiccBorderString(ps.SkillInfo, (int) (p.x / scale), (int) (p.y / scale - 10), 0x4ca7a8);
                }
            } else if (editingMode) {
                stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth("+0.0 Farming (0/0)");
                ApecUtils.drawThiccBorderString("+0.0 Farming (0/0)", (int) (p.x / scale), (int) (p.y / scale - 10), 0x4ca7a8);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f((int) (g_sr.getScaledWidth() / 2 ) - stringWidth/2f, g_sr.getScaledHeight() - 30);
    }

    @Override
    public Vector2f getBoundingPoint() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return ApecUtils.addVec(getRealAnchorPoint(),new Vector2f(stringWidth*scale,-11*scale));
    }

}
