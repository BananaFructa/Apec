package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Components.Gui.GuiIngame.GuiElements.PowderInfoSubElements.PIGemstone;
import Apec.Components.Gui.GuiIngame.GuiElements.PowderInfoSubElements.PIMithril;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

public class PowderInfo extends GUIComponent {

    public PowderInfo () {
        super(GUIComponentID.POWDER_DISPLAY,2);
        addSubComponent(new PIMithril());
        addSubComponent(new PIGemstone());
        this.scalable = false;
    }

    int gemstoneStringWidth = 0, mithrilStringWidth = 0;

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr,boolean editingMode) {
        super.draw(ps,sd,od,ts,sr,editingMode);
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(0, 0);
    }

}
