package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

public class HpBar extends GUIComponent {

    public HpBar() {
        super(GUIComponentID.HP_BAR);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.HP_BAR)) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;

            Vector2f StatBar = this.getAnchorPointPosition(sr);
            StatBar = ApecUtils.addVec(StatBar, delta_position);

            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
            float hpFactor = (ps.Hp > ps.BaseHp) ? 1 : (float) ps.Hp / (float) ps.BaseHp;

            if (ps.Ap != 0) {
                gi.drawTexturedModalRect((int) StatBar.x/scale, (int) StatBar.y/scale, 0, 60, 182, 5);
                gi.drawTexturedModalRect((int) StatBar.x/scale, (int) StatBar.y/scale, 0, 65, (int) (((float) ps.Ap / (float) ps.BaseAp) * 49f), 5);
                gi.drawTexturedModalRect((int) StatBar.x/scale + 51, (int) StatBar.y/scale, 51, 65, (int) (hpFactor * 131f), 5);
            } else {
                gi.drawTexturedModalRect((int) StatBar.x/scale, (int) StatBar.y/scale, 0, 0, 182, 5);
                gi.drawTexturedModalRect((int) StatBar.x/scale, (int) StatBar.y/scale, 0, 5, (int) (hpFactor * 182f), 5);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.HP_BAR)) {
            Vector2f StatBar = this.getAnchorPointPosition(sr);

            StatBar = ApecUtils.addVec(StatBar, delta_position);

            String HPString = ps.Hp + "/" + ps.BaseHp + " HP";
            ApecUtils.drawThiccBorderString(HPString, (int) (StatBar.x/scale + 112 + 70 - mc.fontRendererObj.getStringWidth(HPString)), (int) (StatBar.y/scale - 10), 0xd10808);

            if (ps.Ap != 0) {
                String APString = ps.Ap + "/" + ps.BaseAp + " AP";
                ApecUtils.drawThiccBorderString(APString, (int)(StatBar.x/scale + 112 + 65 - mc.fontRendererObj.getStringWidth(APString) - mc.fontRendererObj.getStringWidth(HPString)), (int)(StatBar.y/scale - 10), 0xC8AC35);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition(ScaledResolution sr) {
        return new Vector2f(sr.getScaledWidth() - 190, 15);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(new ScaledResolution(mc)),new Vector2f(182*scale,5*scale));
    }
}
