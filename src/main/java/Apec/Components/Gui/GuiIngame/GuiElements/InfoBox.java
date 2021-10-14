package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.Utils.ApecUtils.Icon;
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
        super(GUIComponentID.INFO_BOX,13);
    }

    public int  PurseStringWidth = 0, BitsLength = 0, ZoneStringWidth = 0,
                DefenceStringWidth = 0, TimeStringWidth = 0, SpeedStringWidth = 0,
                StrengthStringWidth = 0, CritChanceStringWidth = 0, CritDamageStringWidth = 0,
                AttackSpeedStringWidth = 0, GemstonePowderStringWidth = 0, MithrilPowderStringWidth = 0,
                SoulflowStringWidth = 0;

    public final Icon defenceIcon = new Icon("gui/statBars.png", 32, 215, 7, 10);
    public final Icon speedIcon = new Icon("gui/statBars.png", 40, 216, 9, 9);
    public final Icon strengthIcon  = new Icon("gui/statBars.png", 50, 216, 9, 9);
    public final Icon critChanceIcon = new Icon("gui/statBars.png", 60, 216, 9, 9);
    public final Icon critDamageIcon = new Icon("gui/statBars.png", 70, 216, 9, 9);
    public final Icon attackSpeedIcon = new Icon("gui/statBars.png", 80, 216, 9, 9);
    public final Icon mithrilPowderIcon = new Icon("gui/statBars.png", 90, 216, 9, 9);
    public final Icon gemstonePowderIcon = new Icon("gui/statBars.png", 100, 216, 9, 9);
    public final Icon soulflowIcon = new Icon("gui/statBars.png", 110, 216, 9, 9);

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
        String speedText = (UseIcons ? ts.Speed : "\u2726" + ts.Speed);
        String strengthText = (UseIcons ? ts.Strength : "\u2741" + ts.Strength);
        String critChanceText = (UseIcons ? ts.CritChance : "\u2623" + ts.CritChance);
        String critDamageText = (UseIcons ? ts.CritDamage : "\u2620" + ts.CritDamage);
        String attackSpeedText = (UseIcons ? ts.AttackSpeed : "\u2694" + ts.AttackSpeed);
        String gemstonePowderText = (UseIcons ? ts.GemstonePowder : "\u1805" + ts.GemstonePowder);
        String mithrilPowderText = (UseIcons ? ts.MithrilPowder : "\u1805" + ts.MithrilPowder);
        String soulflowText = (UseIcons ? ps.Soulflow + "" : ps.Soulflow + "\u2e0e");

        boolean inTheCatacombs = ApecMain.Instance.dataExtractor.isInTheCatacombs;
        ApecUtils.drawStylizedString(
                purseText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(0).getX() + (UseIcons ? 9 : 0)) * oneOverScale),
                (int) ((GuiPos.y + subComponentDeltas.get(0).getY()) * oneOverScale),
                0xffffff
        );
        ApecUtils.drawStylizedString(
                bitText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(1).getX() + (UseIcons ? 9 : 0)) * oneOverScale + 120),
                (int) ((GuiPos.y + subComponentDeltas.get(1).getY()) * oneOverScale),
                0xffffff
        );
        int zoneAddX = (inTheCatacombs ? 5 : 9) ;
        ApecUtils.drawStylizedString(
                zoneText,
                (int) (GuiPos.x + 20 + (subComponentDeltas.get(2).getX() + (UseIcons ? zoneAddX : 0)) * oneOverScale + 220),
                (int) ((GuiPos.y + subComponentDeltas.get(2).getY()) * oneOverScale),
                0xffffff
        );

        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB)) {
            ApecUtils.drawStylizedStringWithIcon(
                    defenceText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(3).getX()) * oneOverScale + 360),
                    (int) ((GuiPos.y + subComponentDeltas.get(3).getY()) * oneOverScale),
                    0xffffff,
                    defenceIcon,
                    UseIcons
            );
        }

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SPEED)) {
            ApecUtils.drawStylizedStringWithIcon(
                    speedText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(5).getX()) * oneOverScale + 400),
                    (int) ((GuiPos.y + subComponentDeltas.get(5).getY()) * oneOverScale),
                    0xffffff,
                    speedIcon,
                    UseIcons
            );
        }
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_STRENGTH)) {
            ApecUtils.drawStylizedStringWithIcon(
                    strengthText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(6).getX()) * oneOverScale + 440),
                    (int) ((GuiPos.y + subComponentDeltas.get(6).getY()) * oneOverScale),
                    0xFF5555,
                    strengthIcon,
                    UseIcons
            );
        }

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CRIT_CHANCE)) {
            ApecUtils.drawStylizedStringWithIcon(
                    critChanceText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(7).getX()) * oneOverScale + 480),
                    (int) ((GuiPos.y + subComponentDeltas.get(7).getY()) * oneOverScale),
                    0x5555FF,
                    critChanceIcon,
                    UseIcons
            );
        }

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CRIT_DAMAGE)) {
            ApecUtils.drawStylizedStringWithIcon(
                    critDamageText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(8).getX()) * oneOverScale + 520),
                    (int) ((GuiPos.y + subComponentDeltas.get(8).getY()) * oneOverScale),
                    0x5555FF,
                    critDamageIcon,
                    UseIcons
            );
        }

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_ATTACK_SPEED)) {
            ApecUtils.drawStylizedStringWithIcon(
                    attackSpeedText,
                    (int) (GuiPos.x + 20 + (subComponentDeltas.get(9).getX()) * oneOverScale + 560),
                    (int) ((GuiPos.y + subComponentDeltas.get(9).getY()) * oneOverScale),
                    0xFFFF55,
                    attackSpeedIcon,
                    UseIcons
            );
        }

        if(!ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_POWDER_DISPLAY)){
            if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_MITHRIL_POWDER)) {
                ApecUtils.drawStylizedStringWithIcon(
                        mithrilPowderText,
                        (int) (GuiPos.x + 20 + (subComponentDeltas.get(10).getX()) * oneOverScale + 600),
                        (int) ((GuiPos.y + subComponentDeltas.get(10).getY()) * oneOverScale),
                        0x00AA00,
                        mithrilPowderIcon,
                        UseIcons
                );
            }
    
            if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_GEMSTONE_POWDER)) {
                ApecUtils.drawStylizedStringWithIcon(
                        gemstonePowderText,
                        (int) (GuiPos.x + 20 + (subComponentDeltas.get(11).getX()) * oneOverScale + 660),
                        (int) ((GuiPos.y + subComponentDeltas.get(11).getY()) * oneOverScale),
                        0xFF55FF,
                        gemstonePowderIcon,
                        UseIcons
                );
            }
        }
        if(!ApecMain.Instance.settingsManager.getSettingState(SettingID.SEPARATE_SOULFLOW_DISPLAY)){
            if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_SOULFLOW)) {
                ApecUtils.drawStylizedStringWithIcon(
                        soulflowText,
                        (int) (GuiPos.x + 20 + (subComponentDeltas.get(12).getX()) * oneOverScale + 700),
                        (int) ((GuiPos.y + subComponentDeltas.get(12).getY()) * oneOverScale),
                        0x00AAAA,
                        soulflowIcon,
                        UseIcons,
                        true
                );
            }
        }

        PurseStringWidth = mc.fontRendererObj.getStringWidth(purseText);
        BitsLength = mc.fontRendererObj.getStringWidth(bitText);
        ZoneStringWidth = mc.fontRendererObj.getStringWidth(zoneText);
        DefenceStringWidth = mc.fontRendererObj.getStringWidth(defenceText);
        SpeedStringWidth = mc.fontRendererObj.getStringWidth(speedText);
        StrengthStringWidth = mc.fontRendererObj.getStringWidth(strengthText);
        CritChanceStringWidth = mc.fontRendererObj.getStringWidth(critChanceText);
        CritDamageStringWidth = mc.fontRendererObj.getStringWidth(critDamageText);
        AttackSpeedStringWidth = mc.fontRendererObj.getStringWidth(attackSpeedText);
        GemstonePowderStringWidth = mc.fontRendererObj.getStringWidth(gemstonePowderText);
        MithrilPowderStringWidth = mc.fontRendererObj.getStringWidth(mithrilPowderText);
        SoulflowStringWidth = mc.fontRendererObj.getStringWidth(soulflowText);

        TimeStringWidth = mc.fontRendererObj.getStringWidth(sd.Date + " " + sd.Hour);

        ApecUtils.drawStylizedString(
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
            add(new Vector2f(400*scale + 20 * scale, 6*scale));
            add(new Vector2f(440*scale + 20 * scale, 6*scale));
            add(new Vector2f(480*scale + 20 * scale, 6*scale));
            add(new Vector2f(520*scale + 20 * scale, 6*scale));
            add(new Vector2f(560*scale + 20 * scale, 6*scale));
            add(new Vector2f(600*scale + 20 * scale, 6*scale));
            add(new Vector2f(660*scale + 20 * scale, 6*scale));
            add(new Vector2f(700*scale + 20 * scale, 6*scale));
        }};
    }

    @Override
    public List<Vector2f> getSubElementsBoundingPoints() {
        final boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        boolean inTheCatacombs = ApecMain.Instance.dataExtractor.isInTheCatacombs;
        final int zoneAddX = (inTheCatacombs ? 5 : 9);
        List<Vector2f> RelativeVectors = new ArrayList<Vector2f>(13) {{
            add(new Vector2f(PurseStringWidth  + (UseIcons ? 9 : 0)*scale, 10*scale));
            add(new Vector2f(BitsLength + (UseIcons ? 9 : 0)*scale, 10*scale));
            add(new Vector2f(ZoneStringWidth + (UseIcons ? zoneAddX : 0)*scale, 10*scale));
            add(new Vector2f(DefenceStringWidth + (UseIcons ? 10 : 0)*scale, 10*scale));
            add(new Vector2f(-TimeStringWidth-(getCurrentAnchorPoint().x)*scale, 10*scale));
            add(new Vector2f(SpeedStringWidth + (UseIcons ? 11 : 0), 10*scale));
            add(new Vector2f(StrengthStringWidth + (UseIcons ? 11 : 0), 10*scale));
            add(new Vector2f(CritChanceStringWidth + (UseIcons ? 11 : 0), 10*scale));
            add(new Vector2f(CritDamageStringWidth + (UseIcons ? 11 : 0), 10*scale));
            add(new Vector2f(AttackSpeedStringWidth + (UseIcons ? 11 : 0), 10*scale));
            add(new Vector2f(MithrilPowderStringWidth + (UseIcons ? 11 : 0), 10*scale));
            add(new Vector2f(GemstonePowderStringWidth + (UseIcons ? 11 : 0), 10*scale));
            add(new Vector2f(SoulflowStringWidth + (UseIcons ? 11 : 0), 10*scale));
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
