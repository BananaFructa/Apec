package Apec.Components.Gui.Menu;

import Apec.Utils.ApecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageBox extends GuiScreen {

    private Minecraft mc = Minecraft.getMinecraft();

    private GuiScreen afterScreen;
    private String[] message;
    private int messageHeight = 0;

    public MessageBox(String message,GuiScreen screenToShowAfterClose) {
        message += "\n\nPress enter to continue.";
        afterScreen = screenToShowAfterClose;
        String[] split = message.split("\n");
        List<String> lines = new ArrayList<String>();
        for (String s : split) {
            List<String> wrapped = ApecUtils.stringToSizedArray(mc,s,190);
            lines.addAll(wrapped);
        }
        Object[] arr = lines.toArray();
        this.message = Arrays.copyOf(arr,arr.length,String[].class);
        messageHeight = this.message.length * 10;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);
        int xMiddle = sr.getScaledWidth()/2;
        int yMiddle = sr.getScaledHeight()/2;
        drawRect(xMiddle - 100,yMiddle - messageHeight/2 - 5,xMiddle + 100,yMiddle + messageHeight/2 + 5,0x990a0a0a);
        for (int i = 0;i < message.length;i++) {
            mc.fontRendererObj.drawString(message[i],xMiddle - 95,yMiddle - messageHeight/2 + 10 * i,0xffffffff);
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) mc.displayGuiScreen(afterScreen);
    }
}
