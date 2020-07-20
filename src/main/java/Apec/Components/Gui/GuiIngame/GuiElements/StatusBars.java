package Apec.Components.Gui.GuiIngame.GuiElements;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.DataExtractor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

public class StatusBars extends GUIComponent {

    public StatusBars () {
        super(GUIModifier.GUiComponentID.STAT_BARS);
    }

    @Override
    public void drawTex(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr) {
        GuiIngame gi = Minecraft.getMinecraft().ingameGUI;

        Vector2f StatBar = new Vector2f(sr.getScaledWidth() - 190, 15);
        StatBar = ApecUtils.addVec(StatBar,delta_position);

        mc.renderEngine.bindTexture(new ResourceLocation(ApecMain.modId, "gui/statBars.png"));

        if (ps.Ap != 0) {
            gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y, 0, 60, 182, 5);
            gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y, 0, 65, (int) (((float) ps.Ap / (float) ps.BaseAp) * 49f), 5);
            gi.drawTexturedModalRect((int) StatBar.x + 51, (int) StatBar.y, 51, 65, (int) (((float) ps.Hp / (float) ps.BaseHp) * 131f), 5);
        } else {
            gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y, 0, 0, 182, 5);
            gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y, 0, 5, (int) (((float) ps.Hp / (float) ps.BaseHp) * 182f), 5);
        }
        gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y + 19, 0, 10, 182, 5);
        gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y + 19, 0, 15, (int) (((float) ps.Mp / (float) ps.BaseMp) * 182f), 5);
        gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y + 19 * 2, 0, 30, 182, 5);
        gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y + 19 * 2, 0, 35, (int) (this.mc.thePlayer.experience * 182f), 5);

        if (mc.thePlayer.isInsideOfMaterial(Material.water)) {
            float airPrec = mc.thePlayer.getAir() / 300f;
            if (airPrec < 0) airPrec = 0;
            gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y + 19*3, 0, 40, 182, 5);
            gi.drawTexturedModalRect((int) StatBar.x, (int) StatBar.y + 19*3, 0, 45, (int)(182f * airPrec), 5);
        }


    }

    @Override
    public void draw(DataExtractor.PlayerStats ps, DataExtractor.ScoreBoardData sd,DataExtractor.OtherData od, ScaledResolution sr) {
        Vector2f StatBar = new Vector2f(sr.getScaledWidth() - 190, 15);

        StatBar = ApecUtils.addVec(StatBar,delta_position);

        String HPString = ps.Hp + "/" + ps.BaseHp + " HP";
        ApecUtils.drawThiccBorderString(HPString, (int) StatBar.x + 112 + 70 - mc.fontRendererObj.getStringWidth(HPString), (int) StatBar.y - 10, 0xd10808);
        String MPString = ps.Mp + "/" + ps.BaseMp + " MP";
        ApecUtils.drawThiccBorderString(MPString, (int) StatBar.x + 112 + 70 - mc.fontRendererObj.getStringWidth(MPString), (int) StatBar.y - 10 + 19, 0x1139bd);
        String XPString = "Lvl " + this.mc.thePlayer.experienceLevel + " XP";
        ApecUtils.drawThiccBorderString(XPString, (int) StatBar.x + 112 + 70 - mc.fontRendererObj.getStringWidth(XPString), (int) StatBar.y - 10 + 19*2, 0x80ff20);

        if (ps.Ap != 0) {
            String APString = ps.Ap + "/" + ps.BaseAp + " AP";
            ApecUtils.drawThiccBorderString(APString, (int)StatBar.x + 112 + 65 - mc.fontRendererObj.getStringWidth(APString) - mc.fontRendererObj.getStringWidth(HPString),(int)StatBar.y - 10,0xC8AC35);
        }

        if (mc.thePlayer.isInsideOfMaterial(Material.water)) {
            float airPrec = (mc.thePlayer.getAir() / 300f) * 100;
            if (airPrec < 0) airPrec = 0;
            String ARString = (int) airPrec + "% Air";
            ApecUtils.drawThiccBorderString(ARString, (int) StatBar.x + 112 + 70 - mc.fontRendererObj.getStringWidth(ARString), (int) StatBar.y - 10 + 19 * 3, 0x8ba6b2);
        }

    }


}
