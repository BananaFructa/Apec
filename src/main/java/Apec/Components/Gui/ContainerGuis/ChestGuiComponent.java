package Apec.Components.Gui.ContainerGuis;

import Apec.Component;
import Apec.ComponentId;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;

/**
 * Base component class for all the gui screen components(ex AH menu, skill menu)
 */

public class ChestGuiComponent extends Component {

    public ChestGuiComponent(ComponentId componentId) {
        super(componentId);
    }

    public void OpenGui(IInventory upper, IInventory lower, GuiOpenEvent event) {

    }

}
