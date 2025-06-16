package uk.co.hexeption.apec.hud.elements;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.api.SBAPI;
import uk.co.hexeption.apec.hud.ApecTextures;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.settings.SettingID;
import uk.co.hexeption.apec.utils.ApecUtils;

public class BottomBar extends Element {

    public BottomBar() {

        super(ElementType.BOTTOM_BAR, 7);
    }

    public int PurseStringLength = 0, BitsLength = 0, ZoneStringLength = 0, DefenceStringLength = 0, TimeStringLength = 0, ModeStringLength = 0, KuudraStringLength = 0;
    float yDecremetor = 0;

    @Override
    public void drawText(GuiGraphics graphics, boolean editMode) {

        Vector2f GuiPos = getCurrentAnchorPoint();

        boolean isInChat = Minecraft.getInstance().screen instanceof ChatScreen;
        float fps = Minecraft.getInstance().getFps();
        if (Apec.INSTANCE.settingsManager.getSettingState(SettingID.INFO_BOX_ANIMATION) && !Apec.INSTANCE.settingsManager.getSettingState(SettingID.BB_ON_TOP)) {
            // Calculating delta time for constant smooth velocity
            if (isInChat && yDecremetor < 40) yDecremetor += 1 * (120f / fps);
            if (!isInChat && yDecremetor > 0) yDecremetor -= 1 * (120f / fps);
        } else {
            yDecremetor = 0;
        }
        if (yDecremetor < 0) yDecremetor = 0;
        if (yDecremetor > 40) yDecremetor = 40;

        GuiPos.y += yDecremetor + 6 * scale;

        ApecTextures bottomBarTexture = ApecTextures.BOTTOM_BAR;
        int drawCount = (int) (mc.getWindow().getGuiScaledWidth() / 256) + 1;
        for (int i = 0; i < drawCount; i++) {
            graphics.blit(RenderType::guiTextured, bottomBarTexture.getResourceLocation(), (int) (GuiPos.x + i * 256), (int) (GuiPos.y + yDecremetor - (!Apec.INSTANCE.settingsManager.getSettingState(SettingID.BB_ON_TOP) ? 5 : 7)), 0, 0, 256, 20, bottomBarTexture.getWidth(), bottomBarTexture.getHeight());
        }

        SBAPI.PlayerStats ps = Apec.SKYBLOCK_INFO.getPlayerStats();
        SBAPI.SBScoreBoard sd = Apec.SKYBLOCK_INFO.getScoreboard();

        boolean UseIcons = Apec.INSTANCE.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);

        Component purseText = (UseIcons ? RemovePurseText(sd.purse()) : sd.purse());
        Component zoneText = (UseIcons ? RemoveZoneText(sd.zone()) : sd.zone());
        String defenceText = (UseIcons ? "§a" + ps.defense() : "§a❈ Defense: " + ps.defense());
        Component bitText = (UseIcons ? ApecUtils.removeComponentContaining(sd.bits(), "Bits: ") : sd.bits());
        Component modeText = (UseIcons ? ApecUtils.removeComponentContaining(sd.gameType(),"Mode: ") : sd.gameType());

//        String kuudraText = ChatFormatting.GOLD + ps.KuudraTieredBonus;
        boolean inTheCatacombs = false;

        graphics.drawString(
                mc.font,
                purseText,
                (int) (GuiPos.x + 20 + (subElementDeltaPositions.get(0).x() + (UseIcons ? 9 : 0)) * (1 / scale)),
                (int) ((GuiPos.y + subElementDeltaPositions.get(0).y()) * (1 / scale)),
                0xffffff, false
        );
        graphics.drawString(
                mc.font,
                bitText,
                (int) (GuiPos.x + 20 + (subElementDeltaPositions.get(1).x() + (UseIcons ? 9 : 0)) * (1 / scale) + 120),
                (int) ((GuiPos.y + subElementDeltaPositions.get(1).y()) * (1 / scale)),
                0xffffff, false
        );
        int zoneAddX = (inTheCatacombs ? 5 : 9);
        graphics.drawString(
                mc.font,
                zoneText,
                (int) (GuiPos.x + 20 + (subElementDeltaPositions.get(2).x() + (UseIcons ? zoneAddX : 0)) * (1 / scale) + 220),
                (int) ((GuiPos.y + subElementDeltaPositions.get(2).y()) * (1 / scale)),
                0xffffff, false
        );

        if (!Apec.INSTANCE.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) || editMode) {
            graphics.drawString(
                    mc.font,
                    defenceText,
                    (int) (GuiPos.x + 20 + (subElementDeltaPositions.get(3).x() + (UseIcons ? 10 : 0)) * (1 / scale) + 360),
                    (int) ((GuiPos.y + subElementDeltaPositions.get(3).y()) * (1 / scale)),
                    0xffffff
            );
        }

        if (!Apec.INSTANCE.settingsManager.getSettingState(SettingID.USE_GAME_MODE_OUT_OF_BB) || editMode) {
            graphics.drawString(
                    mc.font,
                    modeText,
                    (int) ((GuiPos.x + deltaPosition.x + subElementDeltaPositions.get(4).x()) * (1 / scale) + (Apec.INSTANCE.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) ? 360 : 460)),
                    (int) ((GuiPos.y + deltaPosition.y + subElementDeltaPositions.get(4).y()) * (1 / scale)),
                    0xffffff, false
            );
        }

