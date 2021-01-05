package Apec.Components.Gui.GuiIngame;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Components.Gui.GuiIngame.GuiElements.GUIComponent;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.FakedScaledResolution;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.lang.reflect.Method;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;


/**
 * The scoreboard and the auction bar has been disabled, all of their data is being processed and displayed in a different manner
 * This is a modified copy of the original GuiIngameForge.class file , used to make other mods compatible
 * And this kids is the story of how banana gone insane
 */

public class ApecGuiIngameForge extends ApecGuiIngame {

    GUIComponent HotBar = (GUIModifier.Instance.getGuiComponent(GUIComponentID.HOT_BAR));
    RenderGameOverlayEvent eventParent;
    Method pre,post;

    public ApecGuiIngameForge  (Minecraft mc) {
        super(mc);
    }

    private static final int WHITE = 0xFFFFFF;

    @Override
    public void renderGameOverlay(float partialTicks) {
        try {
            pre = GuiIngameForge.class.getDeclaredMethod("pre", RenderGameOverlayEvent.ElementType.class);
            pre.setAccessible(true);
            post = GuiIngameForge.class.getDeclaredMethod("post", RenderGameOverlayEvent.ElementType.class);
            post.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        eventParent = new RenderGameOverlayEvent(partialTicks,new ScaledResolution(mc));
        super.renderGameOverlay(partialTicks);
    }

    protected void renderTooltip(ScaledResolution sr, float partialTicks)
    {
        Vector2f pos = HotBar.getRealAnchorPoint();
        float scale = HotBar.getScale();

        // This is for modifying the position of the hotbar
        FakedScaledResolution fsr = new FakedScaledResolution(mc,(int)((pos.x/scale + 91)*2),(int)(pos.y/scale + 22));

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        super.renderTooltip(fsr,partialTicks);
        GlStateManager.popMatrix();
    }

    @Override
    protected void renderArmor(int width, int height) {
        try {
            if ((Boolean) pre.invoke(this,ARMOR)) return;
            mc.mcProfiler.startSection("armor");
            mc.mcProfiler.endSection();
            post.invoke(this,ARMOR);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    protected void renderAir(int width, int height) {
        try {
            if ((Boolean) pre.invoke(this,AIR)) return;
            mc.mcProfiler.startSection("air");
            mc.mcProfiler.endSection();
            post.invoke(this,AIR);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void renderHealth(int width, int height) {
        try {
            if ((Boolean) pre.invoke(this,HEALTH)) return;
            mc.mcProfiler.startSection("health");
            mc.mcProfiler.endSection();
            post.invoke(this,HEALTH);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void renderFood(int width, int height) {
        try {
            if ((Boolean) pre.invoke(this,FOOD)) return;
            mc.mcProfiler.startSection("food");
            mc.mcProfiler.endSection();
            post.invoke(this,FOOD);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    protected void renderExperience(int width, int height) {
        try {
            if ((Boolean) pre.invoke(this,EXPERIENCE)) return;
            mc.mcProfiler.startSection("expBar");
            this.mc.mcProfiler.endSection();
            post.invoke(this,EXPERIENCE);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    protected void renderToolHightlight(ScaledResolution res)
    {
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator())
        {
            mc.mcProfiler.startSection("toolHighlight");

            if ((this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) || mc.currentScreen instanceof CustomizationGui)
            {
                String name;
                if (this.highlightingItemStack != null) {
                    name = this.highlightingItemStack.getDisplayName();
                    if (this.highlightingItemStack.hasDisplayName())
                        name = EnumChatFormatting.ITALIC + name;

                    name = this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, name);
                } else {
                    name = "Item";
                }

                int opacity = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255 || mc.currentScreen instanceof CustomizationGui) opacity = 255;

                if (opacity > 0)
                {
                    GUIComponent guiComponentH = ((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.HOT_BAR);
                    Vector2f posH = guiComponentH.getRealAnchorPoint();
                    Vector2f deltaH = guiComponentH.getDelta_position();
                    float scaleH = guiComponentH.getScale();

                    GUIComponent guiComponentT = ((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.TOOL_TIP_TEXT);
                    Vector2f deltaT = guiComponentT.getDelta_position();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scaleH,scaleH,scaleH);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

                    int x,y;
                    if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.ITEM_HIGHLIGHT_TEXT)) {
                        x = (int) (posH.x/scaleH) + 1;
                        y = (int) (posH.y/scaleH) - 10;
                    } else {
                        x = (int)((- this.getFontRenderer().getStringWidth(name)/ 2) - 92/scaleH + res.getScaledWidth()/scaleH);
                        y = (int)(res.getScaledHeight()/scaleH - 67/scaleH);
                        x += deltaH.x/scaleH;
                        y += deltaH.y/scaleH;
                    }
                    x += deltaT.x/scaleH;
                    y += deltaT.y/scaleH;
                    mc.fontRendererObj.drawStringWithShadow(name,x, y, WHITE | (opacity << 24));
                    GlStateManager.popMatrix();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }

            mc.mcProfiler.endSection();
        }
        else if (this.mc.thePlayer.isSpectator())
        {
            this.spectatorGui.func_175263_a(res);
        }
    }

    @Override
    protected void renderRecordOverlay(int width, int height, float partialTicks) {
        // no record
    }
}
