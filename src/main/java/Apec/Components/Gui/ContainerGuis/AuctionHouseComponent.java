package Apec.Components.Gui.ContainerGuis;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Component;
import Apec.ComponentId;
import Apec.Settings.SettingID;
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

import java.util.HashMap;

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
        if (!DataToSave.isEmpty()) {
            currentCategory = CategoryID.values()[Integer.parseInt(DataToSave.get(0))];
        }
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
        DataToSave.put(0, String.valueOf(currentCategory.ordinal()));
        ApecMain.Instance.componentSaveManager.IssueSave();
    }

    //boolean last = false, _last = false;
    //int tickCounter = 0;

    /*public boolean isInMenu() {
        GuiScreen g = Minecraft.getMinecraft().currentScreen;
        return g instanceof AuctionHouseGui || g instanceof GuiEditSign || (isInOtherMenu && g instanceof GuiChest);
    }*/

    // I SWEAR TO FUCKING GOD I HAD TO SPEND SO MUCH ON TESTING THIS CRAP BECAUSE HYPIXEL WOULDN'T SAVE THE AH CATEGORY THEN THEY DECIDE
    // HUCYISCUIOGYFG WE SHOULD NOW MAKE IT SAVE NOW AND EVEN MORE WE SHOULD MAKE IT SAVE EVEN THO YOU FUCKING LEAVE THE SERVER
    /*@SubscribeEvent
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
    }*/

}