//        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_KUUDRA_SET_BONUS_OUT_OF_BB) || editingMode) {
//            boolean showGameMode = modeText != "";
//            int xWithDefenceAndGameMode = (ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB) ? 360 : 460) + (showGameMode ? (ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_GAME_MODE_OUT_OF_BB) ? 0 : 100) : 0);
//            mc.fontRendererObj.drawString(
//                    kuudraText,
//                    (int) ((GuiPos.x + delta_position.x + subComponentDeltas.get(5).getX()) * oneOverScale + xWithDefenceAndGameMode),
//                    (int) ((GuiPos.y + delta_position.y + subComponentDeltas.get(5).getY()) * oneOverScale),
//                    0xffffff, false
//            );
//        }

        PurseStringLength = mc.font.width(purseText);
        BitsLength = mc.font.width(bitText);
        ZoneStringLength = mc.font.width(zoneText);
        DefenceStringLength = mc.font.width(defenceText);
        TimeStringLength = mc.font.width(sd.date() + " " + sd.Hour());
        ModeStringLength = mc.font.width(modeText);
//        KuudraStringLength = mc.font.width((kuudraText);

        graphics.drawString(
                mc.font,
                sd.date() + " " + sd.Hour(),
                (int) ((mc.getWindow().getGuiScaledWidth() - 15 + deltaPosition.x + subElementDeltaPositions.get(4).x()) * (1f / scale) - mc.font.width(sd.date() + " " + sd.Hour())),
                (int) ((GuiPos.y + deltaPosition.y + subElementDeltaPositions.get(4).y()) * (1f / scale)), 0xffffff, false
        );
    }

    @Override
    public Vector2f getAnchorPointPosition() {

        float y = 0;

        // check setting for bottom bar position
        if (!Apec.INSTANCE.settingsManager.getSettingState(SettingID.BB_ON_TOP)) {
            y = mc.getWindow().getGuiScaledHeight() - 20 * scale;
        }

        return new Vector2f(0, y);
    }

    @Override
    public List<Vector2f> getSubElementsAnchorPoints() {

        return new ArrayList<Vector2f>() {{
            add(new Vector2f(0 + 20 * scale, 6 * scale));
            add(new Vector2f(120 * scale + 20 * scale, 6 * scale));
            add(new Vector2f(220 * scale + 20 * scale, 6 * scale));
            add(new Vector2f(360 * scale + 20 * scale, 6 * scale));
            add(new Vector2f(460 * scale + 20 * scale, 6 * scale));
            add(new Vector2f(600 * scale + 20 * scale, 6 * scale));
            add(new Vector2f((mc.getWindow().getGuiScaledWidth() - 20), 6 * scale));
        }};
    }

    @Override
    public List<Vector2f> getSubElementsBoundingPoints() {

        final boolean UseIcons = Apec.INSTANCE.settingsManager.getSettingState(SettingID.INFO_BOX_ICONS);
        boolean inTheCatacombs = false;
        final int zoneAddX = (inTheCatacombs ? 5 : 9);
        List<Vector2f> rel = new ArrayList<Vector2f>() {{
            add(new Vector2f(PurseStringLength + (UseIcons ? 9 : 0) * scale, 10 * scale));
            add(new Vector2f(BitsLength + (UseIcons ? 9 : 0) * scale, 10 * scale));
            add(new Vector2f(ZoneStringLength + (UseIcons ? zoneAddX : 0) * scale, 10 * scale));
            add(new Vector2f(DefenceStringLength + (UseIcons ? 10 : 0) * scale, 10 * scale));
            add(new Vector2f(ModeStringLength + (getCurrentAnchorPoint().x) * scale, 10 * scale));
            add(new Vector2f(KuudraStringLength + (getCurrentAnchorPoint().x) * scale, 10 * scale));
            add(new Vector2f(-TimeStringLength - (getCurrentAnchorPoint().x) * scale, 10 * scale));
        }};
        return ApecUtils.addVecListToList(rel, getSubElementsCurrentAnchorPoints());
    }

    private Component RemovePurseText(Component s) {

        if (ApecUtils.containedByCharSequence(s, "Purse: ")) {
            return ApecUtils.removeComponentContaining(s, "Purse: ");
        } else if (ApecUtils.containedByCharSequence(s, "Piggy: ")) {
            return ApecUtils.removeComponentContaining(s, "Piggy: ");
        } else if (ApecUtils.containedByCharSequence(s, "Motes: ")) {
            return ApecUtils.removeComponentContaining(s, "Motes: ");
        }
        return Component.empty();
    }

    public Component RemoveZoneText(Component s) {

        if (ApecUtils.containedByCharSequence(s, "\u23E3")) {
            return ApecUtils.removeComponentContaining(s, "\u23E3");
        } else if (ApecUtils.containedByCharSequence(s, "\u0444")) {
            return ApecUtils.removeComponentContaining(s, "\u0444");
        }
        return s;
    }

}
