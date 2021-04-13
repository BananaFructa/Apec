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

public class ApecComponentTogglerCommand extends CommandBase {

    ComponentId componentId;
    String command;
    boolean scheduleOnTick;
    boolean shouldToggle = false;

    Runnable toRun = new Runnable() {
        @Override
        public void run() {
            ApecMain.Instance.getComponent(componentId).Toggle();
        }
    };

    public ApecComponentTogglerCommand(ComponentId component,String command,boolean scheduleOnTick) {
        componentId = component;
        this.command = command;
        this.scheduleOnTick = scheduleOnTick;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] params) throws CommandException {
        if (!scheduleOnTick) Minecraft.getMinecraft().addScheduledTask(toRun);
        else shouldToggle = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (shouldToggle) {
            shouldToggle = false;
            toRun.run();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return command;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/"+command;
    }

}
