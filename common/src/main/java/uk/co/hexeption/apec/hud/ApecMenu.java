package uk.co.hexeption.apec.hud;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.hud.customization.CustomizationScreen;
import uk.co.hexeption.apec.hud.elements.DebugText;
import uk.co.hexeption.apec.hud.elements.ExtraInfo;
import uk.co.hexeption.apec.hud.elements.health.HPBar;
import uk.co.hexeption.apec.hud.elements.health.HPText;
import uk.co.hexeption.apec.hud.elements.mana.MPBar;
import uk.co.hexeption.apec.hud.elements.mana.MPText;
import uk.co.hexeption.apec.hud.elements.xp.XPBar;
import uk.co.hexeption.apec.hud.elements.xp.XPText;
import uk.co.hexeption.apec.utils.ApecUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApecMenu implements MC {

    public List<Element> guiElements = new ArrayList<>() {
        {
            addAll(List.of(
                    new DebugText(),
                    new HPText(),
                    new HPBar(),
                    new MPText(),
                    new MPBar(),
                    new XPText(),
                    new XPBar(),
                    new ExtraInfo()
            ));
        }
    };

    public boolean isCustomizationScreenOpen = false;

    public void init() {

        applyDeltas();

        guiElements.forEach(element -> {

            element.init(this);
        });

        ClientTickEvent.CLIENT_PRE.register(client -> {
            if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
                return;
            }
            guiElements.forEach(Element::tick);
        });

        ClientGuiEvent.RENDER_HUD.register((guiGraphics, deltaTracker) -> {
            if (!Apec.SKYBLOCK_INFO.isOnSkyblock()) {
                return;
            }
            Vector2f scaledResolution = new Vector2f(Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
            guiElements.forEach(element -> {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(element.scale, element.scale, element.scale);
                element.drawText(guiGraphics, scaledResolution, mc.screen instanceof CustomizationScreen);
                guiGraphics.pose().popPose();
            });
        });
    }

    public Vector2f applyGlobalChanges(Element element, Vector2f anchorPoint) {
        boolean isbbUp = false; //todo: Add Settings for bbup
        if (isbbUp && element.getDeltaPosition().length() == 0)
            anchorPoint = ApecUtils.addVec(anchorPoint, new Vector2f(0, 20));
        return anchorPoint;
    }

    private void applyDeltas() {
        try {
            Scanner scanner = new Scanner(new File("config/Apec/GuiDeltas.txt"));
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                String[] tempSplit = s.split("#");
                int subComponent = -1;
                int idx = 0;
                if (tempSplit[0].contains("!")) {
                    String[] tempSplit_ = tempSplit[0].split("!");
                    idx = Integer.parseInt(tempSplit_[0]);
                    subComponent = Integer.parseInt(tempSplit_[1]);
                } else {
                    idx = Integer.parseInt(tempSplit[0]);
                }
                Vector2f delta = new Vector2f(Float.parseFloat(tempSplit[1].split("@")[0]), Float.parseFloat(tempSplit[1].split("@")[1]));
                float scale = 1f;
                if (tempSplit[1].split("@").length == 3) {
                    scale = Float.parseFloat(tempSplit[1].split("@")[2]);
                }
                if (subComponent == -1) {
                    getGuiComponent(ElementType.values()[idx]).setDeltaPosition(delta);
                    getGuiComponent(ElementType.values()[idx]).setScale(scale);
                } else {
                    //getGuiComponent(ElementType.values()[idx]).setSubElementDeltaPosition(delta, subComponent);
                }
            }
            scanner.close();
        } catch (Exception e) {
            //ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading GUI deltas!");
            // Delete the file if it is corrupted
            try {
                new File("config/Apec/GuiDeltas.txt").delete();
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
