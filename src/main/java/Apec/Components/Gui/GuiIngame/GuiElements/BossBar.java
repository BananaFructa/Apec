package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.DataInterpretation.DataExtractor;
import Apec.Utils.ApecUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.boss.BossStatus;
import org.lwjgl.util.vector.Vector2f;

public class BossBar extends GUIComponent {

    public BossBar() {
        super(GUIComponentID.BOSS_HEALTH);
        this.scalable = false;
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd, DataExtractor.OtherData od, ScaledResolution sr, boolean editingMode) {
        super.drawTex(ps, sd, od, sr, editingMode);
        if (editingMode) {
            if (BossStatus.bossName == null) {
                BossStatus.bossName = "Something";
                BossStatus.healthScale = 1;
            }
        }
        if (editingMode) {
            BossStatus.statusBarTime = 1;
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(g_sr.getScaledWidth()/2f-183f/2f,1);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getCurrentAnchorPoint(),new Vector2f(183,13));
    }
}
