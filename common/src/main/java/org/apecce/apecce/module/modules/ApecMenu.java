package org.apecce.apecce.module.modules;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import org.apecce.apecce.ApecCE;
import org.apecce.apecce.events.ClientTick;
import org.apecce.apecce.events.Render2D;
import org.apecce.apecce.gui.Element;
import org.apecce.apecce.gui.elements.HPBar;
import org.apecce.apecce.module.Module;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

@Module.ModuleInfo(name = "ApecMenu", description = "ApecCE's menu")
@Module.ModuleEnabled
public class ApecMenu extends Module {

    public List<Element> guiElements = new ArrayList<Element>() {
        {
            add(new HPBar());
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
