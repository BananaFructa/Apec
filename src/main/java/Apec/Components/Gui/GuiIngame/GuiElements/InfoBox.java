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
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class InfoBox extends GUIComponent {

    // FIXME: Repair improper usages of the gui element position API

    float yDecremetor = 0;

    public InfoBox() {
        super(GUIComponentID.INFO_BOX,10);
    }

    public int PurseStringLength = 0,BitsLength = 0,ZoneStringLength = 0,DefenceStringLength = 0,TimeStringLength = 0, SpeedStringLength = 0, StrengthStringLength = 0, CCStringLength = 0, CDStringLength = 0, ASStringLength = 0;

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
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
            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
            gi.drawTexturedModalRect((int)(GuiPos.x + 20 + (subComponentDeltas.get(0).getX())*oneOverScale), (GuiPos.y+ subComponentDeltas.get(0).getY())*oneOverScale -1,1,216,6,9);
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
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        Vector2f GuiPos = getCurrentAnchorPoint();

        GuiPos.y += yDecremetor + 6*scale;

        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);

        String purseText = (UseIcons ? RemovePurseText(sd.Purse) : sd.Purse);
        String zoneText = (UseIcons ? ApecUtils.RemoveCharSequence("\u23E3", sd.Zone) : sd.Zone);
        String defenceText = (UseIcons ? "\u00a7a" + ps.Defence : "\u00a7a" + ps.Defence + " Defence");
        String bitText = (UseIcons ? ApecUtils.RemoveCharSequence("Bits: ",sd.Bits) : sd.Bits);
        String speedText = "\u2726" + ts.Speed;
        String strengthText = "\u2741" + ts.Strength;
        String critChanceText = "\u2623" + ts.CritChance;
        String critDamageText = "\u2620" + ts.CritDamage;
        String attackSpeedText = "\u2694" + ts.AttackSpeed;

        boolean inTheCatacombs = ApecMain.Instance.dataExtractor.isInTheCatacombs;
        ApecUtils.drawThiccBorderString(
                purseText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(0).getX() + (UseIcons ? 9 : 0)) * oneOverScale),
                (int) ((GuiPos.y + subComponentDeltas.get(0).getY()) * oneOverScale),
                0xffffff
        );
        ApecUtils.drawThiccBorderString(
                bitText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(1).getX() + (UseIcons ? 9 : 0)) * oneOverScale + 120),
                (int) ((GuiPos.y + subComponentDeltas.get(1).getY()) * oneOverScale),
                0xffffff
        );
        int zoneAddX = (inTheCatacombs ? 5 : 9) ;
        ApecUtils.drawThiccBorderString(
                zoneText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(2).getX() + (UseIcons ? zoneAddX : 0)) * oneOverScale + 220),
                (int) ((GuiPos.y + subComponentDeltas.get(2).getY()) * oneOverScale),
                0xffffff
        );

        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) || editingMode) {
            ApecUtils.drawThiccBorderString(
                    defenceText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(3).getX() + (UseIcons ? 10 : 0)) * oneOverScale + 360),
                    (int) ((GuiPos.y + subComponentDeltas.get(3).getY()) * oneOverScale),
                    0xffffff
            );
        }

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SPEED) || editingMode) {
            ApecUtils.drawThiccBorderString(
                    speedText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(5).getX()) * oneOverScale + 500),
                    (int) ((GuiPos.y + subComponentDeltas.get(5).getY()) * oneOverScale),
                    0xffffff
            );
        }
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_STRENGTH) || editingMode) {
            ApecUtils.drawThiccBorderString(
                    strengthText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(6).getX()) * oneOverScale + 540),
                    (int) ((GuiPos.y + subComponentDeltas.get(6).getY()) * oneOverScale),
                    0xFF5555
            );
        }

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CRIT_CHANCE) || editingMode) {
            ApecUtils.drawThiccBorderString(
                    critChanceText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(7).getX()) * oneOverScale + 580),
                    (int) ((GuiPos.y + subComponentDeltas.get(7).getY()) * oneOverScale),
                    0x5555FF
            );
        }

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CRIT_DAMAGE) || editingMode) {
            ApecUtils.drawThiccBorderString(
                    critDamageText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(8).getX()) * oneOverScale + 620),
                    (int) ((GuiPos.y + subComponentDeltas.get(8).getY()) * oneOverScale),
                    0x5555FF
            );
        }

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_ATTACK_SPEED) || editingMode) {
            ApecUtils.drawThiccBorderString(
                    attackSpeedText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(9).getX()) * oneOverScale + 660),
                    (int) ((GuiPos.y + subComponentDeltas.get(9).getY()) * oneOverScale),
                    0xFFFF55
            );
        }

        PurseStringLength = mc.fontRendererObj.getStringWidth(purseText);
        BitsLength = mc.fontRendererObj.getStringWidth(bitText);
        ZoneStringLength = mc.fontRendererObj.getStringWidth(zoneText);
        DefenceStringLength = mc.fontRendererObj.getStringWidth(defenceText);
        SpeedStringLength = mc.fontRendererObj.getStringWidth(speedText);
        StrengthStringLength = mc.fontRendererObj.getStringWidth(strengthText);
        CCStringLength = mc.fontRendererObj.getStringWidth(critChanceText);
        CDStringLength = mc.fontRendererObj.getStringWidth(critDamageText);
        ASStringLength = mc.fontRendererObj.getStringWidth(attackSpeedText);

        TimeStringLength = mc.fontRendererObj.getStringWidth(sd.Date + " " + sd.Hour);

        ApecUtils.drawThiccBorderString(
                sd.Date + " " + sd.Hour,
                (int) ((sr.getScaledWidth() - 15 + delta_position.x + subComponentDeltas.get(4).getX()) * oneOverScale - mc.fontRendererObj.getStringWidth(sd.Date + " " + sd.Hour)),
                (int) ((GuiPos.y + delta_position.y + subComponentDeltas.get(4).getY()) * oneOverScale), 0xffffff
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
            add(new Vector2f((g_sr.getScaledWidth() - 20), 6*scale));
            add(new Vector2f(500*scale + 20 * scale, 6*scale));
            add(new Vector2f(540*scale + 20 * scale, 6*scale));
            add(new Vector2f(580*scale + 20 * scale, 6*scale));
            add(new Vector2f(620*scale + 20 * scale, 6*scale));
            add(new Vector2f(660*scale + 20 * scale, 6*scale));
        }};
    }

    @Override
    public List<Vector2f> getSubElementsBoundingPoints() {
        final boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        boolean inTheCatacombs = ApecMain.Instance.dataExtractor.isInTheCatacombs;
        final int zoneAddX = (inTheCatacombs ? 5 : 9);
        List<Vector2f> RelativeVectors = new ArrayList<Vector2f>(4) {{
            add(new Vector2f(PurseStringLength  + (UseIcons ? 9 : 0)*scale, 10*scale));
            add(new Vector2f( BitsLength + (UseIcons ? 9 : 0)*scale, 10*scale));
            add(new Vector2f(ZoneStringLength + (UseIcons ? zoneAddX : 0)*scale, 10*scale));
            add(new Vector2f(DefenceStringLength + (UseIcons ? 10 : 0)*scale, 10*scale));
            add(new Vector2f(-TimeStringLength-(getCurrentAnchorPoint().x)*scale, 10*scale));
            add(new Vector2f(SpeedStringLength, 10*scale));
            add(new Vector2f(StrengthStringLength, 10*scale));
            add(new Vector2f(CCStringLength, 10*scale));
            add(new Vector2f(CDStringLength, 10*scale));
            add(new Vector2f(ASStringLength, 10*scale));
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
        }
        return "";
    }
}
