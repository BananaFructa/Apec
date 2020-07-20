package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import Apec.EventIDs;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

public class EventLister extends GUIComponent {

    public EventLister() {
        super(GUIModifier.GUiComponentID.EVENT_DISPLAY);
    }

    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr) {
        if (od.currentEvents != null) {
            for (int i = 0; i < od.currentEvents.size(); i++) {
                drawIconForID(od.currentEvents.get(i), sr.getScaledWidth() - 22 - i * 20, 65);
            }
        }
    }

    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od,ScaledResolution sr) {

    }

    private void drawIconForID (EventIDs eventID,int x,int y) {
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        if (mc.thePlayer.isInsideOfMaterial(Material.water)) y += 17;
        switch (eventID) {
            case INV_FULL:
                gi.drawTexturedModalRect(x,y,241,0,15,13);
                break;
            case TRADE_OUT:
                gi.drawTexturedModalRect(x,y,241,13,15,13);
                break;
            case TRADE_IN:
                gi.drawTexturedModalRect(x,y,241,26,15,13);
                break;
            case COIN_COUNT:
                gi.drawTexturedModalRect(x,y,241,39,15,13);
                break;
            case SERVER_REBOOT:
                gi.drawTexturedModalRect(x,y,241,52,15,13);
                break;
            case HIGH_PING:
                gi.drawTexturedModalRect(x,y,241,65,15,13);
                break;
        }
    }

}
