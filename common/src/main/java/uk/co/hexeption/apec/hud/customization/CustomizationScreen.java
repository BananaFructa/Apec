package uk.co.hexeption.apec.hud.customization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.hud.Element;
import uk.co.hexeption.apec.hud.elements.BottomBar;

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
            if (!(element instanceof BottomBar)) {
                Vector2f pos = element.getAnchorPointPosition();
                addRenderableWidget(new CustomizationWidget(element, xSnapPoints, ySnapPoints));
                if (element.isScalable()) {
                    addRenderableWidget(new CustomizationScaleWidget(element, (int) pos.x + 7, (int) pos.y + 7));
                }
            }
            if (element.hasSubComponents()) {
                if (element instanceof BottomBar) {
                    for (int i = 0; i < element.subComponentCount(); i++) {
                        addRenderableWidget(new CustomizationWidget(element, i, xSnapPoints, ySnapPoints, true));
                    }
                } else {
                    for (int i = 0; i < element.subComponentCount(); i++) {
                        addRenderableWidget(new CustomizationWidget(element, i, xSnapPoints, ySnapPoints));
                    }
                }
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
            File file = new File("config/Apec/GuiDeltas.json");
            file.createNewFile();
            JsonArray jsonArray = new JsonArray();
            for (Element element : elements) {
                JsonObject obj = new JsonObject();
                obj.addProperty("type", element.getType().name());
                obj.addProperty("deltaX", element.getDeltaPosition().x);
                obj.addProperty("deltaY", element.getDeltaPosition().y);
                obj.addProperty("scale", element.getScale());
                if (element.hasSubComponents()) {
                    JsonArray subArray = new JsonArray();
                    for (int i = 0; i < element.subComponentCount(); i++) {
                        Vector2f subDelta = element.getSubElementDeltaPosition(i);
                        JsonObject subObj = new JsonObject();
                        subObj.addProperty("index", i);
                        subObj.addProperty("deltaX", subDelta.x);
                        subObj.addProperty("deltaY", subDelta.y);
                        subArray.add(subObj);
                    }
                    obj.add("subComponents", subArray);
                }
                jsonArray.add(obj);
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter fw = new FileWriter(file);
            fw.write(gson.toJson(jsonArray));
            fw.close();
        } catch (IOException e) {
            //ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error saving GUI Deltas!");
        }
    }

}
