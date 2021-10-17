package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Events.ApecSettingChangedState;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Vector2f;

public class DefenceText extends GUIComponent {

    private int StringWidth = 0;

   public DefenceText () {
       super(GUIComponentID.DEFENCE_TEXT);
   }

    @Override
    public void init() {
        super.init();
        this.enabled = ApecMain.Instance.settingsManager.getSettingState(SettingID.USE_DEFENCE_OUT_OF_BB);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr, boolean editingMode) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);

        Vector2f Pos = ApecUtils.scalarMultiply(this.getCurrentAnchorPoint(), oneOverScale);

        String s = "\u00a7aDefence " + ps.Defence;

        StringWidth = mc.fontRendererObj.getStringWidth(s);

        ApecUtils.drawStylizedString("\u00a7aDefence " + ps.Defence, (int) (Pos.x) + 1, (int) (Pos.y) + 1, 0xffffffff);

        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onSettingChanged(ApecSettingChangedState event) {
        if (event.settingID == SettingID.USE_DEFENCE_OUT_OF_BB) {
            this.enabled = event.state;
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth()/2f, g_sr.getScaledHeight()/2f);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(StringWidth*scale,11*scale);
    }

}
