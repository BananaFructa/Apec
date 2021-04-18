package Apec.Components.Gui.Menu.TexturePackMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class TPTaggedDisplayElement implements ITPDrawableElement {

    private TPDisplayElement[] elements;
    private TPDisplayElement currentElement;
    private TPRVTabButton[] tabButtons;

    public TPTaggedDisplayElement(TPDisplayElement[] elements, TPRVTabButton[] tabButtons) {
        this.elements = elements;
        for (TPDisplayElement element :elements) {
            element.removeUpperBorderGlow();
        }
        this.tabButtons = tabButtons;
        for (TPRVTabButton button : tabButtons) {
            button.setParent(this);
            if (button.id == 0) {
                button.selected = true;
            }
        }
        setCurrent(0);
    }

    public void setCurrent(int i) {
        if (i == -1) return;
        elements[i].show();
        currentElement = elements[i];
        for (int j = 0;j < elements.length;j++) {
            if (i != j) {
                elements[j].hide();
            }
        }
        for (TPRVTabButton tabButton : tabButtons) {
            if (tabButton.id != i) {
                tabButton.selected = false;
            }
        }
    }

    @Override
    public int draw(int y, int mouseX, int mouseY, ScaledResolution sr, Minecraft mc) {
        int width = sr.getScaledWidth()/2;
        int cornerXLeft = width - currentElement.elementLength / 2;
        int cornerXRight = width + currentElement.elementLength / 2;
        int xOffset = 5;
        TPRVTabButton selected = null;
        for (TPRVTabButton tabButton : tabButtons) {
            tabButton.xPosition = xOffset + cornerXLeft;
            tabButton.yPosition = y;
            tabButton.drawButton(mc,mouseX,mouseY);
            xOffset += tabButton.width;
            if (tabButton.selected) {
                selected = tabButton;
            }
        }
        if (selected != null) {
            currentElement.parent.drawRect(cornerXLeft, y + 9, selected.xPosition, y + 10, 0xffffffff);
            currentElement.parent.drawRect(selected.xPosition + selected.width, y + 9, cornerXRight, y + 10, 0xffffffff);
        }
        if (currentElement != null) {
            return currentElement.draw(y + 10,mouseX,mouseY,sr,mc) + 10;
        }
        return 0;
    }

    @Override
    public int getOffset() {
        if (currentElement != null) {
            return currentElement.getOffset() + 10;
        }
        return 0;
    }
}
