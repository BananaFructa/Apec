package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class AirText extends GUIComponent {

    public AirText() {
        super(GUIComponentID.AIR_TEXT);
    }

    int stringWidth = 0;

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.draw(ps,sd,od,sr,editingMode);
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.AIR_TEXT)) {
            GlStateManager.scale(scale, scale, scale);
            Vector2f StatBar = ApecUtils.scalarMultiply(this.getCurrentAnchorPoint(),oneOverScale);

            if (mc.thePlayer.isInsideOfMaterial(Material.water) || editingMode) {
                float airPrec = (mc.thePlayer.getAir() / 300f) * 100;
                if (airPrec < 0) airPrec = 0;
                String ARString = (int) airPrec + "% Air";
                ApecUtils.drawThiccBorderString(ARString, (int) (StatBar.x - mc.fontRendererObj.getStringWidth(ARString)), (int) (StatBar.y - 10), 0x8ba6b2);
                stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(ARString);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth() - 190  + 112 + 70, 72);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getCurrentAnchorPoint(),new Vector2f(-stringWidth*scale,-11*scale));
    }

}
