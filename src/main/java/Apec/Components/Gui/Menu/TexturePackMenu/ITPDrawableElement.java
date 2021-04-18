package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public interface ITPDrawableElement {

    public int draw(int y, int mouseX, int mouseY, ScaledResolution sr, Minecraft mc);
    public int getOffset();

}
