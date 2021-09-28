package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataInterpretation.DataExtractor;
import Apec.Utils.ApecUtils;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

public class HorseJumpBar extends GUIComponent {

    public HorseJumpBar() {
        super(GUIComponentID.JUMP_BAR);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, ts, sr, editingMode);
        float charge = mc.thePlayer.getHorseJumpPower();

        if (editingMode && od.ArmadilloEnergy == 0) charge = 1f;

        if (charge > 0) {
            GuiIngame gui = mc.ingameGUI;

            Vector2f pos = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),this.oneOverScale);
            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId,"gui/statBars.png"));

            GlStateManager.scale(scale,scale,1);
            gui.drawTexturedModalRect(pos.x,pos.y,246,0,5,71);
            int height = (int)(71 * charge);
            gui.drawTexturedModalRect(pos.x,pos.y + (71 - height),251,71 - height,5,height);

        }

        if (od.ArmadilloEnergy != 0) {
            GuiIngame gui = mc.ingameGUI;

            Vector2f pos = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),this.oneOverScale);
            mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId,"gui/statBars.png"));

            GlStateManager.scale(scale,scale,1);
            gui.drawTexturedModalRect(pos.x,pos.y,246,0,5,71);
            int height = (int)(71 * ((float)od.ArmadilloEnergy / od.ArmadilloBaseEnergy));
            gui.drawTexturedModalRect(pos.x,pos.y + (71 - height),251,142 - height,5,height);
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return guiModifier.applyGlobalChanges(this,new Vector2f(g_sr.getScaledWidth() - 7,g_sr.getScaledHeight() - 124 + 20*(1 - ((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).getGuiComponent(GUIComponentID.INFO_BOX).getScale())));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(5 * scale, 71 * scale);
    }
}
