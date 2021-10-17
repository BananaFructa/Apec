package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Components.Gui.GuiIngame.GuiElements.InfoBoxSubElements.*;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class InfoBox extends GUIComponent {

    float yDecremetor = 0;

    public InfoBox() {
        super(GUIComponentID.INFO_BOX);
        addSubComponent(new IBPurse());
        addSubComponent(new IBBits());
        addSubComponent(new IBZone());
        addSubComponent(new IBDefence());
        addSubComponent(new IBStrength());
        addSubComponent(new IBSpeed());
        addSubComponent(new IBCritChance());
        addSubComponent(new IBCritDamage());
        addSubComponent(new IBMithrilPowder());
        addSubComponent(new IBAttackSpeed());
        addSubComponent(new IBGemstonePowder());
        addSubComponent(new IBSoulFlow());
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        Vector2f GuiPos = getCurrentAnchorPoint();

        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/bottomBar.png"));
        int DrawCounts = (int)(sr.getScaledWidth()/256) + 1;
        for (int i = 0;i < DrawCounts;i++) {
            gi.drawTexturedModalRect(GuiPos.x + i * 256, GuiPos.y + (int)yDecremetor,0,0,256,20);
            if (mc.gameSettings.guiScale == 1) {
                gi.drawTexturedModalRect(GuiPos.x + i * 256, GuiPos.y + 13*scale + (int)yDecremetor,0,0,256,20);
            }
        }
        GlStateManager.scale(scale,scale,1);
        super.drawTex(ps, sd, od, ts, sr, editingMode);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_AUTO_SCALING_BB)) {
            if (mc.gameSettings.guiScale == 0) {
                this.setScale(0.72f);
            } else if (mc.gameSettings.guiScale == 3) {
                this.setScale(0.8f);
            } else if (mc.gameSettings.guiScale == 2) {
                this.setScale(1f);
            } else if (mc.gameSettings.guiScale == 1) {
                this.setScale(1.5f);
            }
        } else {
            scale = 1;
        }
        GlStateManager.scale(scale, scale, 1);
        super.draw(ps, sd, od, ts, sr, editingMode);
        boolean isInChat = Minecraft.getMinecraft().currentScreen instanceof GuiChat;
        float fps = Minecraft.getDebugFPS();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ANIMATION) && !ApecMain.Instance.settingsManager.getSettingState(SettingID.BB_ON_TOP)) {
            // Calculating delta time for constant smooth velocity
            if (isInChat && yDecremetor < 40) yDecremetor += 1 * (120f / fps);
            if (!isInChat && yDecremetor > 0) yDecremetor -= 1 * (120f / fps);
        } else {
            yDecremetor = 0;
        }
        if (yDecremetor < 0) yDecremetor = 0;
        if (yDecremetor > 40) yDecremetor = 40;

        GlStateManager.scale(1, 1, 1);
        GlStateManager.popMatrix();

    }

    public int getHeight() {
        return (int)(20 * scale);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        float y = 0;
        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.BB_ON_TOP)) {
            y = g_sr.getScaledHeight() - 20*scale + yDecremetor;
        }
        return new Vector2f(0, y);
    }
}
