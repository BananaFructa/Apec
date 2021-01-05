package Apec.Components.Gui.ContainerGuis.AuctionHouse;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Components.Gui.ContainerGuis.ChestGuiComponent;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;

public class AuctionHouseComponent extends ChestGuiComponent {

    public boolean guiIsOpened = false;

    public CategoryID currentCategory = CategoryID.WEAPONS;
    public String searchTerm = "";

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

    @Override
    public void init() {

    }

    public void OpenGui(IInventory upper, IInventory lower, GuiOpenEvent event) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.NPC_GUI)) {
            isInOtherMenu = lower.getDisplayName().getUnformattedText().contains("View") || lower.getDisplayName().getUnformattedText().contains("Confirm");
            if (lower.getDisplayName().getUnformattedText().startsWith("Auctions")) {
                if (lower.getDisplayName().getUnformattedText().contains("\"")) {
                    String[] tempSplit = lower.getDisplayName().getUnformattedText().split("\"");
                    searchTerm = tempSplit[1];
                } else {
                    searchTerm = "";
                }
                event.setCanceled(true);
                Minecraft.getMinecraft().displayGuiScreen(new AuctionHouseGui(upper, lower));
                guiIsOpened = true;
            }
        }
    }

    public void SetCategoryID(CategoryID categoryID) {
        currentCategory = categoryID;
    }

}
