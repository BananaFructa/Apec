/**
 * Detects changes in the player inventory
 */
package Apec.DataInterpretation;

import Apec.Components.Gui.ContainerGuis.AuctionHouse.AuctionHouseGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class InventorySubtractor {

    private Minecraft mc = Minecraft.getMinecraft();

    /** The player inventory in the previous tick */
    private InventoryPlayer lastPlayerInv = new InventoryPlayer(null);

    /** The item stack held in the cursor in the last tick */
    private ItemStack lastItemStack;

    public ArrayList<SubtractionListElem> subtractionListElems = new ArrayList<SubtractionListElem>();

    /**
     * @brief Main logic for detecting changes in the inventory
     */
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        ArrayList<SubtractionListElem> elemtsToRemove = new ArrayList<SubtractionListElem>();
        for (SubtractionListElem sle : subtractionListElems) {
            sle.lifetme--;
            if (sle.lifetme == 0) elemtsToRemove.add(sle);
        }

        for (SubtractionListElem sle : elemtsToRemove) subtractionListElems.remove(sle);

        if (mc.thePlayer != null && !(mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof AuctionHouseGui)) {

            InventoryPlayer currentPlayerInv = mc.thePlayer.inventory;

            HashMap<String, Integer> lastHashMap = new HashMap<String, Integer>();
            HashMap<String, Integer> currentHashMap = new HashMap<String, Integer>();

            for (int i = 0; i < 36; i++) {

                ItemStack is = lastPlayerInv.mainInventory[i];
                if (is != null) addItemToHashMap(is,lastHashMap);

                ItemStack is2 = currentPlayerInv.mainInventory[i];
                if (is2 != null) addItemToHashMap(is2,currentHashMap);

            }

            if (lastItemStack != null) addItemToHashMap(lastItemStack,lastHashMap);
            if (currentPlayerInv.getItemStack() != null) addItemToHashMap(currentPlayerInv.getItemStack(),currentHashMap);

            for (String k : lastHashMap.keySet()) {
                if (currentHashMap.containsKey(k)) {
                    if (currentHashMap.get(k).intValue() != lastHashMap.get(k).intValue()) {
                        addToList(new SubtractionListElem(k,currentHashMap.get(k) - lastHashMap.get(k)));
                    }
                } else {
                    addToList(new SubtractionListElem(k,-lastHashMap.get(k)));
                }
            }

            for (String k : currentHashMap.keySet()) {
                if (!lastHashMap.containsKey(k)) {
                    addToList(new SubtractionListElem(k,currentHashMap.get(k)));
                }
            }

            lastPlayerInv.copyInventory(currentPlayerInv);
            if (currentPlayerInv.getItemStack() != null) {
                lastItemStack = currentPlayerInv.getItemStack().copy();
            } else {
                lastItemStack = null;
            }
        }
    }

    /**
     * @brief Adds a subtraction element to the history list
     */
    private void addToList(SubtractionListElem sle) {
        for (SubtractionListElem _sle : subtractionListElems) {
            if (_sle.text.equals(sle.text)) {
                _sle.quant += sle.quant;
                _sle.lifetme = 200;
                return;
            }
        }
        subtractionListElems.add(sle);
    }

    /**
     * @brief Adds an item to the hash map, if the item already exists it adds the quantity
     * @param is = Items stack to be added
     * @param hm = The hashpmap in which the quatity has to be added
     */
    private void addItemToHashMap(ItemStack is, HashMap<String ,Integer> hm) {
        if (!hm.containsKey(is.getDisplayName())) {
            hm.put(is.getDisplayName(), is.stackSize);
        } else {
            hm.put(is.getDisplayName(), is.stackSize + hm.get(is.getDisplayName()));
        }
    }
}
