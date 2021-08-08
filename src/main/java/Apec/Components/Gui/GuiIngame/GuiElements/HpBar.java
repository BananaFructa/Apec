package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
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
        super.drawTex(ps,sd,od,sr,editingMode);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.HP_BAR)) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;

            Vector2f StatBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),oneOverScale);

            float hpFactor = (ps.Hp > ps.BaseHp) ? 1 : (float) ps.Hp / (float) ps.BaseHp;

            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));

            boolean showAP = ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_ABSORPTION_BAR);
            if (ps.Ap != 0 && showAP) {
                gi.drawTexturedModalRect((int) StatBar.x , (int) StatBar.y , 0, 60, 182, 5);
                gi.drawTexturedModalRect((int) StatBar.x , (int) StatBar.y , 0, 65, (int) (((float) ps.Ap / (float) ps.BaseAp) * 49f), 5);
                gi.drawTexturedModalRect((int) StatBar.x  + 51, (int) StatBar.y , 51, 65, (int) (hpFactor * 131f), 5);
            } else {
                gi.drawTexturedModalRect((int) StatBar.x , (int) StatBar.y , 0, 0, 182, 5);
                gi.drawTexturedModalRect((int) StatBar.x , (int) StatBar.y , 0, 5, (int) (hpFactor * 182f), 5);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return guiModifier.applyGlobalChanges(this,new Vector2f(g_sr.getScaledWidth() - 190, 15));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(182*scale,5*scale);
    }
}
