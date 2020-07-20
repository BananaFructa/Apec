package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import Apec.InventorySubtractor;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class InventoryTraffic extends GUIComponent {

    public InventoryTraffic() {
        super(GUIModifier.GUiComponentID.INV_SUBTRACT);
    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.INVENTORY_TRAFFIC)) {
            Vector2f subtrListPos = new Vector2f(5, 5);

            subtrListPos = ApecUtils.addVec(subtrListPos, delta_position);

            ArrayList<InventorySubtractor.SubtractionListElem> sles = ApecMain.Instance.inventorySubtractor.subtractionListElems;

            if (!sles.isEmpty()) {
                int _i = 0;
                for (int i = sles.size() - 1; i > sles.size() - 8 - _i && i > -1; i--) {
                    float alpha = 0xff;
                    if (sles.get(i).lifetme <= 50) alpha *= sles.get(i).lifetme / 50f;
                    int xToStart = 0;
                    if (sles.get(i).quant == 0 || sles.get(i).text.contains("\u00a78") || sles.get(i).text.contains("\u00a77")) {
                        _i++;
                    } else if (sles.get(i).quant < 0) {
                        ApecUtils.drawThiccBorderString("-" + Math.abs(sles.get(i).quant), (int) subtrListPos.x, (int) subtrListPos.y + (sles.size() - 1 - i - _i) * 11, 0xd10404 | ((int) alpha << 24));
                        xToStart = mc.fontRendererObj.getStringWidth("-" + Math.abs(sles.get(i).quant));
                    } else {
                        ApecUtils.drawThiccBorderString("+" + Math.abs(sles.get(i).quant), (int) subtrListPos.x, (int) subtrListPos.y + (sles.size() - 1 - i - _i) * 11, 0x0ccc39 | ((int) alpha << 24));
                        xToStart = mc.fontRendererObj.getStringWidth("+" + Math.abs(sles.get(i).quant));
                    }
                    if (sles.get(i).quant != 0 && !sles.get(i).text.contains("\u00a78") && !sles.get(i).text.contains("\u00a77"))
                        ApecUtils.drawThiccBorderString(" " + sles.get(i).text, (int) subtrListPos.x + xToStart, (int) subtrListPos.y + (sles.size() - 1 - i - _i) * 11, 0xffffff | ((int) alpha << 24));

                }
            }
        }
    }
}
