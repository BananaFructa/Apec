package Apec.Components.Gui.ContainerGuis;

import Apec.Component;
import Apec.ComponentId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import scala.collection.parallel.ParIterableLike;

public class AuctionHouseComponent extends Component {

    public boolean guiIsOpened = false;

    public CategoryID currentCategory = CategoryID.WEAPONS;

    boolean isInOtherMenu = false;

    public enum CategoryID {
        WEAPONS,
        ARMOUR,
        ACCESSORIES,
        CONSUMABLES,
        BLOCKS,
        OTHER
    }

    public enum Actions {
        SEARCH,
        NEXT,
        BACK,
        RARITY_CHANGE,
        SORT_CHANGE,
        MOD_CHANGE,
        CLOSE

    }

    public AuctionHouseComponent () {
        super(ComponentId.AUCTION_HOUSE_MENU);
    }

    public void OpenTheGui(IInventory upper, IInventory lower, GuiOpenEvent event) {
        isInOtherMenu = lower.getDisplayName().getUnformattedText().contains("View") || lower.getDisplayName().getUnformattedText().contains("Confirm");
        if(lower.getDisplayName().getUnformattedText().equals("Auctions Browser")) {
                event.setCanceled(true);
            Minecraft.getMinecraft().displayGuiScreen(new AuctionHouseGui(upper, lower));
            guiIsOpened = true;
        }

    }

    boolean last = false, _last = false;
    int tickCounter = 0;

    public boolean isInMenu() {
        GuiScreen g = Minecraft.getMinecraft().currentScreen;
        return g instanceof AuctionHouseGui || g instanceof GuiEditSign || (isInOtherMenu && g instanceof GuiChest);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (isInMenu()) _last = true;
        if (!isInMenu() && tickCounter == 0 &&  _last) last = true;
        if (last) tickCounter++;
        if (tickCounter == 20) {
            if (!isInMenu()) {
                currentCategory = CategoryID.WEAPONS;
            }
            tickCounter = 0;
            last = false;
            _last = false;
        }
    }

}
