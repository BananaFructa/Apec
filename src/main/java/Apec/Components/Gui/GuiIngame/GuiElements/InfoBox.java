package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class InfoBox extends GUIComponent {

    // FIXME: Repair improper usages of the gui element position API

    float yDecremetor = 0;

    public InfoBox() {
        super(GUIComponentID.INFO_BOX,6);
    }

    public int PurseStringLength = 0,BitsLength = 0,ZoneStringLength = 0,DefenceStringLength = 0,TimeStringLength = 0, ModeStringLength = 0, KuudraStringLength = 0;

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, sr, editingMode);
        Vector2f GuiPos = getCurrentAnchorPoint();

        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
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
        if (UseIcons) {

            GuiPos.y += yDecremetor + 6*scale;

            if (ApecMain.Instance.dataExtractor.isInTheRift) {
                GlStateManager.pushMatrix();
                mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/rifticons.png"));
                gi.drawTexturedModalRect((int)(GuiPos.x + 20 + (subComponentDeltas.get(0).getX())*oneOverScale), (GuiPos.y+ subComponentDeltas.get(0).getY())*oneOverScale -1,1,1,6,9);
                GlStateManager.popMatrix();
            }else {
                mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
                gi.drawTexturedModalRect((int)(GuiPos.x + 20 + (subComponentDeltas.get(0).getX())*oneOverScale), (GuiPos.y+ subComponentDeltas.get(0).getY())*oneOverScale -1,1,216,6,9);
            }
            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
            gi.drawTexturedModalRect((int)(GuiPos.x + 20 + (subComponentDeltas.get(1).getX())*oneOverScale + 120), (GuiPos.y+ subComponentDeltas.get(1).getY())*oneOverScale -1,8,216,5,9);
            if (ApecMain.Instance.dataExtractor.isInTheCatacombs) {
                gi.drawTexturedModalRect((int) (GuiPos.x + 20 + (subComponentDeltas.get(2).getX())*oneOverScale + 220 - 1), (GuiPos.y + subComponentDeltas.get(2).getY())*oneOverScale -1, 24, 216, 7, 8);
            } else {
                gi.drawTexturedModalRect((int) (GuiPos.x + 20 + (subComponentDeltas.get(2).getX())*oneOverScale + 220), (GuiPos.y + subComponentDeltas.get(2).getY())*oneOverScale -1, 14, 216, 9, 9);
            }
            if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) || editingMode) {
                gi.drawTexturedModalRect((int) (GuiPos.x + 20 + (subComponentDeltas.get(3).getX()) * oneOverScale + 360),  (GuiPos.y + subComponentDeltas.get(3).getY()) * oneOverScale - 1, 32, 215, 7, 10);
            }
        }
        GlStateManager.scale(1,1,1);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
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
        super.draw(ps, sd, od, sr, editingMode);
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
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        Vector2f GuiPos = getCurrentAnchorPoint();

        GuiPos.y += yDecremetor + 6*scale;

        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);

        String purseText = (UseIcons ? RemovePurseText(sd.Purse) : sd.Purse);
        String zoneText = (UseIcons ? RemoveZoneText(sd.Zone) : sd.Zone);
        String defenceText = (UseIcons ? "\u00a7a" + ps.Defence : "\u00a7a" + ps.Defence + " Defence");
        String bitText = (UseIcons ? ApecUtils.RemoveCharSequence("Bits: ",sd.Bits) : sd.Bits);
        String modeText = (UseIcons ? ApecUtils.RemoveCharSequence("Mode: ",sd.GameMode) : sd.GameMode);
        String kuudraText = EnumChatFormatting.GOLD + ps.KuudraTieredBonus;
        boolean inTheCatacombs = ApecMain.Instance.dataExtractor.isInTheCatacombs;
        mc.fontRendererObj.drawString(
                purseText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(0).getX() + (UseIcons ? 9 : 0)) * oneOverScale),
                (int) ((GuiPos.y + subComponentDeltas.get(0).getY()) * oneOverScale),
                0xffffff, false
        );
        mc.fontRendererObj.drawString(
                bitText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(1).getX() + (UseIcons ? 9 : 0)) * oneOverScale + 120),
                (int) ((GuiPos.y + subComponentDeltas.get(1).getY()) * oneOverScale),
                0xffffff, false
        );
        int zoneAddX = (inTheCatacombs ? 5 : 9) ;
        mc.fontRendererObj.drawString(
                zoneText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(2).getX() + (UseIcons ? zoneAddX : 0)) * oneOverScale + 220),
                (int) ((GuiPos.y + subComponentDeltas.get(2).getY()) * oneOverScale),
                0xffffff, false
        );

        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) || editingMode) {
            mc.fontRendererObj.drawString(
                    defenceText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(3).getX() + (UseIcons ? 10 : 0)) * oneOverScale + 360),
                    (int) ((GuiPos.y + subComponentDeltas.get(3).getY()) * oneOverScale),
                    0xffffff
            );
        }

        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_GAME_MODE_OUT_OF_BB) || editingMode) {
            mc.fontRendererObj.drawString(
                    modeText,
                    (int) ((GuiPos.x + delta_position.x + subComponentDeltas.get(4).getX()) * oneOverScale + (ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) ? 360 : 460)),
                    (int) ((GuiPos.y + delta_position.y + subComponentDeltas.get(4).getY()) * oneOverScale),
                    0xffffff, false
            );
        }

        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_KUUDRA_SET_BONUS_OUT_OF_BB) || editingMode) {
            boolean showGameMode = modeText != "";
            int xWithDefenceAndGameMode = (ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) ? 360 : 460) + (showGameMode ? (ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_GAME_MODE_OUT_OF_BB) ?  0 : 100) : 0);
            mc.fontRendererObj.drawString(
                    kuudraText,
                    (int) ((GuiPos.x + delta_position.x + subComponentDeltas.get(5).getX()) * oneOverScale + xWithDefenceAndGameMode),
                    (int) ((GuiPos.y + delta_position.y + subComponentDeltas.get(5).getY()) * oneOverScale),
                    0xffffff, false
            );
        }

        PurseStringLength = mc.fontRendererObj.getStringWidth(purseText);
        BitsLength = mc.fontRendererObj.getStringWidth(bitText);
        ZoneStringLength = mc.fontRendererObj.getStringWidth(zoneText);
        DefenceStringLength = mc.fontRendererObj.getStringWidth(defenceText);
        TimeStringLength = mc.fontRendererObj.getStringWidth(sd.Date + " " + sd.Hour);
        ModeStringLength = mc.fontRendererObj.getStringWidth(modeText);
        KuudraStringLength = mc.fontRendererObj.getStringWidth(kuudraText);

        mc.fontRendererObj.drawString(
                sd.Date + " " + sd.Hour,
                (int) ((sr.getScaledWidth() - 15 + delta_position.x + subComponentDeltas.get(4).getX()) * oneOverScale - mc.fontRendererObj.getStringWidth(sd.Date + " " + sd.Hour)),
                (int) ((GuiPos.y + delta_position.y + subComponentDeltas.get(4).getY()) * oneOverScale), 0xffffff, false
        );
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
            y = g_sr.getScaledHeight() - 20*scale;
        }
        return new Vector2f(0, y);
    }

    @Override
    public List<Vector2f> getSubElementsAnchorPoints() {
        return new ArrayList<Vector2f>() {{
            add(new Vector2f(0 + 20 * scale, 6*scale));
            add(new Vector2f(120*scale + 20 * scale, 6*scale));
            add(new Vector2f(220*scale + 20 * scale, 6*scale));
            add(new Vector2f(360*scale + 20 * scale, 6*scale));
            add(new Vector2f(460*scale + 20 * scale, 6*scale));
            add(new Vector2f(520*scale + 20 * scale, 6*scale));
            add(new Vector2f((g_sr.getScaledWidth() - 20), 6*scale));
        }};
    }

    @Override
    public List<Vector2f> getSubElementsBoundingPoints() {
        final boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        boolean inTheCatacombs = ApecMain.Instance.dataExtractor.isInTheCatacombs;
        final int zoneAddX = (inTheCatacombs ? 5 : 9);
        List<Vector2f> RelativeVectors = new ArrayList<Vector2f>(5) {{
            add(new Vector2f(PurseStringLength  + (UseIcons ? 9 : 0)*scale, 10*scale));
            add(new Vector2f( BitsLength + (UseIcons ? 9 : 0)*scale, 10*scale));
            add(new Vector2f(ZoneStringLength + (UseIcons ? zoneAddX : 0)*scale, 10*scale));
            add(new Vector2f(DefenceStringLength + (UseIcons ? 10 : 0)*scale, 10*scale));
            add(new Vector2f(ModeStringLength + (getCurrentAnchorPoint().x)*scale, 10*scale));
            add(new Vector2f(KuudraStringLength + (getCurrentAnchorPoint().x)*scale, 10*scale));
            add(new Vector2f(-TimeStringLength-(getCurrentAnchorPoint().x)*scale, 10*scale));
            // Since the x is relative to the side of the screen and not the parent's x position i removed it's relativity
            // I can do that since the bottom bar cannot be moved so no wack shit is going to happen
        }};
        return ApecUtils.AddVecListToList(RelativeVectors, getSubElementsCurrentAnchorPoints());
    }

    private String RemovePurseText(String s) {
        if (ApecUtils.containedByCharSequence(s,"Purse: ")) {
            return ApecUtils.RemoveCharSequence("Purse: ",s);
        } else if (ApecUtils.containedByCharSequence(s,"Piggy: ")) {
            return ApecUtils.RemoveCharSequence("Piggy: ",s);
        } else if (ApecUtils.containedByCharSequence(s, "Motes: ")) {
            return ApecUtils.RemoveCharSequence("Motes: ", s);
        }
        return "";
    }

    public String RemoveZoneText(String s) {
        if (ApecUtils.containedByCharSequence(s, "\u23E3")) {
            return ApecUtils.RemoveCharSequence("\u23E3", s);
        } else if (ApecUtils.containedByCharSequence(s, "\u0444")) {
            return ApecUtils.RemoveCharSequence("\u0444", s);
        }

        return s;
    }
}
