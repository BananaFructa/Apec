package Apec.Commands;

import Apec.ApecMain;
import Apec.ComponentId;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ApecMenuOpenCommand extends CommandBase {

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
        ApecMenuOpenCommand.shouldOpenGui = true;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "apecmenu";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/apecmenu";
    }

}
