package uk.co.hexeption.apec.settings.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.network.chat.Component;

public class SettingNavigationButton extends PlainTextButton {

    NavigationAction action;

    public static String getStringFromAction(NavigationAction action) {
        switch (action) {
            case BACK:
                return "<";
            case NEXT:
                return ">";
            case OPEN_GUI_EDITING:
                return "Customize Gui";
            case SEARCH:
                return "Open Search Bar";
            default:
                return "N/A";
        }
    }

    public SettingNavigationButton(int i, int j, int k, int l, OnPress onPress, Font font, NavigationAction action) {
        super(i, j, k, l, Component.literal(getStringFromAction(action)), onPress, font);

        this.action = action;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        if (this.isHovered) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x1adddddd);
        } else {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x99151515);
        }

        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 14737632);
    }

    public void runAction() {
        ((SettingsMenu) Minecraft.getInstance().screen).runAction(this.action);
    }

}
