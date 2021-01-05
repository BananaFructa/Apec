package Apec.Components.Gui.GuiIngame;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;

public class ApecGuiIngame extends GuiIngameForge {

    public ApecGuiIngame(Minecraft mc) {
        super(mc);
    }

    public String GetHighlightText() {
        if (this.highlightingItemStack != null) {
            return this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, this.highlightingItemStack.getDisplayName());
        } else {
            return "Item";
        }
    }
}
