package Apec.Utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

/** Separate way of getting the display names of items as the normal method might be modified by other mods */
public class ItemNameFetcher {

    private static final HashMap<ItemStack,String> nameCache = new HashMap<ItemStack, String>();

    public static String getDisplayName (ItemStack itemStack) {
        if (nameCache.containsKey(itemStack)) return nameCache.get(itemStack);

        String s = itemStack.getItem().getItemStackDisplayName(itemStack);

        NBTTagCompound stackTagCompound = ApecUtils.readDeclaredField(ItemStack.class,itemStack,"stackTagCompound");

        if (stackTagCompound != null && stackTagCompound.hasKey("display", 10)) {
            NBTTagCompound nbtTagCompound = stackTagCompound.getCompoundTag("display");

            if (nbtTagCompound.hasKey("Name",8)) {
                s = nbtTagCompound.getString("Name");
            }

        }

        if (nameCache.size() > 50) {
            nameCache.remove((ItemStack) nameCache.keySet().toArray()[0]);
        }
        nameCache.put(itemStack,s);

        return s;

    }

}
