package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.*;
import Apec.Components.Gui.GuiIngame.GUIComponentID;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class InventoryTraffic extends GUIComponent {

    public InventoryTraffic() {
        super(GUIComponentID.INV_TRAFFIC);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr,boolean editingMode) {
        super.draw(ps,sd,od,sr,editingMode);
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale,scale,scale);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.INVENTORY_TRAFFIC)) {
            Vector2f subtrListPos = this.getAnchorPointPosition();

            subtrListPos = ApecUtils.addVec(subtrListPos, delta_position);

            ArrayList<SubtractionListElem> sles = ApecMain.Instance.inventorySubtractor.subtractionListElems;

            if (editingMode && sles.isEmpty()) {
                final SubtractionListElem elem = new SubtractionListElem("Something",1);
                sles = new ArrayList<SubtractionListElem>() {{
                    add(elem);
                    add(elem);
                    add(elem);
                    add(elem);
                    add(elem);
                    add(elem);
                    add(elem);
                }};
            }

            if (!sles.isEmpty()) {
                int _i = 0;
                for (int i = sles.size() - 1; i > sles.size() - 8 - _i && i > -1; i--) {
                    float alpha = 0xff;
                    if (sles.get(i).lifetme <= 50) alpha *= sles.get(i).lifetme / 50f;
                    int xToStart = 0;
                    if (sles.get(i).quant == 0 || sles.get(i).text.contains("\u00a78") || sles.get(i).text.contains("\u00a77")) {
                        _i++;
                    } else if (sles.get(i).quant < 0) {
                        ApecUtils.drawThiccBorderString("-" + Math.abs(sles.get(i).quant), (int)(subtrListPos.x/scale), (int)(subtrListPos.y/scale + (sles.size() - 1 - i - _i) * 11), 0xd10404 | ((int) alpha << 24));
                        xToStart = mc.fontRendererObj.getStringWidth("-" + Math.abs(sles.get(i).quant));
                    } else {
                        ApecUtils.drawThiccBorderString("+" + Math.abs(sles.get(i).quant), (int)(subtrListPos.x/scale), (int)( subtrListPos.y/scale + (sles.size() - 1 - i - _i) * 11), 0x0ccc39 | ((int) alpha << 24));
                        xToStart = mc.fontRendererObj.getStringWidth("+" + Math.abs(sles.get(i).quant));
                    }
                    if (sles.get(i).quant != 0 && !sles.get(i).text.contains("\u00a78") && !sles.get(i).text.contains("\u00a77"))
                        ApecUtils.drawThiccBorderString(" " + sles.get(i).text, (int)(subtrListPos.x/scale + xToStart), (int)( subtrListPos.y/scale + (sles.size() - 1 - i - _i) * 11), 0xffffff | ((int) alpha << 24));

                }
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return new Vector2f(5, 5);
    }

    @Override
    public Vector2f getBoundingPoint() {
        return ApecUtils.addVec(getRealAnchorPoint(),new Vector2f(65*scale,75*scale));
    }
}
