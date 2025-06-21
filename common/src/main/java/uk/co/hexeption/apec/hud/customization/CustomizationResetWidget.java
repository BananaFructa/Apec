package uk.co.hexeption.apec.hud.customization;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import uk.co.hexeption.apec.MC;

public class CustomizationResetWidget extends Button implements MC {

    public CustomizationResetWidget(int i, int j, int k, int l, Component component, OnPress onPress) {

        super(i, j, k, l, component, onPress, CustomizationWidget.DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {

        if (this.isHovered) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x1adddddd);
        } else {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x990a0a0a);
        }

        int k = this.active ? 16777215 : 10526880;
        this.renderString(guiGraphics, mc.font, k | Mth.ceil(this.alpha * 255.0F) << 24);
    }

}
