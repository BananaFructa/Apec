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

public class AirBar extends GUIComponent {

    public AirBar() {
        super(GUIComponentID.AIR_BAR);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_AIR_BAR)) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;

            Vector2f StatBar = this.getAnchorPointPosition(sr);
            StatBar = ApecUtils.addVec(StatBar, delta_position);

            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));

            if (mc.thePlayer.isInsideOfMaterial(Material.water) || editingMode) {
                float airPrec = mc.thePlayer.getAir() / 300f;
                if (airPrec < 0) airPrec = 0;
                gi.drawTexturedModalRect((int) StatBar.x/scale, (int) StatBar.y/scale, 0, 40, 182, 5);
                gi.drawTexturedModalRect((int) StatBar.x/scale, (int) StatBar.y/scale, 0, 45, (int) (182f * airPrec), 5);
            }

        }
        GlStateManager.popMatrix();
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_AIR_BAR)) {
            Vector2f StatBar = this.getAnchorPointPosition(sr);

            StatBar = ApecUtils.addVec(StatBar, delta_position);

            if (mc.thePlayer.isInsideOfMaterial(Material.water) || editingMode) {
                float airPrec = (mc.thePlayer.getAir() / 300f) * 100;
                if (airPrec < 0) airPrec = 0;
                String ARString = (int) airPrec + "% Air";
                ApecUtils.drawThiccBorderString(ARString, (int)(StatBar.x/scale + 112 + 70 - mc.fontRendererObj.getStringWidth(ARString)), (int) (StatBar.y/scale - 10), 0x8ba6b2);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition(ScaledResolution sr) {
        return new Vector2f(sr.getScaledWidth() - 190, 72);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(new ScaledResolution(mc)),new Vector2f(182*scale,5*scale));
    }

}
