package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.TextComponent;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class XpText extends TextComponent {

    public XpText() {
        super(GUIComponentID.XP_TEXT);
    }

    int stringWidth = 0;

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps,sd,od,ts,sr,editingMode);
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.XP_TEXT)) {
            GlStateManager.scale(scale, scale, scale);
            Vector2f StatBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),oneOverScale);

            String XPString;
            if (ApecMain.Instance.dataExtractor.isInTheCatacombs && ApecMain.Instance.settingsManager.getSettingState(SettingID.XP_BAR)) {
                XPString = "Ultimate Cooldown " + ApecUtils.ReduceToTwoDecimals(this.mc.thePlayer.experience * 100 + 0.1f) + "%";
            } else {
                XPString = "Lvl " + this.mc.thePlayer.experienceLevel + " XP";
            }
            ApecUtils.drawStylizedString(XPString, (int) (StatBar.x - mc.fontRendererObj.getStringWidth(XPString)), (int) (StatBar.y - 10), 0x80ff20);
            stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(XPString);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.guiModifier.applyGlobalChanges(this,new Vector2f(g_sr.getScaledWidth() - 190 + 112 + 70, 53));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-stringWidth*scale,-11*scale);
    }

}
