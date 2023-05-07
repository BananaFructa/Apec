package org.apec.apec.module.modules;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import org.apec.apec.events.ClientTick;
import org.apec.apec.events.Render2D;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.elements.DebugText;
import org.apec.apec.gui.elements.health.HPBar;
import org.apec.apec.gui.elements.health.HPText;
import org.apec.apec.gui.elements.mana.MPBar;
import org.apec.apec.gui.elements.mana.MPText;
import org.apec.apec.module.Module;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

@Module.ModuleInfo(name = "ApecMenu", description = "Apec's menu")
@Module.ModuleEnabled
public class ApecMenu extends Module {

    public List<Element> guiElements = new ArrayList<Element>() {
        {
            addAll(List.of(
                    new HPBar(),
                    new HPText(),
                    new MPBar(),
                    new MPText(),
                    new DebugText()
            ));
        }
    };

    @Override
    public void postInit() {
        guiElements.forEach(element -> element.init(this));
    }

    @Subscribe
    Listener<ClientTick> clientTickListener = new Listener<>(event -> guiElements.forEach(Element::tick));

    @Subscribe
    Listener<Render2D> render2DListener = new Listener<>(event -> {
        Vector2f scaledResolution = new Vector2f(event.width(), event.height());
        guiElements.forEach(element -> {
            event.poseStack().pushPose();
            element.drawText(event.poseStack(), scaledResolution);
            event.poseStack().popPose();
        });
        guiElements.forEach(element -> {
            event.poseStack().pushPose();
            element.render(event.poseStack(), event.delta());
            event.poseStack().popPose();
        });
    });


}
