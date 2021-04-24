package Apec.Components.Gui;

import Apec.ApecMain;
import Apec.Component;
import Apec.ComponentId;
import Apec.Components.Gui.ContainerGuis.ApecContainerGui;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Events.ApecSettingChangedState;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import Apec.Utils.ItemNameFetcher;
import Apec.Utils.ItemRarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class CustomItemToolTip extends Component {

    private Minecraft mc = Minecraft.getMinecraft();
    private List<Runnable> afterRuns = new ArrayList<Runnable>();


    public CustomItemToolTip() {
        super(ComponentId.CUSTOM_ITEM_TOOL_TIP);
    }

    final char starChar = '\u272A';

    @SubscribeEvent
    public void onSettingChanged(ApecSettingChangedState event) {
        boolean on = (ApecMain.Instance.settingsManager.getSettingState(SettingID.GUIS_WHEN_DISABLED) || GUIModifier.Instance.getEnableState()) && ApecMain.Instance.settingsManager.getSettingState(SettingID.CUSTOM_TOOL_TIP);
        if (on != this.getEnableState()) this.Toggle();
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (this.getEnableState()) {
            Slot hoveredSlot = null;
            if (mc.currentScreen instanceof GuiContainer) {
                hoveredSlot = ApecUtils.readDeclaredField(GuiContainer.class, mc.currentScreen, "theSlot");
            } else if (mc.currentScreen instanceof ApecContainerGui) {
                hoveredSlot = ((ApecContainerGui) mc.currentScreen).theSlot;
            }
            if (hoveredSlot != null) {
                if (hoveredSlot.getStack() != null && mc.thePlayer.inventory.getItemStack() == null) {
                    int _x = event.mouseX + 8;
                    int _y = event.mouseY - 15;

                    int maxWidth = 0;
                    List<String> toolTip = hoveredSlot.getStack().getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
                    int maxHeight = 8 + 3 + 6;

                    for (String s : toolTip) {
                        int width = mc.fontRendererObj.getStringWidth(s);
                        if (width > maxWidth) {
                            maxWidth = width;
                        }
                    }


                    if (toolTip.size() > 1) {
                        maxHeight += 2 + (toolTip.size() - 1) * 10;
                    }

                    if (_x + maxWidth + 4> mc.currentScreen.width) {
                        _x -= 28 + maxWidth;
                    }

                    if (_y + maxHeight > mc.currentScreen.height) {
                        _y = mc.currentScreen.height - maxHeight;
                    }

                    final int x = _x;
                    final int y = _y;

                    GlStateManager.disableDepth();
                    RenderHelper.disableStandardItemLighting();

                    drawUpperInfo(hoveredSlot,toolTip,x,y,maxWidth);

                    int lightEndX = (int)((maxWidth) * 3f/5f);
                    lightEndX /= 3;
                    for (int i = 0;i < lightEndX;i++) {
                        int alpha1 = (int)((1-Math.tanh(((float)i/lightEndX))) * 170f);
                        int alpha2 = (int)((1-Math.tanh(((float)i/lightEndX) * 2)) * 170f);
                        GuiScreen.drawRect(x+1+i*3,y-1,x+1+(i+1)*3,y,(alpha1 << 24) | 0xffffff);
                        GuiScreen.drawRect(x+1+i*3,y,x+1+(i+1)*3,y + 1,(alpha2 << 24) | 0xffffff);
                    }

                    int lightEndY = (int)((maxHeight) * 3f/5f);
                    lightEndY /= 3;
                    for (int i = 0;i < lightEndY;i++) {
                        int alpha1 = (int)((1-Math.tanh(((float)i/lightEndY))) * 170f);
                        int alpha2 = (int)((1-Math.tanh(((float)i/lightEndY) * 2)) * 170f);
                        GuiScreen.drawRect(x,y+i*3,x+1,y+(i+1)*3,(alpha1 << 24) | 0xffffff);
                        GuiScreen.drawRect(x+1,y+1+i*3,x+2,y+1+(i+1)*3,(alpha2 << 24) | 0xffffff);
                    }

                    for (Runnable runnable : afterRuns) {
                        runnable.run();
                    }

                    afterRuns.clear();

                    GlStateManager.enableDepth();
                    RenderHelper.enableStandardItemLighting();
                }
            }
        }
    }

    public void drawUpperInfo(final Slot hoveredSlot,final List<String> toolTip, final int x,final int y,final int maxWidth) {
        ItemRarity rarity = ItemRarity.getRarity(mc, hoveredSlot.getStack());

        if (rarity != null) {

            int xEnd = x + maxWidth + 6;
            String itemDisplayName = ItemNameFetcher.getDisplayName(hoveredSlot.getStack());
            String nameWithNoStar = itemDisplayName.replace(String.valueOf(starChar), "");
            int stars = itemDisplayName.length() - nameWithNoStar.length();

            int xIcon = xEnd - 20;

            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1);
            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/rarityIcons.png"));
            int rarityIdx = rarity.ordinal();
            float scaleFactor = 20f / 16f;
            GlStateManager.scale(scaleFactor, scaleFactor, 1);
            mc.currentScreen.drawTexturedModalRect((xIcon + 1) / scaleFactor, (y - 21) / scaleFactor, rarityIdx * 16, 0, 16, 16);
            {
                final int current = xIcon;
                afterRuns.add(new Runnable() {
                    @Override
                    public void run() {
                        GlStateManager.pushMatrix();
                        GlStateManager.disableLighting();
                        GuiScreen.drawRect(current, y - 21, current + 1, y - 1, 0xff000000);
                        GuiScreen.drawRect(current + 1, y - 22, current + 21, y - 21, 0xff000000);
                        GuiScreen.drawRect(current + 21, y - 21, current + 22, y - 1, 0xff000000);
                        GlStateManager.enableLighting();
                        GlStateManager.popMatrix();
                    }
                });
            }
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1);

            if (stars > 0) {
                xIcon -= 58 + 2;
                mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/itemStars.png"));
                mc.currentScreen.drawTexturedModalRect(xIcon + 1, y - 17, 0, 0, 58, 16);
                for (int i = 0; i < stars; i++) {
                    mc.currentScreen.drawTexturedModalRect(xIcon + 4 + 11 * i, y - 17, 58, 0, 8, 12);
                }

                final int current = xIcon;
                afterRuns.add(new Runnable() {
                    @Override
                    public void run() {
                        GlStateManager.disableLighting();
                        GuiScreen.drawRect(current, y - 17, current + 1, y - 1, 0xff000000);
                        GuiScreen.drawRect(current + 1, y - 18, current + 59, y - 17, 0xff000000);
                        GuiScreen.drawRect(current + 59, y - 17, current + 60, y - 1, 0xff000000);
                        GlStateManager.enableLighting();
                    }
                });
            }
            GlStateManager.popMatrix();
        }
    }

    public int getGearScore(List<String> toolTip) {
        try {
            for (String s : toolTip) {
                if (ApecUtils.containedByCharSequence(s, "Gear Score:")) {
                    String unformatted = ApecUtils.removeAllCodes(s);
                    unformatted = unformatted.replace("Gear Score: ", "");
                    String[] split = unformatted.split(" ");
                    return Integer.parseInt(split[0]);
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
            return -1;
        }
        return -1;
    }

}
