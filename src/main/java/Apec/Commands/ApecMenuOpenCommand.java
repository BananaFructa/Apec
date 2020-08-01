package Apec.Commands;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Components.Gui.Menu.ApecMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public abstract class ApecMenuOpenCommand extends CommandBase {

    public static boolean shouldOpenGui = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (shouldOpenGui) {
            ApecMain.Instance.getComponent(ComponentId.SETTINGS_MENU).Toggle();
            shouldOpenGui = false;
        }
    }

    public ApecMenuOpenCommand() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        // TODO: awful in any way possible, there has to be a better method. Too bad!
        Thread awaitThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (Minecraft.getMinecraft().currentScreen != null);
                ApecMenuOpenCommand.shouldOpenGui = true;

            }
        });
        awaitThread.start();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "apecMenu";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

}
