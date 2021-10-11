package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIComponent;
import Apec.DataInterpretation.DataExtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.lwjgl.util.vector.Vector2f;

public class EditGrid extends GUIComponent {

    public EditGrid () {
        super(GUIComponentID.EDIT_GRID);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, DataExtractor.TabStats ts, ScaledResolution sr,boolean editingMode) {
        super.drawTex(ps,sd,od,ts,sr,editingMode);
        GlStateManager.pushMatrix();
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.EDIT_GRID) & editingMode) {
            GuiIngame gi = Minecraft.getMinecraft().ingameGUI;
            int width = mc.displayWidth;
            int height = mc.displayHeight;
            int scalar = 15;

            for(int i=0; i < width; i = i + scalar){
                if(i==0){continue;}
                gi.drawRect(i,0,i + 1,height,0xC8C8C8C8);
            }
            for(int j=0; j < height; j = j + scalar){
                if(j==0){continue;}
                gi.drawRect(0,j,width,j + 1,0xC8C8C8C8);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(0.0f,0.0f);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(0.0f,0.0f);
    }

}
