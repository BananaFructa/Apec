package uk.co.hexeption.apec.settings.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.network.chat.Component;
import uk.co.hexeption.apec.settings.Setting;

public class SettingButton extends PlainTextButton {

    Setting setting;

    public SettingButton(int i, int j, int k, int l, Setting setting) {
        super(i, j, k, l, Component.literal(""), button -> {
            setting.Toggle();
        }, Minecraft.getInstance().font);

        this.setting = setting;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        if (this.isHovered) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x1adddddd);
        }
    }

}
