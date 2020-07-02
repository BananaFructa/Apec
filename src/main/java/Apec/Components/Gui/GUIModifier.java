package Apec.Components.Gui;

import Apec.Component;
import Apec.ComponentId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;


public class GUIModifier extends Component {

    private  Minecraft mc = Minecraft.getMinecraft();

    public GuiIngame normalGuiIngame;
    public ApecGuiIngame apecGuiIngame;

    public GUIModifier() {
        super(ComponentId.GUI_MODIFIER);
    }

    @Override
    protected void onEnable() {
        mc.ingameGUI =  new ApecGuiIngame(mc);
    }

    @Override
    protected void onDisable() {
        mc.ingameGUI = new GuiIngameForge(mc);
    }

}
