package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Vector2f;

public class AbilityText extends GUIComponent {

    private MpText mpText;
    private Vector2f AnchorPosition = new Vector2f(0,0);
    private int stringWidth = 0;

    public AbilityText() {
        super(GUIComponentID.ABILITY_TEXT);
    }

    @Override
    public void init() {
        mpText = (MpText) GUIModifier.Instance.getGuiComponent(GUIComponentID.MP_TEXT);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.draw(ps, sd, od, sr, editingMode);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        AnchorPosition.y = mpText.getAnchorPointPosition().y;
        if (mpText.getDeltaPosition().length() == 0 && this.getDeltaPosition().length() == 0 && ApecMain.Instance.settingsManager.getSettingState(SettingID.MP_TEXT)) {
            AnchorPosition.x = mpText.getBoundingPoint().x - 5;
        } else {
            AnchorPosition.x = mpText.getAnchorPointPosition().x;
        }
        if ((ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_ABILITY_TEXT) && ps.IsAbilityShown) || editingMode) {
            stringWidth = mc.fontRendererObj.getStringWidth(ps.AbilityText);
            Vector2f rap = ApecUtils.scalarMultiply(getCurrentAnchorPoint(),oneOverScale);
            ApecUtils.drawThiccBorderString(
                    ps.AbilityText,
                    (int)(rap.x - mc.fontRendererObj.getStringWidth(ps.AbilityText)),
                    (int)(rap.y - 10),
                    0xffffffff
            );
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return AnchorPosition;
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getCurrentAnchorPoint(),new Vector2f(-stringWidth*scale,-11*scale));
    }
}
