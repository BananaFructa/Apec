package Apec;

import Apec.Components.Gui.ContainerGuis.AuctionHouseComponent;
import Apec.Components.Gui.ContainerGuis.ChestGuiComponent;
import Apec.Components.Gui.ContainerGuis.SkillViewComponent;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.ArrayList;
import java.util.List;

public class ContainerGuiManager {

    public List<ChestGuiComponent> guiMenuComponents =  new ArrayList<ChestGuiComponent>() {{

        //  add(new SkillViewComponent());
    }};

    public void init() {
        guiMenuComponents.add((ChestGuiComponent) ApecMain.Instance.getComponent(ComponentId.AUCTION_HOUSE_MENU));
        guiMenuComponents.add((ChestGuiComponent) ApecMain.Instance.getComponent(ComponentId.SKILL_VIEW_MENU));
        guiMenuComponents.add((ChestGuiComponent) ApecMain.Instance.getComponent(ComponentId.TRANSPARENT_ACTIVE_EFFECTS_MENU));
    }

    @SubscribeEvent
    public void onGui (GuiOpenEvent event) {
        if (ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER).getEnableState() && event.gui instanceof GuiChest) {
            Tuple<IInventory,IInventory> UpperLower = ApecUtils.GetUpperLowerFromGuiEvent(event);
            if (UpperLower == null) return;
            for (ChestGuiComponent guiMenuComponent : guiMenuComponents) {
                guiMenuComponent.OpenGui(UpperLower.getFirst(), UpperLower.getSecond(), event);
            }
        }
    }

}
