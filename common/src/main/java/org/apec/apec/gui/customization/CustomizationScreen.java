package org.apec.apec.gui.customization;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apec.apec.Apec;
import org.apec.apec.MC;
import org.apec.apec.gui.Element;
import org.apec.apec.module.modules.ApecMenu;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CustomizationScreen extends Screen implements MC {

    List<Element> elements = ((ApecMenu) Apec.getInstance().getModuleManager().getModuleByClass(ApecMenu.class)).guiElements;
    List<Integer> xSnapPoints = new ArrayList<>();
    List<Integer> ySnapPoints = new ArrayList<>();

    public CustomizationScreen() {
        super(Component.nullToEmpty("Customization Screen"));
    }

    @Override
    protected void init() {
        ((ApecMenu) Apec.getInstance().getModuleManager().getModuleByClass(ApecMenu.class)).isCustomizationScreenOpen = true;
        super.init();

        refreshSnapPoints();

        elements.forEach(element -> {
            Vector2f pos = element.getAnchorPointPosition();
            addRenderableWidget(new CustomizationWidget(element, xSnapPoints, ySnapPoints));
        });

    }

    void refreshSnapPoints() {
        xSnapPoints.clear();
        ySnapPoints.clear();
        Window window = mc.getWindow();
        int scaledWidth = window.getGuiScaledWidth();
        int scaledHeight = window.getGuiScaledHeight();
        elements.forEach(element -> {
            Vector2f pos = element.getCurrentAnchorPoint();
            Vector2f b_pos = element.getCurrentBoundingPoint();

            xSnapPoints.add(scaledWidth / 2);

            xSnapPoints.add((int) pos.x);
            ySnapPoints.add((int) pos.y);
            xSnapPoints.add((int) b_pos.x);
            ySnapPoints.add((int) b_pos.y);
            xSnapPoints.add((int) (b_pos.x / 2));
            ySnapPoints.add((int) (b_pos.y / 2));
        });
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {

        if (i == 0) {
            this.children().forEach(guiEventListener -> {
                if (guiEventListener.mouseClicked(d, e, i) && guiEventListener instanceof CustomizationWidget)
                    ((CustomizationWidget) guiEventListener).userStartedDragging(d, e);
            });
        }

        return super.mouseClicked(d, e, i);
    }

}
