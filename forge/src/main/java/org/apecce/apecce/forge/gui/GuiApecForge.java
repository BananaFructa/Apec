package org.apecce.apecce.forge.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.apecce.apecce.ApecCE;
import org.apecce.apecce.events.Render2D;

public class GuiApecForge extends ForgeGui {

    public GuiApecForge(Minecraft mc) {
        super(mc);
    }

    @Override
    public void render(PoseStack poseStack, float partialTick) {
        super.render(poseStack, partialTick);

        Window window = this.getMinecraft().getWindow();
        Render2D event = new Render2D(window.getGuiScaledWidth(), window.getGuiScaledHeight(), poseStack, partialTick);
        ApecCE.getInstance().getEventBus().post(event);
    }
}
