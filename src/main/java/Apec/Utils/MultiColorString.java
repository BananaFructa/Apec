package Apec.Utils;

import net.minecraft.client.Minecraft;

public class MultiColorString {
    private int x, y, shiftValue = 0;
    protected Minecraft mc = Minecraft.getMinecraft();

    private String[] stringSet;
    private int[] colorSet;

    public MultiColorString() {

    }

    public MultiColorString(String[] stringSet, int[] colorSet, int x, int y) {
        this(stringSet, colorSet);
        this.x = x;
        this.y = y;
    }

    public MultiColorString(String[] stringSet, int[] colorSet) {
        this.stringSet = stringSet;
        this.colorSet = colorSet;
    }

    public MultiColorString(String[] stringSet, Integer[] colorSet) {
        this.stringSet = stringSet;
        int[] i = new int[colorSet.length];
        for (int j = 0; j < colorSet.length; j++) {
            i[j] = colorSet[j].intValue();
        }
        this.colorSet = i;
    }

    public void setString(String[] stringSet, int[] colorSet) {
        this.stringSet = stringSet;
        this.colorSet = colorSet;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setShift(int shiftValue) {
        this.shiftValue = shiftValue;
    }

    public int getStringWidth() {
        int whidh = 0;
        for (String string : stringSet) {
            whidh += mc.fontRendererObj.getStringWidth(string);
        }
        return whidh;
    }

    public String[] getStringSet() {
        return this.stringSet;
    }

    public int[] getColorSet() {
        return this.colorSet;
    }

    public void render() {
        int nowX = this.x;
        for (int i = 0; i < this.stringSet.length; i++) {
            if (this.stringSet != null) {
                ApecUtils.drawStylizedString(stringSet[i], nowX + this.shiftValue, this.y, colorSet[i]);
                nowX += mc.fontRendererObj.getStringWidth(stringSet[i]);
            }
        }
    }
}
