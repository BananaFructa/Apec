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
            String line = ApecUtils.removeAllCodes(toolTip.get(i));
            for (ItemRarity rarity : ItemRarity.values()) {
                if (
                    (rarity == COMMON && line.contains("UNCOMMON")) ||
                    (rarity == SPECIAL && line.contains("VERY SPECIAL"))
                ) continue;
                if (line.contains(rarity.name().replace("_"," "))) return rarity;
            }
        }
        return null;
    }
}
