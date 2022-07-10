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
    SUPREME, // DIVINE
    SPECIAL,
    VERY_SPECIAL;

    public static ItemRarity getRarity(Minecraft mc,ItemStack itemStack) {
        List<String> toolTip = itemStack.getTooltip(mc.thePlayer,false);
        for (int i = toolTip.size() - 1;i > -1;i--) {
            String line = ApecUtils.removeAllCodes(toolTip.get(i));
            line = line.replace("\u00a7",""); // double checking
            for (ItemRarity rarity : ItemRarity.values()) {
                if (
                    (rarity == COMMON && line.contains("UNCOMMON")) ||
                    (rarity == SPECIAL && line.contains("VERY SPECIAL"))
                ) continue;
                if (rarity == SUPREME && line.contains("DIVINE")) return rarity; // Exception for divine rarity
                if (line.contains(rarity.name().replace("_"," "))) return rarity;
            }
        }
        return null;
    }
}
