package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.*;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Settings.SettingID;
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

    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
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
            Vector2f warningPos = getAnchorPointPosition(sr);
            warningPos = ApecUtils.addVec(warningPos,this.delta_position);
            for (int i = 0; i < od.currentEvents.size(); i++) {
                drawIconForID(od.currentEvents.get(i), (int)(warningPos.x/scale - (i+1) * 20),(int)(warningPos.y/scale));
            }
        }
        GlStateManager.popMatrix();
    }

    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od,ScaledResolution sr,boolean editingMode) {

    }

    private void drawIconForID (EventIDs eventID,int x,int y) {
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        if (mc.thePlayer.isInsideOfMaterial(Material.water) &&
                ApecUtils.zeroMagnitude(this.delta_position) &&
                ApecUtils.zeroMagnitude(((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.AIR_BAR).delta_position) &&
                (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_AIR_BAR))) y += 17;
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

    @Override
    public Vector2f getAnchorPointPosition(ScaledResolution sr) {
        return new Vector2f(sr.getScaledWidth() - 2, 65);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(new ScaledResolution(mc)),new Vector2f(-120*scale,15*scale));
    }

}
