package Apec.Components.Gui;

import Apec.ApecMain;
import Apec.Component;
import Apec.ComponentId;
import Apec.Components.Gui.ContainerGuis.ApecContainerGui;
import Apec.Utils.ApecUtils;
import Apec.Utils.ItemRarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class CustomItemToolTip extends Component {

    private Minecraft mc = Minecraft.getMinecraft();

    public CustomItemToolTip() {
        super(ComponentId.CUSTOM_ITEM_TOOL_TIP);
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        Slot hoveredSlot = null;
        if (mc.currentScreen instanceof GuiContainer) {
            hoveredSlot = ApecUtils.readDeclaredField(GuiContainer.class,mc.currentScreen,"theSlot");
        } else if (mc.currentScreen instanceof ApecContainerGui) {
            hoveredSlot = ((ApecContainerGui)mc.currentScreen).theSlot;
        }
        if (hoveredSlot != null) {
            if (hoveredSlot.getStack() != null) {
                ItemRarity rarity = ItemRarity.getRarity(mc,hoveredSlot.getStack());
                if (rarity != null) {
                    int x = event.mouseX + 8;
                    int y = event.mouseY - 15;

                    int maxWidth = 0;
                    List<String> toolTip = hoveredSlot.getStack().getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

                    for (String s : toolTip) {
                        int width = mc.fontRendererObj.getStringWidth(s);
                        if (width > maxWidth) {
                            maxWidth = width;
                        }
                    }

                    int k = 8 + 3;

                    if (toolTip.size() > 1) {
                        k += 2 + (toolTip.size() - 1) * 10;
                    }

                    if (x + maxWidth + 4 > mc.currentScreen.width) {
                        x -= 28 + maxWidth;
                    }

                    if (y + k + 6 > mc.currentScreen.height) {
                        y = mc.currentScreen.height - k - 6;
                    }

                    int xEnd = x + maxWidth + 6;

                    if (hoveredSlot.getStack() != null && mc.thePlayer.inventory.getItemStack() == null) {

                        int xIcon = xEnd - 20;

                        GlStateManager.disableDepth();
                        RenderHelper.disableStandardItemLighting();

                        GlStateManager.pushMatrix();

                        GlStateManager.color(1,1,1);
                        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId,"gui/rarityIcons.png"));
                        int rarityIdx = rarity.ordinal();
                        float scaleFactor = 20f/16f;
                        GlStateManager.scale(scaleFactor,scaleFactor,1);
                        if (rarityIdx == 8) rarityIdx = 7;
                        mc.currentScreen.drawTexturedModalRect((xIcon+1)/scaleFactor,(y-21)/scaleFactor,rarityIdx * 16,0,16,16);

                        GlStateManager.popMatrix();

                        GlStateManager.disableLighting();

                        GuiScreen.drawRect(xIcon, y - 21, xIcon + 1, y - 1, 0xff000000);
                        GuiScreen.drawRect(xIcon + 1, y - 22, xIcon + 21, y - 21, 0xff000000);
                        GuiScreen.drawRect(xIcon + 21, y - 21, xIcon + 22, y - 1, 0xff000000);

                        GlStateManager.enableLighting();
                        GlStateManager.enableDepth();
                        RenderHelper.enableStandardItemLighting();
                    }
                }
            }
        }
    }

}
