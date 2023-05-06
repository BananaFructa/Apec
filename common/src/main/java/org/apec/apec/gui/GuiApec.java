package org.apec.apec.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class GuiApec extends Gui {

    public GuiApec(Minecraft minecraft, ItemRenderer itemRenderer) {
        super(minecraft, itemRenderer);
    }

    @Override
    public void render(PoseStack poseStack, float f) {
        super.render(poseStack, f);
//        ApecCE.getInstance().getLogger().info("rendering apecce gui");
    }
}
