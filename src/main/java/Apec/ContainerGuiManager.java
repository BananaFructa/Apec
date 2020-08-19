package Apec;

import Apec.Components.Gui.ContainerGuis.AuctionHouseComponent;
import Apec.Components.Gui.ContainerGuis.ChestGuiComponent;
import Apec.Components.Gui.ContainerGuis.SkillViewComponent;
import Apec.Settings.SettingID;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
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
    }

    @SubscribeEvent
    public void onGui (GuiOpenEvent event) {
        if (ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER).getEnableState() && event.gui instanceof GuiChest) {
            try {
                /** This is to ensure that there is not an Inner class of the GuiChes class forced by a mod , ughh ughh looking at you Skypixel */
                String upperFieldName = ApecUtils.unObfedFieldNames.get("upperChestInventory");
                String lowerFieldName = ApecUtils.unObfedFieldNames.get("lowerChestInventory");
                if (ApecUtils.isNameInFieldList(event.gui.getClass().getDeclaredFields(), upperFieldName) &&
                        ApecUtils.isNameInFieldList(event.gui.getClass().getDeclaredFields(), lowerFieldName)) {
                    IInventory upper = (IInventory) FieldUtils.readDeclaredField(event.gui, upperFieldName, true);
                    IInventory lower = (IInventory) FieldUtils.readDeclaredField(event.gui, lowerFieldName, true);
                    for (ChestGuiComponent guiMenuComponent : guiMenuComponents) {
                        guiMenuComponent.OpenGui(upper, lower, event);
                    }
                } else {
                    IInventory upper = (IInventory) FieldUtils.readField(event.gui, upperFieldName, true);
                    IInventory lower = (IInventory) FieldUtils.readField(event.gui, lowerFieldName, true);
                    for (ChestGuiComponent guiMenuComponent : guiMenuComponents) {
                        guiMenuComponent.OpenGui(upper, lower, event);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
