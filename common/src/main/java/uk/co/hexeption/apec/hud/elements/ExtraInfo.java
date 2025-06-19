package uk.co.hexeption.apec.hud.elements;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.utils.ApecUtils;

public class ExtraInfo extends Element {
    public ExtraInfo() {
        super(ElementType.EXTRA_INFO);
    }

    @Override
    public void drawText(GuiGraphics graphics, boolean editMode) {
        Vector2f ExtraScoreInfo = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);

        ArrayList<Component> ei = new ArrayList<Component>();
        ei.addAll(Apec.SKYBLOCK_INFO.getScoreboard().extra());
        if (editMode && ei.isEmpty()) {
            for (int i = 0;i < 10;i++)
                ei.add(Component.literal("Something"));
        }

        if (!ei.isEmpty()) {
            for (int i = 0;i < ei.size();i++) {
                ApecUtils.drawOutlineText(mc, graphics, ei.get(i), (int)(ExtraScoreInfo.x), (int) (ExtraScoreInfo.y + i * 15), 0x0ffffff);
            }
        }


    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return this.menu.applyGlobalChanges(this, new Vector2f(5, 85));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(55 * scale, 100 * scale);
    }
}
