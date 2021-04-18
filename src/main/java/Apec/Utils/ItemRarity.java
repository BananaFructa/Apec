package Apec.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.List;

public enum ItemRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHIC,
    SUPREME,
    SPECIAL,
    VERY_SPECIAL;

    public static ItemRarity getRarity(Minecraft mc,ItemStack itemStack) {
        List<String> toolTip = itemStack.getTooltip(mc.thePlayer,false);
        for (int i = toolTip.size() - 1;i > -1;i--) {
            for (ItemRarity rarity : ItemRarity.values()) {
                if (ApecUtils.containedByCharSequence(toolTip.get(i),rarity.name().replace("_"," "))) return rarity;
            }
        }
        return null;
    }
}
