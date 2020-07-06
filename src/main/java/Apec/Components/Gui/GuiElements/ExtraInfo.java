package Apec.Components.Gui.GuiElements;

import Apec.ApecUtils;
import Apec.Components.Gui.ApecGuiIngame;
import Apec.Components.Gui.GUIModifier;
import Apec.DataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

public class ExtraInfo extends GUIComponent {

    public ExtraInfo() {
        super(GUIModifier.GUiComponentID.EXTRA_SCR_INF);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, ScaledResolution sr) {
        ApecGuiIngame agi = (ApecGuiIngame) Minecraft.getMinecraft().ingameGUI;
        Vector2f ExtraScoreInfo = new Vector2f(5,85);

        ExtraScoreInfo = ApecUtils.addVec(ExtraScoreInfo,delta_position);

        if (!sd.ExtraInfo.isEmpty()) {
            for (int i = 0;i < sd.ExtraInfo.size();i++) {
                agi.drawThiccBorderString(sd.ExtraInfo.get(i), (int) ExtraScoreInfo.x, (int) ExtraScoreInfo.y + i * 11, 0x0ffffff);
            }
        }
    }
}
