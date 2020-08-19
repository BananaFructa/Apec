package Apec.Components.Gui.ContainerGuis;

import Apec.Component;
import Apec.ComponentId;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;

public class ChestGuiComponent extends Component {

    public enum Actions {
        PAGE_CHANGE,
        BACK,
        CLOSE
    }

    public ChestGuiComponent(ComponentId componentId) {
        super(componentId);
    }

    public void OpenGui(IInventory upper, IInventory lower, GuiOpenEvent event) {

    }

}
