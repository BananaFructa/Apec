package Apec.Components.Gui.GuiIngame;

import Apec.*;
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

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTHMOUNT;


/**
 * The scoreboard and the auction bar has been disabled, all of their data is being processed and displayed in a different manner
 * This is a modified copy of the original GuiIngameForge.class file , used to make other mods compatible
 * And this kids is the story of how banana gone insane
 */

public class ApecGuiIngameForge extends GuiIngameForge {

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

    //Flags to toggle the rendering of certain aspects of the HUD, valid conditions
    //must be met for them to render normally. If those conditions are met, but this flag
    //is false, they will not be rendered.
    public static boolean renderHelmet = true;
    public static boolean renderPortal = true;
    public static boolean renderHotbar = true;
    public static boolean renderCrosshairs = true;
    public static boolean renderBossHealth = true;
    public static boolean renderHealth = true;
    public static boolean renderArmor = true;
    public static boolean renderFood = true;
    public static boolean renderHealthMount = true;
    public static boolean renderAir = true;
    public static boolean renderExperiance = true;
    public static boolean renderJumpBar = true;
    public static boolean renderObjective = true;

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
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        renderJumpBar = mc.thePlayer.isRidingHorse();

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

        if (renderHelmet) renderHelmet(res, partialTicks);

        if (renderPortal && !mc.thePlayer.isPotionActive(Potion.confusion))
        {
            renderPortal(res, partialTicks);
        }

        renderTooltip(res, partialTicks);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        zLevel = -90.0F;
        rand.setSeed((long)(updateCounter * 312871));

        if (renderCrosshairs) renderCrosshairs(width, height);
        if (renderBossHealth) renderBossHealth();

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
        renderHUDText(width, height);
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

        if (gUIModifier != null) gUIModifier.onRender(res);

