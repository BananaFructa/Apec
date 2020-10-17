package Apec.Components.Gui.GuiIngame;

import Apec.ApecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.List;

public class ApecGuiIngame extends GuiIngameForge {

    public ApecGuiIngame(Minecraft mc) {
        super(mc);
    }

    public void setChatData (List<ChatLine> messages) {
        //FieldUtils.writeDeclaredField(this.persistantChatGUI, "field_146253_i",messages, true);
        MinecraftForge.EVENT_BUS.register(this);
        for (int i = messages.size() - 1;i > -1;i--) {
            this.persistantChatGUI.printChatMessage(messages.get(i).getChatComponent());
        }
    }

    public void setChatSentMessages(List<String> messages) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(this.persistantChatGUI, ApecUtils.unObfedFieldNames.get("sentMessages"),messages,true);
    }

    public String GetHighlightText() {
        if (this.highlightingItemStack != null) {
            return this.highlightingItemStack.getItem().getHighlightTip(this.highlightingItemStack, this.highlightingItemStack.getDisplayName());
        } else {
            return "Item";
        }
    }



}
