package Apec;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;

public class ApecUtils {

    private static String colorCodes[] = { "\u00a70","\u00a71","\u00a72","\u00a73","\u00a74","\u00a75","\u00a76","\u00a77","\u00a78","\u00a79","\u00a7a","\u00a7b","\u00a7c","\u00a7d","\u00a7e","\u00a7f" };

    public static int colorCodeToHex (char c) {
        return Minecraft.getMinecraft().fontRendererObj.getColorCode(c);
    }

    public static HashMap<String,String> unObfedFieldNames = new HashMap() {{
        put("footer","field_175255_h");
        put("header","field_175256_i");
    }};

    public static String removeAllCodes(String s) {
        while (s.contains("\u00a7")) {
            s = s.replace("\u00a7"+s.charAt(s.indexOf("\u00a7") + 1),"");
        }
        return s;
    }



    public static String removeColorCodes(String s) {
        for (String code : colorCodes) {
            s = s.replace(code,"\u00a7r");
            s = s.replace(code.toUpperCase(),"\u00a7r");
        }
        return s;
    }

    public static Vector2f addVec (Vector2f a,Vector2f b) {
        return new Vector2f(a.x+b.x,a.y+b.y);
    }

    public static String reverseString (String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb = sb.reverse();
        return sb.toString();
    }

    public static void showMessage(String string) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(string));
    }

}
