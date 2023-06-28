package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class RiftTimer extends GUIComponent {

    int stringWidth = 0;

    public RiftTimer() {
        super(GUIComponentID.RIFT_TIMER);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, sr, editingMode);
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_RIFT_TIMER) && ApecMain.Instance.dataExtractor.isInTheRift) {
            GlStateManager.scale(scale, scale, scale);
            Vector2f StatBar = ApecUtils.scalarMultiply(this.getCurrentAnchorPoint(), oneOverScale);

            String RiftString = "Rift: " + ps.RiftTimer;
            ApecUtils.drawThiccBorderString(RiftString, (int) (StatBar.x - mc.fontRendererObj.getStringWidth(RiftString)), (int) (StatBar.y - 10), 0xAA00AA);
            stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(RiftString);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.guiModifier.applyGlobalChanges(this, new Vector2f(g_sr.getScaledWidth() - 190 + 112 + 70, 72));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-stringWidth * scale, -11 * scale);
    }
}
