package Apec.Components.Gui.ContainerGuis;

import Apec.ApecMain;
import Apec.ComponentId;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;

public class ActiveEffectsTransparentComponent extends ChestGuiComponent {

    public ActiveEffectsTransparentComponent() {
        super(ComponentId.TRANSPARENT_ACTIVE_EFFECTS_MENU);
    }

    public void OpenGui(IInventory upper, IInventory lower, GuiOpenEvent event) {
        if (lower.getDisplayName().getUnformattedText().contains("Active Effects") && ApecMain.Instance.dataExtractor.potionFetcher.NeedsInitialFetch && !(event.gui instanceof ActiveEffectsTransparentGui)) {
            event.setCanceled(true);
            Minecraft.getMinecraft().displayGuiScreen(new ActiveEffectsTransparentGui(upper, lower));
        }
    }

}
