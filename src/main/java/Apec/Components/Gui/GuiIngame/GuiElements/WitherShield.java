package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import Apec.Utils.ApecUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.util.vector.Vector2f;

import java.text.DecimalFormat;

import static net.minecraft.nbt.NBTBase.NBT_TYPES;

public class WitherShield extends GUIComponent {

    private MpText mpText;
    private Vector2f AnchorPosition = new Vector2f(g_sr.getScaledWidth() - 8, 132);
    private int stringWidth = 0;
    private long nextShield = 1;
    private long nextShieldInSeconds = 1;
    private DecimalFormat df = new DecimalFormat("#.000");

    public WitherShield() {
        super(GUIComponentID.WITHER_SHIELD);
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent event) {
        long curTime = System.currentTimeMillis();
        if(ApecMain.Instance.settingsManager.getSettingState(SettingID.WITHER_SHIELD) && (mc.theWorld.getWorldTime() > nextShield || curTime > nextShieldInSeconds)){
            if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
                if(event.entityPlayer != null){
                    ItemStack heldItem = event.entityPlayer.getHeldItem();
                    if(heldItem != null){
                        if(heldItem.hasTagCompound() && heldItem.getTagCompound().hasKey("ExtraAttributes")) {
                            NBTTagCompound nbtTagCompound = heldItem.getTagCompound().getCompoundTag("ExtraAttributes");
                            NBTTagList nbtTagList = nbtTagCompound.getTagList("ability_scroll", Constants.NBT.TAG_STRING);
                            for (int i = 0; i < nbtTagList.tagCount(); i++) {
                                if (nbtTagList.getStringTagAt(i).equals("WITHER_SHIELD_SCROLL")) {
                                    nextShield = mc.theWorld.getWorldTime() + (5 * 20);
                                    nextShieldInSeconds = curTime + 5500;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, ts, sr, editingMode);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);

        if ((ApecMain.Instance.settingsManager.getSettingState(SettingID.WITHER_SHIELD) && nextShield > 0) || editingMode) {
            double time = (double) (nextShield - mc.theWorld.getWorldTime()) / 20;

            String shieldText = time < 0 ? "Shield: Ready" : "Shield: " + df.format(time) + "s";
            stringWidth = mc.fontRendererObj.getStringWidth(shieldText);

            Vector2f StatBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),oneOverScale);

            ApecUtils.drawStylizedString(
                    shieldText,
                    (int)(StatBar.x - stringWidth),
                    (int)(StatBar.y - 10),
                    time < 0 ? 0x00FF00 : 0xFFFF00
            );
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        if(ApecMain.Instance.settingsManager.getSettingState(SettingID.CENTER_WITHER_SHIELD)){
            return this.guiModifier.applyGlobalChanges(this,new Vector2f((int) (AnchorPosition.x + (stringWidth * 0.5f)), AnchorPosition.y));
        }
        return this.guiModifier.applyGlobalChanges(this,AnchorPosition);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-stringWidth*scale,-11*scale);
    }
}
