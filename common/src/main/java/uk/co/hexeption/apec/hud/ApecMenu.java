package uk.co.hexeption.apec.hud;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.event.events.client.ClientTickEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.hud.customization.CustomizationScreen;
import uk.co.hexeption.apec.hud.elements.BottomBar;
import uk.co.hexeption.apec.hud.elements.ExtraInfo;
import uk.co.hexeption.apec.hud.elements.ItemHotBar;
import uk.co.hexeption.apec.hud.elements.health.HPBar;
import uk.co.hexeption.apec.hud.elements.health.HPText;
import uk.co.hexeption.apec.hud.elements.mana.MPBar;
import uk.co.hexeption.apec.hud.elements.mana.MPText;
import uk.co.hexeption.apec.hud.elements.xp.XPBar;
import uk.co.hexeption.apec.hud.elements.xp.XPText;
import uk.co.hexeption.apec.settings.SettingID;
import uk.co.hexeption.apec.utils.ApecUtils;

public class ApecMenu implements MC {

    public List<Element> guiElements = new ArrayList<>() {
        {
            addAll(List.of(
//                    new DebugText(),
                    new HPText(),
                    new HPBar(),
                    new MPText(),
                    new MPBar(),
                    new XPText(),
                    new XPBar(),
                    new ExtraInfo(),
                    new BottomBar(),
                    new ItemHotBar()
            ));
        }
    };


    public void init() {

        applyDeltas();

        for (Element element : guiElements) {
            element.init(this);
        }

        ClientTickEvent.CLIENT_PRE.register(client -> {
            if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
                return;
            }
            for (Element element : guiElements) {
                element.tick();
            }
        });

    }

    public void CustomizationMenuOpened() {
        for (Element element : guiElements) {
            element.editInit();
        }
    }

    public void render(GuiGraphics guiGraphics) {
        if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
            return;
        }
        for (Element element : guiElements) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(element.scale, element.scale, element.scale);
            element.drawText(guiGraphics, mc.screen instanceof CustomizationScreen);
            guiGraphics.pose().popPose();
        }
    }

    public Vector2f applyGlobalChanges(Element element, Vector2f anchorPoint) {
        boolean isbbUp = Apec.INSTANCE.settingsManager.getSettingState(SettingID.BB_ON_TOP);
        if (isbbUp && element.getDeltaPosition().length() == 0)
            anchorPoint = ApecUtils.addVec(anchorPoint, new Vector2f(0, 20));
        return anchorPoint;
    }

    private void applyDeltas() {
        try {
            File file = new File("config/Apec/GuiDeltas.json");
            if (!file.exists()) return;
            Gson gson = new Gson();
            String jsonContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            JsonArray jsonArray = JsonParser.parseString(jsonContent).getAsJsonArray();
            for (JsonElement el : jsonArray) {
                JsonObject obj = el.getAsJsonObject();
                String typeName = obj.get("type").getAsString();
                ElementType elementType = ElementType.valueOf(typeName);
                Element element = getGuiComponent(elementType);
                float deltaX = obj.get("deltaX").getAsFloat();
                float deltaY = obj.get("deltaY").getAsFloat();
                float scale = obj.get("scale").getAsFloat();
                element.setDeltaPosition(new org.joml.Vector2f(deltaX, deltaY));
                element.setScale(scale);
                if (obj.has("subComponents")) {
                    JsonArray subArray = obj.getAsJsonArray("subComponents");
                    for (JsonElement subEl : subArray) {
                        JsonObject subObj = subEl.getAsJsonObject();
                        int subIdx = subObj.get("index").getAsInt();
                        float subDeltaX = subObj.get("deltaX").getAsFloat();
                        float subDeltaY = subObj.get("deltaY").getAsFloat();
                        element.setSubElementDeltaPosition(subIdx, new org.joml.Vector2f(subDeltaX, subDeltaY));
                    }
                }
            }
        } catch (Exception e) {
            //ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading GUI deltas!");
            try {
                new File("config/Apec/GuiDeltas.json").delete();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public <T extends Element> T getGuiComponent(ElementType type) {
        for (Element component : guiElements) {
            if (component.type == type) return (T) component;
        }
        return null;
    }

}
