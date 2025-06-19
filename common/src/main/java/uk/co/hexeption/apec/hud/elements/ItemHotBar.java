package uk.co.hexeption.apec.hud.elements;

import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.ElementType;
import uk.co.hexeption.apec.settings.SettingID;

public class ItemHotBar extends Element {

    public ItemHotBar() {

        super(ElementType.ITEM_HOT_BAR);
    }

    @Override
    public void editInit() {

        this.scalable = !Apec.INSTANCE.settingsManager.getSettingState(SettingID.COMPATIBILITY_SAFETY);
    }

    @Override
    public Vector2f getAnchorPointPosition() {

        return this.menu.applyGlobalChanges(this, new Vector2f(mc.getWindow().getGuiScaledWidth() - 183, mc.getWindow().getGuiScaledHeight() - 43 + 20 * (1 - Apec.apecMenu.getGuiComponent(ElementType.BOTTOM_BAR).getScale())));
    }

    @Override
    public Vector2f getBoundingPoint() {

        return new Vector2f(182 * scale, 22 * scale);
    }

}
