package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataExtractor;
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

    // TODO: Maybe create an enum for subcomponents

    float yDecremetor = 0;

    public InfoBox() {
        super(GUIComponentID.INFO_BOX,5);
    }

    public int PurseStringLength = 0,BitsLength = 0,ZoneStringLength = 0,DefenceStringLength = 0,TimeStringLength = 0;

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, sr, editingMode);
        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/bottomBar.png"));
        int DrawCounts = (int)(sr.getScaledWidth()/256) + 1;
        for (int i = 0;i < DrawCounts;i++) {
            gi.drawTexturedModalRect((int) delta_position.x + i * 256, sr.getScaledHeight() - 20*scale + (int) delta_position.y + (int)yDecremetor,0,0,256,(int)(20.0f));
            if (mc.gameSettings.guiScale == 1) {
                gi.drawTexturedModalRect((int) delta_position.x + i * 256, sr.getScaledHeight() - 7*scale + (int) delta_position.y + (int)yDecremetor,0,0,256,(int)(20.0f));
            }
        }
        GlStateManager.scale(scale,scale,1);
        if (UseIcons) {
            Vector2f GuiPos = getRealAnchorPoint();

            GuiPos.y += yDecremetor;
            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));
            gi.drawTexturedModalRect((int)(GuiPos.x + (subComponentDeltas.get(0).getX())/scale), (int)((GuiPos.y+ subComponentDeltas.get(0).getY())/scale -1),250,84,6,9);
            gi.drawTexturedModalRect((int)(GuiPos.x + (subComponentDeltas.get(1).getX())/scale + 120), (int)((GuiPos.y+ subComponentDeltas.get(1).getY())/scale -1),245,84,5,9);
            if (ApecMain.Instance.dataExtractor.isInTheCatacombs) {
                gi.drawTexturedModalRect((int) (GuiPos.x + (subComponentDeltas.get(2).getX())/scale + 220 - 1), (int) ((GuiPos.y + subComponentDeltas.get(2).getY())/scale -1), 229, 84, 7, 9);
            } else {
                gi.drawTexturedModalRect((int) (GuiPos.x + (subComponentDeltas.get(2).getX())/scale + 220), (int) ((GuiPos.y + subComponentDeltas.get(2).getY())/scale -1 ), 236, 84, 9, 9);
            }
            if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) || editingMode) {
                gi.drawTexturedModalRect((int) (GuiPos.x + (subComponentDeltas.get(3).getX()) / scale + 360), (int) ((GuiPos.y + subComponentDeltas.get(3).getY()) / scale - 1), 222, 84, 7, 10);
            }
        }
        GlStateManager.scale(1,1,1);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_AUTO_SCALING_BB)) {
            if (mc.gameSettings.guiScale == 0) {
                this.scale = 0.72f;
            } else if (mc.gameSettings.guiScale == 3) {
                this.scale = 0.8f;
            } else if (mc.gameSettings.guiScale == 2) {
                this.scale = 1f;
            } else if (mc.gameSettings.guiScale == 1) {
                this.scale = 1.5f;
            }
        } else {
            scale = 1;
        }
        GlStateManager.scale(scale, scale, 1);
        super.draw(ps, sd, od, sr, editingMode);
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
        Vector2f GuiPos = getRealAnchorPoint();

        GuiPos.y += yDecremetor;

        boolean UseIcons = ApecMain.Instance.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);

        String purseText = (UseIcons ? RemovePurseText(sd.Purse) : sd.Purse);
        String zoneText = (UseIcons ? ApecUtils.RemoveCharSequence("\u23E3", sd.Zone) : sd.Zone);
        String defenceText = (UseIcons ? "\u00a7a" + ps.Defence : "\u00a7a" + ps.Defence + " Defence");
        String bitText = (UseIcons ? ApecUtils.RemoveCharSequence("Bits: ",sd.Bits) : sd.Bits);
        boolean inTheCatacombs = ApecMain.Instance.dataExtractor.isInTheCatacombs;
        mc.fontRendererObj.drawString(
                purseText,
                (int) (GuiPos.x + (subComponentDeltas.get(0).getX() + (UseIcons ? 9 : 0)) / scale),
                (int) ((GuiPos.y + subComponentDeltas.get(0).getY()) / scale),
                0xffffff, false
        );
        mc.fontRendererObj.drawString(
                bitText,
                (int) (GuiPos.x + (subComponentDeltas.get(1).getX() + (UseIcons ? 9 : 0)) / scale + 120),
                (int) ((GuiPos.y + subComponentDeltas.get(1).getY()) / scale),
                0xffffff, false
        );
        int zoneAddX = (inTheCatacombs ? 5 : 9) ;
        mc.fontRendererObj.drawString(
                zoneText,
                (int) (GuiPos.x + (subComponentDeltas.get(2).getX() + (UseIcons ? zoneAddX : 0)) / scale + 220),
                (int) ((GuiPos.y + subComponentDeltas.get(2).getY()) / scale),
                0xffffff, false
        );

        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) || editingMode) {
            mc.fontRendererObj.drawString(
                    defenceText,
                    (int) (GuiPos.x + (subComponentDeltas.get(3).getX() + (UseIcons ? 10 : 0)) / scale + 360),
                    (int) ((GuiPos.y + subComponentDeltas.get(3).getY()) / scale),
                    0xffffff
            );
        }

        PurseStringLength = mc.fontRendererObj.getStringWidth(purseText);
        BitsLength = mc.fontRendererObj.getStringWidth(bitText);
        ZoneStringLength = mc.fontRendererObj.getStringWidth(zoneText);
        DefenceStringLength = mc.fontRendererObj.getStringWidth(defenceText);
        TimeStringLength = mc.fontRendererObj.getStringWidth(sd.Date + " " + sd.Hour);

        mc.fontRendererObj.drawString(
                sd.Date + " " + sd.Hour,
                (int) ((sr.getScaledWidth() - 15 + delta_position.x + subComponentDeltas.get(4).getX()) / scale - mc.fontRendererObj.getStringWidth(sd.Date + " " + sd.Hour)),
                (int) ((GuiPos.y + delta_position.y + subComponentDeltas.get(4).getY()) / scale), 0xffffff, false
        );
        GlStateManager.scale(1, 1, 1);
        GlStateManager.popMatrix();

    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(20, g_sr.getScaledHeight() - 14*scale);
    }

    @Override
    public List<Vector2f> getSubElementsAnchorPoints() {
        return new ArrayList<Vector2f>() {{
            add(new Vector2f(0, 0));
            add(new Vector2f(120*scale, 0));
            add(new Vector2f(220*scale, 0));
            add(new Vector2f(360*scale, 0));
            add(new Vector2f((g_sr.getScaledWidth() - 20), 0));
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
            add(new Vector2f(-TimeStringLength-(getRealAnchorPoint().x)*scale, 10*scale));
            // Since the x is relative to the side of the screen and not the parent's x position i removed it's relativity
            // I can do that since the bottom bar cannot be moved so no wack shit is going to happen
        }};
        return ApecUtils.AddVecListToList(RelativeVectors,getSubElementsRealAnchorPoints());
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
