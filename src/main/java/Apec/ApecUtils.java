package Apec;

import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.util.vector.Vector2f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApecUtils {

    /** If you are in debugging mode set this variable to true */
    public static boolean inFMLDebugFramework = false;

    private static String[] colorCodes = { "\u00a70","\u00a71","\u00a72","\u00a73","\u00a74","\u00a75","\u00a76","\u00a77","\u00a78","\u00a79","\u00a7a","\u00a7b","\u00a7c","\u00a7d","\u00a7e","\u00a7f" };

    public static HashMap<String,String> unObfedFieldNames = new HashMap<String,String>() {{
        put("footer","field_175255_h");
        put("header","field_175256_i");
        put("upperChestInventory",inFMLDebugFramework ? "upperChestInventory" : "field_147016_v");
        put("lowerChestInventory",inFMLDebugFramework ? "lowerChestInventory" : "field_147015_w");
        put("persistantChatGUI",inFMLDebugFramework ? "persistantChatGUI" : "field_73840_e");
        put("chatMessages","field_146253_i");
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

    public static void showMessage(String string) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_DEBUG_MESSAGES))
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(string));
    }

    /**
     * @brief This is made since there is this weird character in the purse text that im too lazy to see what unicode it has so now we have this
     * @return Returns a string that has all non numerical characters removed from a string
     */

    public static String removeNonNumericalChars(String s) {

        StringBuilder _s = new StringBuilder();

        for (int i = 0;i < s.length();i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c) || c == '.') _s.append(c);
        }

        return _s.toString();

    }

    /**
     * Usually used to ensure compatibility with other mods that replace classes with a child version of it
     */

    public static boolean isNameInFieldList(Field[] fields,String name) {
        List<String> fieldNames = new ArrayList<String>();
        for (Field f : fields) {
            fieldNames.add(f.getName());
        }
        return fieldNames.contains(name);
    }

    public static boolean containedByCharSequence(String s1,String s2) {
        int lastIndex = 0;
        for (char c : s2.toCharArray()) {
            for ( ; lastIndex <= s1.length() ; lastIndex++) {
                if (lastIndex == s1.length()) break;
                if (s1.charAt(lastIndex) == c) break;
            }
            if (lastIndex == s1.length()) return false;
        }
        return true;
    }

    public static String removeFirstSpaces(String s) {
        if (s.equals("")) return s;
        int nonSpaceIdx = 0;
        for (int i = 0;s.charAt(i) == ' ';i++) {
            nonSpaceIdx = i+1;
        }
        return s.substring(nonSpaceIdx);
    }

    public static void drawThiccBorderString(String s,int x,int y,int c) {
        String noColorCodeS = ApecUtils.removeColorCodes(s);
        Minecraft.getMinecraft().fontRendererObj.drawString(noColorCodeS, x + 1,y, (c >> 24) << 24);
        Minecraft.getMinecraft().fontRendererObj.drawString(noColorCodeS, x - 1, y, (c >> 24) << 24);
        Minecraft.getMinecraft().fontRendererObj.drawString(noColorCodeS, x, y + 1, (c >> 24) << 24);
        Minecraft.getMinecraft().fontRendererObj.drawString(noColorCodeS, x, y - 1, (c >> 24) << 24);
        Minecraft.getMinecraft().fontRendererObj.drawString(s, x, y, c);
    }


}
