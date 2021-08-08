package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.ComponentId;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Components.Gui.GuiIngame.SkillType;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
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
            Vector2f SkillBarPos = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),oneOverScale);

            if (ps.SkillIsShown) {
                mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
                float factor = ps.SkillExpPercentage;
                if (factor > 1f) factor = 1f;
                factor *= 182f;
                if (ps.SkillInfo.contains("Rune")) {
                    gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 50, 182, 5);
                    gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 55, (int) factor, 5);
                } else {
                    SkillType skillType =  SkillType.GetSkillType(ps.SkillInfo);
                    if (ApecMain.Instance.settingsManager.getSettingState(SettingID.COLORED_SKILL_XP) && skillType != SkillType.NONE) {
                        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/coloredSkillBars.png"));
                        switch (skillType) {
                            case FARMING:
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 0, 182, 5);
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 5, (int) factor, 5);
                                break;
                            case COMBAT:
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 10, 182, 5);
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 15, (int) factor, 5);
                                break;
                            case MINING:
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 20, 182, 5);
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 25, (int) factor, 5);
                                break;
                            case FORAGING:
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 30, 182, 5);
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 35, (int) factor, 5);
                                break;
                            case ENCHANTING:
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 40, 182, 5);
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 45, (int) factor, 5);
                                break;
                            case FISHING:
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 50, 182, 5);
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 55, (int) factor, 5);
                                break;
                            case ALCHEMY:
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 60, 182, 5);
                                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 65, (int) factor, 5);
                                break;
                        }
                    } else {
                        gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 20, 182, 5);
                        gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 25, (int) factor, 5);
                    }
                }
            } else if (editingMode) {
                gi.drawTexturedModalRect((int) SkillBarPos.x, (int) SkillBarPos.y, 0, 25, 182, 5);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.guiModifier.applyGlobalChanges(this,new Vector2f((int) (g_sr.getScaledWidth() / 2 - 91), g_sr.getScaledHeight() - 30 + 20 * (1 - ((GUIModifier) ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.INFO_BOX).getScale())));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(182*scale,5*scale);
    }
}
