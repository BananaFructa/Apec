package Apec;

import net.minecraft.client.Minecraft;

public class ApecUtils {

    public static int colorCodeToHex (char c) {
        return Minecraft.getMinecraft().fontRendererObj.getColorCode(c);
    }

}
