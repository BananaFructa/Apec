/**
 * Manages custom guis
 */
package Apec.DataInterpretation;

import Apec.ApecMain;
import Apec.Utils.ApecUtils;
import Apec.ComponentId;
import Apec.Components.Gui.ContainerGuis.ChestGuiComponent;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ContainerGuiManager {

    public List<ChestGuiComponent> guiMenuComponents =  new ArrayList<ChestGuiComponent>();

    public void init() {
        guiMenuComponents.add((ChestGuiComponent) ApecMain.Instance.getComponent(ComponentId.AUCTION_HOUSE_MENU));
        guiMenuComponents.add((ChestGuiComponent) ApecMain.Instance.getComponent(ComponentId.SKILL_VIEW_MENU));
        guiMenuComponents.add((ChestGuiComponent) ApecMain.Instance.getComponent(ComponentId.TRANSPARENT_ACTIVE_EFFECTS_MENU));
    }

    @SubscribeEvent
    public void onGui (GuiOpenEvent event) {
        if ((ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER).getEnableState() || ApecMain.Instance.settingsManager.getSettingState(SettingID.GUIS_WHEN_DISABLED)) && event.gui instanceof GuiChest) {
            Tuple<IInventory,IInventory> UpperLower = ApecUtils.GetUpperLowerFromGuiEvent(event);
            if (UpperLower == null) return;
            for (ChestGuiComponent guiMenuComponent : guiMenuComponents) {
                guiMenuComponent.OpenGui(UpperLower.getFirst(), UpperLower.getSecond(), event);
            }
        }
    }

}
