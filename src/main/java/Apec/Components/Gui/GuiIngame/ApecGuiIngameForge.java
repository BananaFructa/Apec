package Apec.Components.Gui.GuiIngame;

import Apec.*;
import Apec.Components.Gui.GuiIngame.GuiElements.GUIComponent;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.*;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTHMOUNT;


/**
 * The scoreboard and the auction bar has been disabled, all of their data is being processed and displayed in a different manner
 * This is a modified copy of the original GuiIngameForge.class file , used to make other mods compatible
 * And this kids is the story of how banana gone insane
 */

public class ApecGuiIngameForge extends ApecGuiIngame {

    GUIModifier gUIModifier;


    public ApecGuiIngameForge  (Minecraft mc) {
        super(mc);
        Object comp = ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER);
        if (comp != null) gUIModifier = (GUIModifier) comp;
        debugOverlay = new GuiOverlayDebugForge(mc);
    }

    public void drawThiccBorderString(String s,int x,int y,int c) {
        String noColorCodeS = ApecUtils.removeColorCodes(s);
        this.getFontRenderer().drawString(noColorCodeS, x + 1,y, (c >> 24) << 24);
        this.getFontRenderer().drawString(noColorCodeS, x - 1, y, (c >> 24) << 24);
        this.getFontRenderer().drawString(noColorCodeS, x, y + 1, (c >> 24) << 24);
        this.getFontRenderer().drawString(noColorCodeS, x, y - 1, (c >> 24) << 24);
        this.getFontRenderer().drawString(s, x, y, c);
    }

    //private static final ResourceLocation VIGNETTE     = new ResourceLocation("textures/misc/vignette.png");
    //private static final ResourceLocation WIDGITS      = new ResourceLocation("textures/gui/widgets.png");
    //private static final ResourceLocation PUMPKIN_BLUR = new ResourceLocation("textures/misc/pumpkinblur.png");

    private static final int WHITE = 0xFFFFFF;

    public static int left_height = 39;
    public static int right_height = 39;

    private ScaledResolution res = null;
    private FontRenderer fontrenderer = null;
    private RenderGameOverlayEvent eventParent;
    //private static final String MC_VERSION = MinecraftForge.MC_VERSION;
    private GuiOverlayDebugForge debugOverlay;


    @Override
    public void renderGameOverlay(float partialTicks)
    {
        res = new ScaledResolution(mc);
        eventParent = new RenderGameOverlayEvent(partialTicks, res);
        try {
            //FieldUtils.writeField(this,"eventParent",eventParent,true);
            Field f = GuiIngameForge.class.getDeclaredField("eventParent");
            f.setAccessible(true);
            f.set(this,eventParent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        GuiIngameForge.renderJumpBar = mc.thePlayer.isRidingHorse();

        right_height = 39;
        left_height = 39;

        if (pre(ALL)) return;

        fontrenderer = mc.fontRendererObj;
        mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();

        if (Minecraft.isFancyGraphicsEnabled())
        {
            renderVignette(mc.thePlayer.getBrightness(partialTicks), res);
        }
        else
        {
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        if (GuiIngameForge.renderHelmet) renderHelmet(res, partialTicks);

        if (GuiIngameForge.renderPortal && !mc.thePlayer.isPotionActive(Potion.confusion))
        {
            renderPortal(res, partialTicks);
        }

        renderTooltip(res, partialTicks);

        // This may be very bad but i can't be bothered
        FakedScaledResolution fsr = new FakedScaledResolution(mc,0,100);
        super.renderTooltip(fsr,partialTicks);

        if (gUIModifier != null) gUIModifier.onRender(res);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        zLevel = -90.0F;
        rand.setSeed((long)(updateCounter * 312871));

        if (GuiIngameForge.renderCrosshairs) renderCrosshairs(width, height);
        if (GuiIngameForge.renderBossHealth) renderBossHealth();

        pre(HEALTH);
        mc.mcProfiler.startSection("health");
        mc.mcProfiler.endSection();
        post(HEALTH);
        pre(ARMOR);
        mc.mcProfiler.startSection("armor");
        mc.mcProfiler.endSection();
        post(ARMOR);
        pre(FOOD);
        mc.mcProfiler.startSection("food");
        mc.mcProfiler.endSection();
        post(FOOD);
        pre(HEALTHMOUNT);
        mc.mcProfiler.endStartSection("mountHealth");
        GlStateManager.disableBlend();
        post(HEALTHMOUNT);
        pre(AIR);
        mc.mcProfiler.startSection("air");
        mc.mcProfiler.endSection();
        post(AIR);

        renderSleepFade(width, height);

        if (renderJumpBar)
        {
            renderJumpBar(width, height);
        } else {
            pre(EXPERIENCE);
            mc.mcProfiler.startSection("expBar");
            mc.mcProfiler.endSection();
            post(EXPERIENCE);
        }

        renderToolHightlight(res);
        try {
            renderHUDText(width, height);
        } catch (Exception e) {
        }
        renderTitle(width, height, partialTicks);


        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.thePlayer.getName());
        if (scoreplayerteam != null)
        {
            int slot = scoreplayerteam.getChatFormat().getColorIndex();
            if (slot >= 0) objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
        }
        ScoreObjective scoreobjective1 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableAlpha();

        renderChat(width, height);

        renderPlayerList(width, height);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();

        post(ALL);
    }


    protected void renderTooltip(ScaledResolution sr, float partialTicks)
    {
        if (pre(HOTBAR)) return;
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            GUIComponent guiComponent = ((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.HOT_BAR);
            Vector2f pos = ApecUtils.addVec(guiComponent.getAnchorPointPosition(),guiComponent.getDelta_position());
            float scale = guiComponent.getScale();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale,scale,scale);
            float f = this.zLevel;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(pos.x/scale, pos.y/scale, 0, 0, 182, 22);
            this.drawTexturedModalRect(pos.x/scale- 1 + entityplayer.inventory.currentItem * 20, pos.y/scale-1, 0, 22, 24, 22);

            GlStateManager.enableBlend();
            GlStateManager.enableRescaleNormal();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            this.zLevel = f;

            for (int j = 0; j < 9; ++j)
            {
                int k = (int)(pos.x/scale + 1 + j * 20 + 2);
                int l = (int)(pos.y/scale + 3);
                this.renderHotbarItem(j, (int)k, (int)l, partialTicks, entityplayer);
            }
            GlStateManager.popMatrix();

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
        post(HOTBAR);
    }

    public ScaledResolution getResolution()
    {
        return res;
    }

    private void renderHelmet(ScaledResolution res, float partialTicks)
    {
        if (pre(HELMET)) return;

        ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() != null)
        {
            if (itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
            {
                renderPumpkinOverlay(res);
            }
            else
            {
                itemstack.getItem().renderHelmetOverlay(itemstack, mc.thePlayer, res, partialTicks);
            }
        }

        post(HELMET);
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
                    fontrenderer.drawStringWithShadow(name,x, y, WHITE | (opacity << 24));
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

    protected void renderHUDText(int width, int height)
    {
        mc.mcProfiler.startSection("forgeHudText");
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        ArrayList<String> listL = new ArrayList<String>();
        ArrayList<String> listR = new ArrayList<String>();

        if (mc.isDemo())
        {
            long time = mc.theWorld.getTotalWorldTime();
            if (time >= 120500L)
            {
                listR.add(I18n.format("demo.demoExpired"));
            }
            else
            {
                listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - time))));
            }
        }

        if (this.mc.gameSettings.showDebugInfo && !pre(DEBUG))
        {
            listL.addAll(debugOverlay.getLeft());
            listR.addAll(debugOverlay.getRight());
            post(DEBUG);
        }

        RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(eventParent, listL, listR);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            int top = 2;
            for (String msg : listL)
            {
                if (msg == null) continue;
                drawRect(1, top - 1, 2 + fontrenderer.getStringWidth(msg) + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
                fontrenderer.drawString(msg, 2, top, 14737632);
                top += fontrenderer.FONT_HEIGHT;
            }

            top = 2;
            for (String msg : listR)
            {
                if (msg == null) continue;
                int w = fontrenderer.getStringWidth(msg);
                int left = width - 2 - w;
                drawRect(left - 1, top - 1, left + w + 1, top + fontrenderer.FONT_HEIGHT - 1, -1873784752);
                fontrenderer.drawString(msg, left, top, 14737632);
                top += fontrenderer.FONT_HEIGHT;
            }
        }

        mc.mcProfiler.endSection();
        post(TEXT);
    }

    //Helper macros
    private boolean pre(RenderGameOverlayEvent.ElementType type)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }
    private void post(RenderGameOverlayEvent.ElementType type)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }
    private void bind(ResourceLocation res)
    {
        mc.getTextureManager().bindTexture(res);
    }

    private class GuiOverlayDebugForge extends GuiOverlayDebug
    {
        private GuiOverlayDebugForge(Minecraft mc){ super(mc); }
        @Override protected void renderDebugInfoLeft(){}
        @Override protected void renderDebugInfoRight(ScaledResolution res){}
        private List<String> getLeft(){ return this.call(); }
        private List<String> getRight(){ return this.getDebugInfoRight(); }
    }

}
