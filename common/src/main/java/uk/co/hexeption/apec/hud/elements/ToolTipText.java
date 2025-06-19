package uk.co.hexeption.apec.hud.elements;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.hud.ApecMenu;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.settings.SettingID;
import uk.co.hexeption.apec.utils.ApecUtils;

public class ToolTipText extends Element {

    private ItemHotBar itemHotBar;

    public ToolTipText() {

        super(ElementType.TOOL_TIP_TEXT);
        this.scalable = false;
    }

    @Override
    public void init(ApecMenu menu) {

        super.init(menu);
        this.itemHotBar = (ItemHotBar) menu.getGuiComponent(ElementType.ITEM_HOT_BAR);
    }

    @Override
    public Vector2f getAnchorPointPosition() {

        float x = 0f;
        float y = 0f;
        if (!Apec.INSTANCE.settingsManager.getSettingState(SettingID.ITEM_HIGHLIGHT_TEXT)) {
            Vector2f posH = ApecUtils.scalarMultiply(itemHotBar.getCurrentAnchorPoint(), 1f / itemHotBar.getScale());
            x = (int) (posH.x) + 1;
            y = (int) (posH.y) - 10;
        } else {
            x = (-mc.font.width("hello") * 0.5f) - 92 + mc.getWindow().getGuiScaledWidth() * 1f / itemHotBar.getScale();
            y = mc.getWindow().getGuiScaledHeight() * 1f / scale - 67 * 1f / itemHotBar.getScale();
            x += itemHotBar.getDeltaPosition().x * itemHotBar.getScale();
            y += itemHotBar.getDeltaPosition().y * 1f / itemHotBar.getScale();
        }
        return new Vector2f(x, y);
    }

    @Override
    public Vector2f getBoundingPoint() {

        float x = 0f;
        float y = 0f;
        x = mc.font.width("hello");
        y = mc.font.lineHeight * itemHotBar.getScale();
        return new Vector2f(x, y);
    }

    public int getXOffset(GuiGraphics guiGraphics) {
        var deltaH = itemHotBar.getDeltaPosition();
        var scaleH = itemHotBar.getScale();

        var x = guiGraphics.guiWidth() + deltaH.x / scaleH - 185;
        return (int) x;
    }

    public int getYOffset(GuiGraphics guiGraphics) {

        var deltaH = itemHotBar.getDeltaPosition();
        var scaleH = itemHotBar.getScale();

        var y = guiGraphics.guiHeight() + deltaH.y / scaleH - 56;
        return (int) y;
    }

}
