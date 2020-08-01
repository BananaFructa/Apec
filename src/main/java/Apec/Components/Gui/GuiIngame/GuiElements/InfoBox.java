package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.util.vector.Vector2f;

public class InfoBox extends GUIComponent {

    float yDecremetor = 0;

    public InfoBox() {
        super(GUIComponentID.INFO_BOX);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {

        boolean isInChat = Minecraft.getMinecraft().currentScreen instanceof GuiChat;
        float fps = Minecraft.getDebugFPS();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ANIMATION)) {
            // Calculating delta time for constant smooth velocity
            if (isInChat && yDecremetor < 40) yDecremetor += 1 * (120f / fps);
            if (!isInChat && yDecremetor > 0) yDecremetor -= 1 * (120f / fps);
        } else {
            yDecremetor = 0;
        }
        if (yDecremetor < 0) yDecremetor = 0;
        if (yDecremetor > 40) yDecremetor = 40;
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        gi.drawRect(0 + (int) delta_position.x, sr.getScaledHeight() - 20 + (int) delta_position.y + (int)yDecremetor, sr.getScaledWidth() + (int) delta_position.x, sr.getScaledHeight() + (int) delta_position.y, 0xca0a0a0a);
        Vector2f GuiPos = getAnchorPointPosition(sr);

        GuiPos = ApecUtils.addVec(GuiPos, delta_position);
        GuiPos.y += yDecremetor;

        if (mc.gameSettings.guiScale == 0) {
            mc.fontRendererObj.drawString(sd.Purse, (int) GuiPos.x, (int) GuiPos.y, 0xffffff, false);
            mc.fontRendererObj.drawString(sd.Zone, (int) GuiPos.x + 100, (int) GuiPos.y, 0xffffff, false);
            String DFString = "\u00a7a" + ps.Defence + " Defence";
            mc.fontRendererObj.drawString(DFString, (int) GuiPos.x + 230, (int) GuiPos.y, 0xffffff);
        } else {
            mc.fontRendererObj.drawString(sd.Purse, (int) GuiPos.x, (int) GuiPos.y, 0xffffff, false);
            mc.fontRendererObj.drawString(sd.Zone, (int) GuiPos.x + 160, (int) GuiPos.y, 0xffffff, false);
            String DFString = "\u00a7a" + ps.Defence + " Defence";
            mc.fontRendererObj.drawString(DFString, (int) GuiPos.x + 320, (int) GuiPos.y, 0xffffff);
        }
        mc.fontRendererObj.drawString(sd.Date + " " + sd.Hour, (int) sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(sd.Date + " " + sd.Hour) - 15 + delta_position.x, (int) GuiPos.y + delta_position.y, 0xffffff, false);

    }

    @Override
    public Vector2f getAnchorPointPosition(ScaledResolution sr) {
        return new Vector2f(20, sr.getScaledHeight() - 14);
    }
}
