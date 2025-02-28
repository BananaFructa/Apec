package uk.co.hexeption.apec.hud.customization;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.hud.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomizationScreen extends Screen implements uk.co.hexeption.apec.MC {

    List<Element> elements = Apec.apecMenu.guiElements;
    List<Integer> xSnapPoints = new ArrayList<>();
    List<Integer> ySnapPoints = new ArrayList<>();

    public CustomizationScreen() {
        super(Component.nullToEmpty("Customization Screen"));
    }

    @Override
    protected void init() {
        Apec.apecMenu.isCustomizationScreenOpen = true;
        super.init();

        refreshSnapPoints();

        elements.forEach(element -> {
            Vector2f pos = element.getAnchorPointPosition();
            addRenderableWidget(new CustomizationWidget(element, xSnapPoints, ySnapPoints));
            if (element.isScalable()) {
                addRenderableWidget(new CustomizationScaleWidget(element, (int) pos.x + 7, (int) pos.y + 7));
            }

        });

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {

    }

    void refreshSnapPoints() {
        xSnapPoints.clear();
        ySnapPoints.clear();
        elements.forEach(element -> {
            Vector2f pos = element.getCurrentAnchorPoint();
            Vector2f b_pos = element.getCurrentBoundingPoint();

            xSnapPoints.add(width / 2);

            xSnapPoints.add((int) pos.x);
            ySnapPoints.add((int) pos.y);
            xSnapPoints.add((int) b_pos.x);
            ySnapPoints.add((int) b_pos.y);
            xSnapPoints.add((int) (b_pos.x / 2));
            ySnapPoints.add((int) (b_pos.y / 2));
        });
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        refreshSnapPoints();
        saveDeltas();
        return super.mouseReleased(d, e, i);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        refreshSnapPoints();

        if (i == 0) {
            this.children().forEach(guiEventListener -> {
                if (guiEventListener.mouseClicked(d, e, i) && guiEventListener instanceof CustomizationWidget)
                    ((CustomizationWidget) guiEventListener).userStartedDragging(d, e);
            });
            this.children().forEach(guiEventListener -> {
                if (guiEventListener.mouseClicked(d, e, i) && guiEventListener instanceof CustomizationScaleWidget)
                    ((CustomizationScaleWidget) guiEventListener).userStartedDragging(d, e);
            });
        }

        if (i == 1) {
            this.children().forEach(guiEventListener -> {
                if (guiEventListener.mouseClicked(d, e, i) && guiEventListener instanceof CustomizationWidget)
                    ((CustomizationWidget) guiEventListener).reset();
            });
            this.children().forEach(guiEventListener -> {
                if (guiEventListener.mouseClicked(d, e, i) && guiEventListener instanceof CustomizationScaleWidget)
                    ((CustomizationScaleWidget) guiEventListener).resetScale();
            });

        }

        return super.mouseClicked(d, e, i);
    }

    private void saveDeltas() {
        try {
            new File("config/Apec").mkdirs();
            new File("config/Apec/GuiDeltas.txt").createNewFile();
            FileWriter fw = new FileWriter("config/Apec/GuiDeltas.txt");
            String s = "";
            for (int i = 0;i < elements.size();i++) {
                s += elements.get(i).getType().ordinal() + "#" + elements.get(i).getDeltaPosition().x + "@" + elements.get(i).getDeltaPosition().y + "@" + elements.get(i).getScale();
                if (i != elements.size() - 1) s += "\n";
            }
            fw.write(s);
            fw.close();
        } catch (IOException e) {
            //ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error saving GUI Deltas!");
        }
    }

}