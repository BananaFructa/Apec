package org.apec.apec.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Data;
import me.zero.alpine.listener.EventSubscriber;
import org.apec.apec.Apec;
import org.apec.apec.MC;
import org.apec.apec.module.modules.ApecMenu;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Data
public class Element implements MC, EventSubscriber {

    protected ApecMenu manager;

    protected Vector2f position;

    protected List<Vector2f> subComponents = new ArrayList<>();

    protected float scale = 1;
    protected boolean scalable = true;
    protected Vector2f scaledResolution; // NOTE: X = width, Y = height

    protected ElementType elementType;

    public Element(ElementType elementType) {
        this.elementType = elementType;
        Apec.getInstance().getEventBus().subscribe(this);
    }

    public Element(ElementType elementType, int subComponentCount) {
        this(elementType);
        IntStream.range(0, subComponentCount).forEach(i -> this.subComponents.add(new Vector2f(0, 0)));
    }

    public void init(ApecMenu apecMenu) {
        this.manager = apecMenu;
        this.scaledResolution = new Vector2f(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }


    public void render(PoseStack poseStack, float delta) {

    }

    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {

    }

    public void tick() {

    }
}
