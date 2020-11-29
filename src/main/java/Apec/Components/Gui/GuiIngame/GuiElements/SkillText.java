package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.ComponentId;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Components.Gui.GuiIngame.SkillType;
import Apec.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
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
                    SkillType skillType =  SkillType.GetSkillType(ps.SkillInfo);
                    int color = 0x4ca7a8;
                    if (ApecMain.Instance.settingsManager.getSettingState(SettingID.COLORED_SKILL_XP) && skillType != SkillType.NONE) {
                        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/coloredSkillBars.png"));
                        switch (skillType) {
                            case FARMING:
                                color = 0xD0CE30;
                                break;
                            case COMBAT:
                                color = 0xDC3615;
                                break;
                            case MINING:
                                color = 0x797979;
                                break;
                            case FORAGING:
                                color = 0x237926;
                                break;
                            case ENCHANTING:
                                color = 0x711C99;
                                break;
                            case FISHING:
                                color = 0x184A87;
                                break;
                            case ALCHEMY:
                                color = 0x981B4C;
                                break;
                        }
                    }
                    ApecUtils.drawThiccBorderString(ps.SkillInfo, (int) (p.x / scale), (int) (p.y / scale - 10), color);
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
        return new Vector2f((int) (g_sr.getScaledWidth() / 2 ) - stringWidth/2f, g_sr.getScaledHeight() - 30 + 20 * (1 - ((GUIModifier) ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.INFO_BOX).scale));
    }

    @Override
    public Vector2f getBoundingPoint() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return ApecUtils.addVec(getRealAnchorPoint(),new Vector2f(stringWidth*scale,-11*scale));
    }

}
