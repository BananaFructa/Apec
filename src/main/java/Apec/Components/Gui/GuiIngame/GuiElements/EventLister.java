package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.*;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class EventLister extends GUIComponent {

    public EventLister() {
        super(GUIComponentID.WARNING_ICONS);
    }

    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps,sd,od,sr,editingMode);
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (editingMode) {
            od.currentEvents = new ArrayList<EventIDs>() {{
               add(EventIDs.INV_FULL);
               add(EventIDs.TRADE_OUT);
               add(EventIDs.TRADE_IN);
               add(EventIDs.COIN_COUNT);
               add(EventIDs.SERVER_REBOOT);
               add(EventIDs.HIGH_PING);
            }};
        }
        if (od.currentEvents != null) {
            Vector2f warningPos = ApecUtils.scalarMultiply(this.getCurrentAnchorPoint(),oneOverScale);
            for (int i = 0; i < od.currentEvents.size(); i++) {
                drawIconForID(od.currentEvents.get(i), (int)(warningPos.x - (i+1) * 20),(int)(warningPos.y));
            }
        }
        GlStateManager.popMatrix();
    }

    private void drawIconForID (EventIDs eventID,int x,int y) {
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        if (mc.thePlayer.isInsideOfMaterial(Material.water) &&
                ApecUtils.zeroMagnitude(this.delta_position) &&
                ApecUtils.zeroMagnitude(((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.AIR_BAR).getDeltaPosition()) &&
                (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_AIR_BAR))) y += 17;
        switch (eventID) {
            case INV_FULL:
                gi.drawTexturedModalRect(x,y,1,226,14,13);
                break;
            case TRADE_OUT:
                gi.drawTexturedModalRect(x,y,16,226,15,13);
                break;
            case TRADE_IN:
                gi.drawTexturedModalRect(x,y,32,226,15,13);
                break;
            case COIN_COUNT:
                gi.drawTexturedModalRect(x,y,48,226,13,13);
                break;
            case SERVER_REBOOT:
                gi.drawTexturedModalRect(x,y,62,226,15,13);
                break;
            case HIGH_PING:
                gi.drawTexturedModalRect(x,y,78,226,15,13);
                break;
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth() - 2, 65);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getCurrentAnchorPoint(),new Vector2f(-120*scale,15*scale));
    }

}
