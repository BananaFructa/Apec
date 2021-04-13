package Apec.Components.Gui.GuiIngame;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.FakedScaledResolution;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.util.vector.Vector2f;

import java.lang.reflect.Method;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;


/**
 * And this kids is the story of how banana gone insane
 */

public class ApecGuiIngameForge extends GuiIngameForge {

    private final GUIComponent HotBar = GUIModifier.Instance.getGuiComponent(GUIComponentID.HOT_BAR);
    private final GUIComponent ToolTipText = GUIModifier.Instance.getGuiComponent(GUIComponentID.TOOL_TIP_TEXT);
    private final Method pre = ApecUtils.GetDeclaredMethod(GuiIngameForge.class,"pre",RenderGameOverlayEvent.ElementType.class);
    private final Method post = ApecUtils.GetDeclaredMethod(GuiIngameForge.class,"post",RenderGameOverlayEvent.ElementType.class);

    public ApecGuiIngameForge  (Minecraft mc) {
        super(mc);
    }

    /**
     * @return Returns the name of the highlighted item stack
     */
    public String GetHighlightText() {
        if (this.highlightingItemStack != null) {
            return this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, this.highlightingItemStack.getDisplayName());
        } else {
            return "Item";
        }
    }

    @Override
    protected void renderTooltip(ScaledResolution sr, float partialTicks)
    {
        Vector2f pos = HotBar.getCurrentAnchorPoint();
        float scale = HotBar.getScale();

        // This is for modifying the position of the hotbar, the faked scale resolution class is used instead of glTranslate so it doesn't affect 5zig and other mods
        FakedScaledResolution fsr = new FakedScaledResolution(mc,(int)((pos.x/scale + 91)*2),(int)(pos.y/scale + 22));

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        super.renderTooltip(fsr,partialTicks);
        GlStateManager.popMatrix();
    }

    @Override
    protected void renderToolHightlight(ScaledResolution res) {

        Vector2f posH = HotBar.getCurrentAnchorPoint();
        Vector2f deltaH = HotBar.getDeltaPosition();
        float scaleH = HotBar.getScale();

        Vector2f deltaT = ToolTipText.getDeltaPosition();

        GlStateManager.pushMatrix();

        String name = "Item";

        if (this.highlightingItemStack != null) {
            name = this.highlightingItemStack.getDisplayName();
            if (this.highlightingItemStack.hasDisplayName())
                name = EnumChatFormatting.ITALIC + name;
            name = this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, name);
        }


        // Calculating the position where apec wants the tooltip
        int x, y;
        if (!ApecMain.Instance.settingsManager.getSettingState(SettingID.ITEM_HIGHLIGHT_TEXT)) {
            x = (int) (posH.x / scaleH) + 1;
            y = (int) (posH.y / scaleH) - 10;
        } else {
            x = (int) ((-this.getFontRenderer().getStringWidth(name) / 2) - 92 / scaleH + res.getScaledWidth() / scaleH);
            y = (int) (res.getScaledHeight() / scaleH - 67 / scaleH);
            x += deltaH.x / scaleH;
            y += deltaH.y / scaleH;
        }
        x += deltaT.x / scaleH;
        y += deltaT.y / scaleH;


        if (this.highlightingItemStack != null) {

            // Calculating the normal tooltip position
            int Rx, Ry;
            Ry = res.getScaledHeight() - 59;
            if (!mc.playerController.shouldDrawHUD()) y += 14;
            FontRenderer font = highlightingItemStack.getItem().getFontRenderer(highlightingItemStack);
            if (font != null) {
                Rx = (res.getScaledWidth() - font.getStringWidth(name)) / 2;
            } else {
                Rx = (res.getScaledWidth() - mc.fontRendererObj.getStringWidth(name)) / 2;
            }

            GlStateManager.translate(x - Rx, y - Ry, 0);
            GlStateManager.scale(scaleH, scaleH, scaleH);

        }


        // Makes the tooltip show when the customization ui is opened
        if (mc.currentScreen instanceof CustomizationGui) {
            this.remainingHighlightTicks = 10;
            if (this.highlightingItemStack == null) {
                // Draws the tooltip texture when the highlight stack is null when the customization menu is opened
                mc.fontRendererObj.drawStringWithShadow(name, x, y, 0xffffffff);
            }
        }


        super.renderToolHightlight(res);


        GlStateManager.popMatrix();

    }

    @Override
    protected void renderRecordOverlay(int width, int height, float partialTicks) {
        // no record
    }

    /**
     * Removing rendering is done this done this way in the case other mods might use the render game overlay events
     * Note the methods from the outer class are not called
     */

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
}
