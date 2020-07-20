package Apec.Components.Gui.GuiIngame;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.ComponentId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.List;


/**
 * The scoreboard and the auction bar has been disabled, all of their data is being processed and displayed in a different manner
 * This is a modified copy of the original GuiIngame.class file , used when the user wants the gui to overwrite any other custom gui
 */
public class ApecGuiIngameVanilla extends GuiIngame {
        GUIModifier gUIModifier;



        public ApecGuiIngameVanilla  (Minecraft mc) {
            super(mc);
            Object comp = ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER);
            if (comp != null) gUIModifier = (GUIModifier) comp;
        }

        public void renderGameOverlay(float partialTicks)
        {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);


            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            this.mc.entityRenderer.setupOverlayRendering();
            GlStateManager.enableBlend();

            if (Minecraft.isFancyGraphicsEnabled())
            {
                this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledresolution);
            }
            else
            {
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            }

            ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);

            if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
            {
                this.renderPumpkinOverlay(scaledresolution);
            }

            if (!this.mc.thePlayer.isPotionActive(Potion.confusion))
            {
                float f = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;

                if (f > 0.0F)
                {
                    this.renderPortal(f, scaledresolution);
                }
            }

            if (this.mc.playerController.isSpectator())
            {
                this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
            }
            else
            {
                this.renderTooltip(scaledresolution, partialTicks);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
            GlStateManager.enableBlend();

            if (this.showCrosshair())
            {
                GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
                GlStateManager.enableAlpha();
                this.drawTexturedModalRect(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
            }

            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            super.renderBossHealth();

            if (this.mc.playerController.shouldDrawHUD())
            {
                this.renderPlayerStats(scaledresolution);
            }

            GlStateManager.disableBlend();

            if (this.mc.thePlayer.getSleepTimer() > 0)
            {
                this.mc.mcProfiler.startSection("sleep");
                GlStateManager.disableDepth();
                GlStateManager.disableAlpha();
                int j1 = this.mc.thePlayer.getSleepTimer();
                float f1 = (float)j1 / 100.0F;

                if (f1 > 1.0F)
                {
                    f1 = 1.0F - (float)(j1 - 100) / 10.0F;
                }

                int k = (int)(220.0F * f1) << 24 | 1052704;
                drawRect(0, 0, i, j, k);
                GlStateManager.enableAlpha();
                GlStateManager.enableDepth();
                this.mc.mcProfiler.endSection();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = i / 2 - 91;

            if (this.mc.thePlayer.isRidingHorse())
            {
                this.renderHorseJumpBar(scaledresolution, k1);
            }

            if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator())
            {
                this.renderSelectedItem(scaledresolution);
            }
            else if (this.mc.thePlayer.isSpectator())
            {
                this.spectatorGui.func_175263_a(scaledresolution);
            }

            if (this.mc.isDemo())
            {
                this.renderDemo(scaledresolution);
            }

            if (this.mc.gameSettings.showDebugInfo)
            {
                this.overlayDebug.renderDebugInfo(scaledresolution);
            }

            if (this.field_175195_w > 0)
            {
                this.mc.mcProfiler.startSection("titleAndSubtitle");
                float f3 = (float)this.field_175195_w - partialTicks;
                int i2 = 255;

                if (this.field_175195_w > this.field_175193_B + this.field_175192_A)
                {
                    float f4 = (float)(this.field_175199_z + this.field_175192_A + this.field_175193_B) - f3;
                    i2 = (int)(f4 * 255.0F / (float)this.field_175199_z);
                }

                if (this.field_175195_w <= this.field_175193_B)
                {
                    i2 = (int)(f3 * 255.0F / (float)this.field_175193_B);
                }

                i2 = MathHelper.clamp_int(i2, 0, 255);

                if (i2 > 8)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)(i / 2), (float)(j / 2), 0.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(4.0F, 4.0F, 4.0F);
                    int j2 = i2 << 24 & -16777216;
                    this.getFontRenderer().drawString(this.field_175201_x, (float)(-this.getFontRenderer().getStringWidth(this.field_175201_x) / 2), -10.0F, 16777215 | j2, true);
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(2.0F, 2.0F, 2.0F);
                    this.getFontRenderer().drawString(this.field_175200_y, (float)(-this.getFontRenderer().getStringWidth(this.field_175200_y) / 2), 5.0F, 16777215 | j2, true);
                    GlStateManager.popMatrix();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }

                this.mc.mcProfiler.endSection();
            }

            Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
            ScoreObjective scoreobjective = null;
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());

            if (scoreplayerteam != null)
            {
                int i1 = scoreplayerteam.getChatFormat().getColorIndex();

                if (i1 >= 0)
                {
                    scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
                }
            }

            ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.disableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, (float)(j - 48), 0.0F);
            this.mc.mcProfiler.startSection("chat");
            this.persistantChatGUI.drawChat(this.updateCounter);
            this.mc.mcProfiler.endSection();
            GlStateManager.popMatrix();

            scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);


            if (!this.mc.gameSettings.keyBindPlayerList.isKeyDown() || this.mc.isIntegratedServerRunning() && this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && scoreobjective1 == null)
            {
                this.overlayPlayerList.updatePlayerList(false);
            }
            else
            {
                this.overlayPlayerList.updatePlayerList(true);
                this.overlayPlayerList.renderPlayerlist(i, scoreboard, scoreobjective1);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();

            if ( gUIModifier != null)  gUIModifier.onRender(scaledresolution);

        }

        protected void renderTooltip(ScaledResolution sr, float partialTicks)
        {
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
        }


        public void renderSelectedItem(ScaledResolution p_181551_1_)
        {
            this.mc.mcProfiler.startSection("selectedItemName");

            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
            {
                String s = this.highlightingItemStack.getDisplayName();

                if (this.highlightingItemStack.hasDisplayName())
                {
                    s = EnumChatFormatting.ITALIC + s;
                }

                int i = p_181551_1_.getScaledWidth() - 182;
                int j = p_181551_1_.getScaledHeight() - 53;

                int k = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);

                if (k > 255)
                {
                    k = 255;
                }

                if (k > 0)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    this.getFontRenderer().drawStringWithShadow(s, (float)i, (float)j, 16777215 + (k << 24));
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }

            this.mc.mcProfiler.endSection();
        }

        protected void renderPlayerStats(ScaledResolution p_180477_1_)
        {
            if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();

                int i = MathHelper.ceiling_float_int(entityplayer.getHealth());
                boolean flag = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;

                if (i < this.playerHealth && entityplayer.hurtResistantTime > 0)
                {
                    this.lastSystemTime = Minecraft.getSystemTime();
                    this.healthUpdateCounter = (long)(this.updateCounter + 20);
                }
                else if (i > this.playerHealth && entityplayer.hurtResistantTime > 0)
                {
                    this.lastSystemTime = Minecraft.getSystemTime();
                    this.healthUpdateCounter = (long)(this.updateCounter + 10);
                }

                if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L)
                {
                    this.playerHealth = i;
                    this.lastPlayerHealth = i;
                    this.lastSystemTime = Minecraft.getSystemTime();
                }

                this.playerHealth = i;
                this.rand.setSeed((long)(this.updateCounter * 312871));
            }
        }

        public void setChatData (List<ChatLine> messages) {
            //FieldUtils.writeDeclaredField(this.persistantChatGUI "field_146253_i",messages, true);
            for (int i = messages.size() - 1;i > -1;i--) {
                this.persistantChatGUI.printChatMessage(messages.get(i).getChatComponent());
            }
        }

}
