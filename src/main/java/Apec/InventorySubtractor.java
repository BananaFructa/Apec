package Apec;

import Apec.Components.Gui.ContainerGuis.AuctionHouseGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class InventorySubtractor {

    private Minecraft mc = Minecraft.getMinecraft();
    private InventoryPlayer lastPlayerInv = new InventoryPlayer(null);
    private ItemStack lastItemStack;

    public ArrayList<SubtractionListElem> subtractionListElems = new ArrayList<SubtractionListElem>();

    @SubscribeEvent
    public void onWorldTickEvent(TickEvent.ClientTickEvent event) {

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
                        System.out.println("A");
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

    private void addItemToHashMap(ItemStack is, HashMap<String ,Integer> hm) {
        if (!hm.containsKey(is.getDisplayName())) {
            hm.put(is.getDisplayName(), is.stackSize);
        } else {
            hm.put(is.getDisplayName(), is.stackSize + hm.get(is.getDisplayName()));
        }
    }
}
