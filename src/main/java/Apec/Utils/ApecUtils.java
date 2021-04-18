package Apec.Utils;

import Apec.ApecMain;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.util.vector.Vector2f;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ApecUtils {

    /** If you are in a fml workspace set this variable to true */
    public static boolean inFMLDebugFramework = false;

    private static String[] colorCodes = { "\u00a70","\u00a71","\u00a72","\u00a73","\u00a74","\u00a75","\u00a76","\u00a77","\u00a78","\u00a79","\u00a7a","\u00a7b","\u00a7c","\u00a7d","\u00a7e","\u00a7f" };

    public static HashMap<String,String> unObfedFieldNames = new HashMap<String,String>() {{
        if (!inFMLDebugFramework) {
            put("footer", "field_175255_h");
            put("header", "field_175256_i");
            put("upperChestInventory", "field_147016_v");
            put("lowerChestInventory", "field_147015_w");
            put("persistantChatGUI", "field_73840_e");
            put("sentMessages", "field_146248_g");
            put("streamIndicator", "field_152127_m");
            put("updateCounter", "field_73837_f");
            put("overlayPlayerList", "field_175196_v");
            put("guiIngame", "field_175251_g");
            put("chatMessages", "field_146253_i");
            put("theSlot","field_147006_u");
        }
    }};

    private static HashMap<String,Field> reflectionFieldCache = new HashMap<String, Field>();

    public static HashMap<String,String> getUnObfedMethodNames = new HashMap<String, String>() {{
        if (!inFMLDebugFramework) {
            put("handleMouseClick", "func_146984_a");
            put("drawItemStack","func_146982_a");
        }
    }};

    public static <T> T readDeclaredField(Class<?> targetType, Object target, String name) {
        try {
            if (reflectionFieldCache.containsKey(name)) {
                return (T)reflectionFieldCache.get(name).get(target);
            } else {
                Field f = targetType.getDeclaredField(unObfedFieldNames.getOrDefault(name, name));
                f.setAccessible(true);
                return (T)f.get(target);
            }
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public static void writeDeclaredField(Class<?> targetType, Object target, String name, Object value) {
        try {
            Field f = targetType.getDeclaredField(unObfedFieldNames.getOrDefault(name,name));
            f.setAccessible(true);
            f.set(target,value);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static Method getDeclaredMethod(Class<?> targetClass, String name, Class<?>... parameters) {
        try {
            Method m = targetClass.getDeclaredMethod(getUnObfedMethodNames.getOrDefault(name,name),parameters);
            m.setAccessible(true);
            return m;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    /**
     * @param s = Input string
     * @return Returns a string with all the formating tags removed
     */

    public static String removeAllCodes(String s) {
        while (s.contains("\u00a7")) {
            s = s.replace("\u00a7"+s.charAt(s.indexOf("\u00a7") + 1),"");
        }
        return s;
    }

    /**
     * @param v = An input vector
     * @return Returns true if the specified vector has a magnitude of 0
     */

    public static boolean zeroMagnitude (Vector2f v) {
        return v.x == 0 && v.y == 0;
    }

    /**
     * @param s = Input string
     * @return Returns a string with all the color tags removed
     */

    public static String removeColorCodes(String s) {
        for (String code : colorCodes) {
            s = s.replace(code,"\u00a7r");
            s = s.replace(code.toUpperCase(),"\u00a7r");
        }
        return s;
    }

    /**
     * @param a = Vector 1
     * @param b = Vector 2
     * @return Returns the sum of the 2 vectors
     */

    public static Vector2f addVec (Vector2f a,Vector2f b) {
        return new Vector2f(a.x+b.x,a.y+b.y);
    }

    public static Vector2f scalarMultiply (Vector2f v, float s) {
        return new Vector2f(v.x*s,v.y*s);
    }

    public static Vector2f subVec(Vector2f a,Vector2f b) {
        return new Vector2f(a.x - b.x,a.y - b.y);
    }

    /**
     * @brief Shown the specified message in the chat if debug messages are on
     * @param string = Input message
     */

    public static void showMessage(String string) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_DEBUG_MESSAGES))
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(string));
    }

    public static void showNonDebugMessage(String string) {
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
     * @param l = A list of strings
     * @return Returns the maximum display length of a string from the list
     */

    public static int getMaxStringWidth(List<String> l) {
        int w = 0;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        for (String _l : l) {
            int _w =  fr.getStringWidth(_l);
            if (w < _w) {
                w = _w;
            }
        }
        return w;
    }

    /**
     * Usually used to ensure compatibility with other mods that replace classes with a child version of it
     * @param fields = Any field array
     * @param name = The name that has to be checked
     * @return Returns true if there is a field present in the array with the specified name
     */

    public static boolean isNameInFieldList(Field[] fields,String name) {
        List<String> fieldNames = new ArrayList<String>();
        for (Field f : fields) {
            fieldNames.add(f.getName());
        }
        return fieldNames.contains(name);
    }

    /**
     * @param s1 = The string in which the sequence will be searched
     * @param s2 = The char sequence
     * @return Returns true if the specified character sequence is present in the input string.
     * The characters can have other characters between them only the order in which they
     * exist matters.
     */

    public static boolean containedByCharSequence(String s1,String s2) {

        char[] c = s2.toCharArray();
        char[] s = s1.toCharArray();
        int cIdx = 0;
        for (int i = 0;i < s.length && cIdx < c.length;i++) {
            if (s[i] == c[cIdx]) cIdx++;
        }

        return cIdx == c.length;

    }

    /**
     * @param l = A list of strings
     * @param regex = Regex
     * @return Returns true if the input string is contained in a string from the list
     */

    public static boolean doesListContainRegex(List<String> l,String regex) {
        for (String _l : l) {
            if (_l.contains(regex)) return true;
        }
        return false;
    }

    /**
     * @param s = Input string
     * @return Returns a string without white spaces at the start of it, if any are present
     */

    public static String removeFirstSpaces(String s) {
        if (s.equals("")) return s;
        int nonSpaceIdx = 0;
        for (int i = 0;s.charAt(i) == ' ';i++) {
            nonSpaceIdx = i+1;
        }
        return s.substring(nonSpaceIdx);
    }

    /**
     * @param l A list of strings
     * @return Returns an ordered list of the input strings by their width
     */

    public static List<String> orderByWidth (List<String> l) {
        List<Integer> arr = new ArrayList<Integer>();
        for (String s : l) {
            arr.add(Minecraft.getMinecraft().fontRendererObj.getStringWidth(s));
        }
        bubbleSort(arr,l);
        return l;
    }


    // A wise man once said bubble sort is good enough when there are not a lot of elements
    public static <T> void  bubbleSort(List<Integer> arr,List<T> s) {
        int n = arr.size();
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (arr.get(j) < arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);

                    T _temp = s.get(j);
                    s.set(j, s.get(j + 1));
                    s.set(j + 1, _temp);
                }
    }

    /**
     * @brief The function used to draw all ingame gui strings
     * @param s = String to be drawn
     * @param x = x position
     * @param y = y position
     * @param c = color
     */
    public static void drawThiccBorderString(String s,int x,int y,int c) {
        String noColorCodeS = ApecUtils.removeColorCodes(s);
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.BORDER_TYPE)) {
            Minecraft.getMinecraft().fontRendererObj.drawString(noColorCodeS, x + 1, y, (c >> 24) << 24);
            Minecraft.getMinecraft().fontRendererObj.drawString(noColorCodeS, x - 1, y, (c >> 24) << 24);
            Minecraft.getMinecraft().fontRendererObj.drawString(noColorCodeS, x, y + 1, (c >> 24) << 24);
            Minecraft.getMinecraft().fontRendererObj.drawString(noColorCodeS, x, y - 1, (c >> 24) << 24);
            Minecraft.getMinecraft().fontRendererObj.drawString(s, x, y, c);
        } else {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s,x,y,c);
        }
    }

    /**
     * @param event = Gui open input event
     * @return Returns a tuple containing the lower and upper inventory of the gui linked with the event
     */

    public static Tuple<IInventory,IInventory> GetUpperLowerFromGuiEvent(GuiOpenEvent event) {
        try {
            /** This is to ensure that there is not an Inner class of the GuiChes class forced by a mod , ughh ughh looking at you Skypixel */
            String upperFieldName = ApecUtils.unObfedFieldNames.get("upperChestInventory");
            String lowerFieldName = ApecUtils.unObfedFieldNames.get("lowerChestInventory");
            if (ApecUtils.isNameInFieldList(event.gui.getClass().getDeclaredFields(), upperFieldName) &&
                    ApecUtils.isNameInFieldList(event.gui.getClass().getDeclaredFields(), lowerFieldName)) {
                IInventory upper = (IInventory) FieldUtils.readDeclaredField(event.gui, upperFieldName, true);
                IInventory lower = (IInventory) FieldUtils.readDeclaredField(event.gui, lowerFieldName, true);
                return new Tuple<IInventory, IInventory>(upper,lower);
            } else {
                IInventory upper = (IInventory) FieldUtils.readField(event.gui, upperFieldName, true);
                IInventory lower = (IInventory) FieldUtils.readField(event.gui, lowerFieldName, true);
                return new Tuple<IInventory, IInventory>(upper,lower);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Not used right now
    public static List<Vector2f> AddVecToList(List<Vector2f> vl, Vector2f vec) {
        for (Vector2f v : vl) {
            v = addVec(v, vec);
        }
        return vl;
    }

    /**
     * @return A list of vectors that represents the sum of the two inputed list of vectors
     */
    public static List<Vector2f> AddVecListToList(List<Vector2f> vl1,List<Vector2f> vl2) {
        assert (vl1.size() < vl2.size());
        for (int i = 0;i < vl1.size();i++) {
            vl1.set(i,addVec(vl1.get(i), vl2.get(i)));
        }
        return vl1;
    }

    // Usefull for not dealing with those pesky color codes

    /**
     * @param Seq = The sequence of characters
     * @param s = The string
     * @return Returns a string with the char sequence removed
     */
    public static String RemoveCharSequence (String Seq,String s) {
        char[] csq = Seq.toCharArray();
        String result = "";
        int CurrentInSequence = 0;
        boolean SequenceEnded = false;
        for (int i = 0;i < s.length();i++) {
            if (!SequenceEnded) {
                if (csq[CurrentInSequence] == s.charAt(i)) {
                    CurrentInSequence++;
                    if (CurrentInSequence == csq.length) SequenceEnded = true;
                    continue;
                }
            }
            result += String.valueOf(s.charAt(i));
        }
        return result;
    }

    /**
     * @param v A float value
     * @return Cuts the decimals to only 2
     */
    public static float ReduceToTwoDecimals(float v) {
        return (float)((int)(v*100)) / 100.0f;
    }

    /**
     * @param string = The string you want to extract data from
     * @param symbol = A string that will act as a pivot
     * @param leftChar = It will copy all the character from the left of the pivot until it encounters this character
     * @param rightChar = It will copy all the character from the right of the pivot until it encounters this character
     * @param allowedInstancesL = How many times can it encounter the left char before it stops copying the characters
     * @param allowedInstancesR = How many times can it encounter the right char before it stops copying the characters
     * @param totallyExclusive = Makes so that the substring wont include the character from the left index
     * @return Returns the string that is defined by the bounds of leftChar and rightChar encountered allowedInstacesL  respectively allowedInctancesR - 1 within it
     *         allowedInsracesL only if totallyExclusive = false else allowedInstacesL - 1
     */

    public static String segmentString(String string,String symbol,char leftChar,char rightChar,int allowedInstancesL,int allowedInstancesR,boolean totallyExclusive,boolean totallyInclusive) {

        int leftIdx = 0,rightIdx = 0;

        if (string.contains(symbol)) {

            int symbolIdx = string.indexOf(symbol);

            for (int i = 0; symbolIdx - i > -1; i++) {
                leftIdx = symbolIdx - i;
                if (string.charAt(symbolIdx - i) == leftChar) allowedInstancesL--;
                if (allowedInstancesL == 0) {
                    break;
                }
            }

            symbolIdx += symbol.length() - 1;

            for (int i = 0; symbolIdx + i < string.length(); i++) {
                rightIdx = symbolIdx + i;
                if (string.charAt(symbolIdx + i) == rightChar) allowedInstancesR--;
                if (allowedInstancesR == 0) {
                    break;
                }
            }

            return string.substring(leftIdx + (totallyExclusive ? 1 : 0), rightIdx + (totallyInclusive ? 1 : 0));
        } else {
            return null;
        }

    }

    public static int RomanSymbolToValue(char s) {
        switch (s) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
        }
        return 0;
    }

    public static int RomanStringToValue(String str) {
        int res = 0;
        for (int i = 0; i < str.length(); i++)
        {
            int s1 = RomanSymbolToValue(str.charAt(i));
            if (i + 1 < str.length())
            {
                int s2 = RomanSymbolToValue(str.charAt(i + 1));
                if (s1 >= s2) {
                    res = res + s1;
                }
                else
                {
                    res = res + s2 - s1;
                    i++;
                }
            }
            else {
                res = res + s1;
            }
        }
        return res;
    }

    public static String applyTagOnUrl(String url,String tag) {
        return url.replace("__TAG__",tag);
    }

    public static List<String> stringToSizedArray(Minecraft mc,String s,int widthToWrap) {
        List<String> lines = new ArrayList<String>();
        if (s.equals("")) {
            lines.add("");
            return lines;
        }
        String[] words = s.split(" ");
        String currentSentence = "";
        for (String word : words) {
            if (mc.fontRendererObj.getStringWidth(currentSentence + word) < widthToWrap) {
                if (currentSentence.equals("")) {
                    currentSentence = word;
                } else {
                    currentSentence += " " + word;
                }
            } else {
                lines.add(currentSentence);
                currentSentence = word;
            }
        }
        if (!currentSentence.equals("")) {
            lines.add(currentSentence);
        }
        return lines;
    }

    public static <T> T[] listToArray(List<T> list,Class<T[]> type) {
        Object[] arr = list.toArray();
        return Arrays.copyOf(arr,arr.length,type);
    }
}
