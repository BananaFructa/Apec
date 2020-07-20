package Apec.Components.Gui.ContainerGuis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;

import java.util.List;

public class AuctionHouseGui extends AuctionHouseContainer {


    public AuctionHouseGui(IInventory upperInv, IInventory lowerInv)
    {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().thePlayer));
        this.allowUserInput = false;
        int i = 222;
        int j = i - 108;
        this.ySize = j + lowerInv.getSizeInventory() / 9 * 18;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {

    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        drawRect(sr.getScaledWidth()/2-100,sr.getScaledHeight()/2-100,sr.getScaledWidth()/2+100,sr.getScaledHeight()/2+70,0xaa353535);
        drawRect(sr.getScaledWidth()/2+110,sr.getScaledHeight()/2-100,sr.getScaledWidth()/2+190,sr.getScaledHeight()/2+70,0xaa353535);
        drawRect(sr.getScaledWidth()/2+200,sr.getScaledHeight()/2-100,sr.getScaledWidth()/2+280,sr.getScaledHeight()/2+70,0xaa353535);
        List<String> sortLines = getSortText();
        for (int i = 0;i < sortLines.size();i++) {
            mc.fontRendererObj.drawString(sortLines.get(i),sr.getScaledWidth()/2+113,sr.getScaledHeight()/2-97 + 10*i,0xffffff);
        }
        List<String> modeLines = getModeText();
        for (int i = 0;i < modeLines.size();i++) {
            mc.fontRendererObj.drawString(modeLines.get(i),sr.getScaledWidth()/2+113,sr.getScaledHeight()/2-27+10*i,0xffffff);
        }
        List<String> rarityLines = getRarityText();
        for (int i = 0;i < rarityLines.size();i++) {
            mc.fontRendererObj.drawString(rarityLines.get(i),sr.getScaledWidth()/2+203,sr.getScaledHeight()/2-97+10*i,0xffffff);
        }
        mc.fontRendererObj.drawString("Auction Browser" + getPageText().replace("("," ").replace(")",""),((sr.getScaledWidth()/2-97)),((sr.getScaledHeight()/2-97)),0xffffff);
    }


}