        post(ALL);
    }


    protected void renderTooltip(ScaledResolution sr, float partialTicks)
    {
        if (pre(HOTBAR)) return;
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
            float f = this.zLevel;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(sr.getScaledWidth()-183, sr.getScaledHeight() - 43, 0, 0, 182, 22);
            this.drawTexturedModalRect(sr.getScaledWidth()-183- 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 43 - 1, 0, 22, 24, 22);

            GlStateManager.enableBlend();
            GlStateManager.enableRescaleNormal();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            this.zLevel = f;

            for (int j = 0; j < 9; ++j)
            {
                int k = sr.getScaledWidth() - 182 + j * 20 + 2;
                int l = sr.getScaledHeight() - 37 - 3;
                this.renderHotbarItem(j, k, l, partialTicks, entityplayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
        post(HOTBAR);
    }

    @Override
    protected void renderBossHealth()
    {
        if (pre(BOSSHEALTH)) return;
        bind(Gui.icons);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        mc.mcProfiler.startSection("bossHealth");
        GlStateManager.enableBlend();
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0)
        {
            --BossStatus.statusBarTime;
            FontRenderer fontrenderer = this.mc.fontRendererObj;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaledWidth();
            int j = 182;
            int k = i / 2 - j / 2;
            int l = (int)(BossStatus.healthScale * (float)(j + 1));
            int i1 = 12;
            this.drawTexturedModalRect(k, i1, 0, 74, j, 5);
            this.drawTexturedModalRect(k, i1, 0, 74, j, 5);

            if (l > 0)
            {
                this.drawTexturedModalRect(k, i1, 0, 79, l, 5);
            }

            String s = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(s, (float)(i / 2 - this.getFontRenderer().getStringWidth(s) / 2), (float)(i1 - 10), 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }
        GlStateManager.disableBlend();
        mc.mcProfiler.endSection();
        post(BOSSHEALTH);
    }

    public ScaledResolution getResolution()
    {
        return res;
    }

    protected void renderCrosshairs(int width, int height)
    {
        if (pre(CROSSHAIRS)) return;
        if (this.showCrosshair())
        {
            bind(Gui.icons);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
            GlStateManager.enableAlpha();
            drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GlStateManager.disableBlend();
        }
        post(CROSSHAIRS);
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

    protected void renderPortal(ScaledResolution res, float partialTicks)
    {
        if (pre(PORTAL)) return;

        float f1 = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * partialTicks;

        if (f1 > 0.0F)
        {
            renderPortal(f1, res);
        }

        post(PORTAL);
    }

    protected void renderSleepFade(int width, int height)
    {
        if (mc.thePlayer.getSleepTimer() > 0)
        {
            mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int sleepTime = mc.thePlayer.getSleepTimer();
            float opacity = (float)sleepTime / 100.0F;

            if (opacity > 1.0F)
            {
                opacity = 1.0F - (float)(sleepTime - 100) / 10.0F;
            }

            int color = (int)(220.0F * opacity) << 24 | 1052704;
            drawRect(0, 0, width, height, color);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            mc.mcProfiler.endSection();
        }
    }

    protected void renderJumpBar(int width, int height)
    {
        bind(icons);
        if (pre(JUMPBAR)) return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();

        mc.mcProfiler.startSection("jumpBar");
        float charge = mc.thePlayer.getHorseJumpPower();
        final int barWidth = 182;
        int x = (width / 2) - (barWidth / 2);
        int filled = (int)(charge * (float)(barWidth + 1));
        int top = height - 32 + 3;

        drawTexturedModalRect(x, top, 0, 84, barWidth, 5);

        if (filled > 0)
        {
            this.drawTexturedModalRect(x, top, 0, 89, filled, 5);
        }

        GlStateManager.enableBlend();
        mc.mcProfiler.endSection();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        post(JUMPBAR);
    }

    protected void renderToolHightlight(ScaledResolution res)
    {
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator())
        {
            mc.mcProfiler.startSection("toolHighlight");

            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
            {
                String name = this.highlightingItemStack.getDisplayName();
                if (this.highlightingItemStack.hasDisplayName())
                    name = EnumChatFormatting.ITALIC + name;

                name = this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, name);

                int opacity = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255) opacity = 255;

                if (opacity > 0)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                    FontRenderer font = highlightingItemStack.getItem().getFontRenderer(highlightingItemStack);
                    int x = res.getScaledWidth() - 182;
                    int y = res.getScaledHeight() - 53;
                    fontrenderer.drawStringWithShadow(name, x, y, WHITE | (opacity << 24));
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

    protected void renderTitle(int width, int height, float partialTicks)
    {
        if (field_175195_w > 0)
        {
            mc.mcProfiler.startSection("titleAndSubtitle");
            float age = (float)this.field_175195_w - partialTicks;
            int opacity = 255;

            if (field_175195_w > field_175193_B + field_175192_A)
            {
                float f3 = (float)(field_175199_z + field_175192_A + field_175193_B) - age;
                opacity = (int)(f3 * 255.0F / (float)field_175199_z);
            }
            if (field_175195_w <= field_175193_B) opacity = (int)(age * 255.0F / (float)this.field_175193_B);

            opacity = MathHelper.clamp_int(opacity, 0, 255);

            if (opacity > 8)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(width / 2), (float)(height / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int l = opacity << 24 & -16777216;
                this.getFontRenderer().drawString(this.field_175201_x, (float)(-this.getFontRenderer().getStringWidth(this.field_175201_x) / 2), -10.0F, 16777215 | l, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                this.getFontRenderer().drawString(this.field_175200_y, (float)(-this.getFontRenderer().getStringWidth(this.field_175200_y) / 2), 5.0F, 16777215 | l, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }
    }

    protected void renderChat(int width, int height)
    {
        mc.mcProfiler.startSection("chat");

        RenderGameOverlayEvent.Chat event = new RenderGameOverlayEvent.Chat(eventParent, 0, height - 48);
        if (MinecraftForge.EVENT_BUS.post(event)) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)event.posX, (float)event.posY, 0.0F);
        persistantChatGUI.drawChat(updateCounter);
        GlStateManager.popMatrix();

        post(CHAT);

        mc.mcProfiler.endSection();
    }

    protected void renderPlayerList(int width, int height)
    {
        ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = mc.thePlayer.sendQueue;

        if (mc.gameSettings.keyBindPlayerList.isKeyDown() && (!mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null))
        {
            this.overlayPlayerList.updatePlayerList(true);
            if (pre(PLAYER_LIST)) return;
            this.overlayPlayerList.renderPlayerlist(width, this.mc.theWorld.getScoreboard(), scoreobjective);
            post(PLAYER_LIST);
        }
        else
        {
            this.overlayPlayerList.updatePlayerList(false);
        }
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

    public void setChatData (List<ChatLine> messages) {
        //FieldUtils.writeDeclaredField(this.persistantChatGUI, "field_146253_i",messages, true);
        for (int i = messages.size() - 1;i > -1;i--) {
            this.persistantChatGUI.printChatMessage(messages.get(i).getChatComponent());
        }
    }

}
