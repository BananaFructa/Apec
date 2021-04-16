package Apec.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

/**
 * Allows for setting a delta constant to the real scaled resolution
 */

public class FakedScaledResolution extends ScaledResolution {

    private final double scaledWidthD;
    private final double scaledHeightD;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    private int X,Y;

    public FakedScaledResolution(Minecraft mc,int X,int Y)
    {
        super(mc);
        this.X = X;
        this.Y = Y;
        this.scaledWidth = mc.displayWidth;
        this.scaledHeight = mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = mc.gameSettings.guiScale;

        if (i == 0)
        {
            i = 1000;
        }

        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240)
        {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1)
        {
            --this.scaleFactor;
        }

        this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
        this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
        this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
    }

    public int getScaledWidth()
    {
        return X;
    }

    public int getScaledHeight()
    {
        return Y;
    }

    public double getScaledWidth_double()
    {
        return X;
    }

    public double getScaledHeight_double()
    {
        return Y;
    }

    public int getScaleFactor()
    {
        return this.scaleFactor;
    }

}
