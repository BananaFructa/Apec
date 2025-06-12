package uk.co.hexeption.apec.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.joml.Vector2f;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.settings.SettingID;

import java.util.List;

public class ApecUtils {

    public static boolean isContainedIn(String s1, String s2) {
        char[] targetChars = s2.toCharArray();
        char[] sourceChars = s1.toCharArray();

        int targetIndex = 0;
        for (char sourceChar : sourceChars) {
            if (sourceChar == targetChars[targetIndex]) {
                targetIndex++;
                if (targetIndex == targetChars.length) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean containedByCharSequence(String str, String substr) {
        if (substr.length() > str.length()) {
            return false;
        }
        int j = 0;
        for (char c : str.toCharArray()) {
            if (c == substr.charAt(j)) {
                j++;
                if (j == substr.length()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String removeFirstSpaces(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        int i = 0;
        while (i < s.length() && s.charAt(i) == ' ') {
            i++;
        }

        return s.substring(i);
    }

    public static Vector2f addVec(Vector2f a, Vector2f b) {
        return new Vector2f(a.x + b.x, a.y + b.y);
    }

    public static Vector2f scalarMultiply(Vector2f v, float s) {
        return new Vector2f(v.x * s, v.y * s);
    }

    public enum SegmentationOptions {

        TOTALLY_EXCLUSIVE, TOTALLY_INCLUSIVE, ALL_INSTANCES_RIGHT, ALL_INSTANCES_LEFT

    }

    public static String segmentString(String string, String symbol, char leftChar, char rightChar, int allowedInstancesL, int allowedInstancesR, SegmentationOptions... options) {
        boolean totallyExclusive = false, totallyInclusive = false, allInstancesR = false, allInstancesL = false;
        for (SegmentationOptions option : options) {
            if (option == SegmentationOptions.TOTALLY_EXCLUSIVE) totallyExclusive = true;
            if (option == SegmentationOptions.TOTALLY_INCLUSIVE) totallyInclusive = true;
            if (option == SegmentationOptions.ALL_INSTANCES_RIGHT) allInstancesR = true;
            if (option == SegmentationOptions.ALL_INSTANCES_LEFT) allInstancesL = true;
        }
        return segmentString(string, symbol, leftChar, rightChar, allowedInstancesL, allowedInstancesR, totallyExclusive, totallyInclusive, allInstancesR, allInstancesL);
    }

    /**
     * @param string            = The string you want to extract data from
     * @param symbol            = A string that will act as a pivot
     * @param leftChar          = It will copy all the character from the left of the pivot until it encounters this character
     * @param rightChar         = It will copy all the character from the right of the pivot until it encounters this character
     * @param allowedInstancesL = How many times can it encounter the left char before it stops copying the characters
     * @param allowedInstancesR = How many times can it encounter the right char before it stops copying the characters
     * @param totallyExclusive  = Makes so that the substring wont include the character from the left index
     * @return Returns the string that is defined by the bounds of leftChar and rightChar encountered allowedInstacesL  respectively allowedInctancesR - 1 within it
     * allowedInsracesL only if totallyExclusive = false else allowedInstacesL - 1
     */

    public static String segmentString(String string, String symbol, char leftChar, char rightChar, int allowedInstancesL, int allowedInstancesR, boolean totallyExclusive, boolean totallyInclusive, boolean allInstancesR, boolean allInstancesL) {

        int leftIdx = 0, rightIdx = 0;

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

            if (allowedInstancesL != 0 && allInstancesL) return null;
            if (allowedInstancesR != 0 && allInstancesR) return null;
            return string.substring(leftIdx + (totallyExclusive ? 1 : 0), rightIdx + (totallyInclusive ? 1 : 0));
        } else {
            return null;
        }

    }

    public static String removeAllColourCodes(String s) {
        while (s.contains("§")) {
            s = s.replace("§" + s.charAt(s.indexOf("§") + 1), "");
        }
        return s;
    }

    public static void drawOutlineText(Minecraft mc, GuiGraphics guiGraphics, String text, int x, int y, int colour) {
        String noColorText = removeAllColourCodes(text);
        guiGraphics.drawString(mc.font, noColorText, x + 1, y, (colour >> 24) << 24);
        guiGraphics.drawString(mc.font, noColorText, x - 1, y, (colour >> 24) << 24);
        guiGraphics.drawString(mc.font, noColorText, x, y + 1, (colour >> 24) << 24);
        guiGraphics.drawString(mc.font, noColorText, x, y - 1, (colour >> 24) << 24);
        guiGraphics.drawString(mc.font, text, x, y, colour);
    }

    public static void drawOutlineWrappedText(Minecraft mc, GuiGraphics guiGraphics, String text, int x, int y, int wordWrap, int colour) {
        FormattedText formattedText = FormattedText.of(text);
        guiGraphics.drawWordWrap(mc.font, formattedText, x + 1, y, wordWrap, (colour >> 24) << 24);
        guiGraphics.drawWordWrap(mc.font, formattedText, x - 1, y, wordWrap, (colour >> 24) << 24);
        guiGraphics.drawWordWrap(mc.font, formattedText, x, y + 1, wordWrap, (colour >> 24) << 24);
        guiGraphics.drawWordWrap(mc.font, formattedText, x, y - 1, wordWrap, (colour >> 24) << 24);
        guiGraphics.drawWordWrap(mc.font, formattedText, x, y, wordWrap, colour);
    }

    public static void drawWrappedText(GuiGraphics guiGraphics, String text, int x, int y, int wordWrap, int colour) {
        FormattedText formattedText = FormattedText.of(text);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, formattedText, x, y, wordWrap, colour);
    }

    /**
     * @param string = Input message
     * @brief Shown the specified message in the chat if debug messages are on
     */

    public static void showMessage(String string) {
        if (Apec.INSTANCE.settingsManager.getSettingState(SettingID.SHOW_DEBUG_MESSAGES))
            Minecraft.getInstance().player.displayClientMessage(Component.literal(string), false);
    }

    public static void showNonDebugMessage(String string) {
        Minecraft.getInstance().player.displayClientMessage(Component.literal(string), false);
    }

    // A wise man once said bubble sort is good enough when there are not a lot of elements
    public static <T> void bubbleSort(List<Integer> arr, List<T> s) {
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


}
