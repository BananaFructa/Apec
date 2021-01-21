package Apec.Commands;

import Apec.ApecMain;
import Apec.ComponentId;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class ApecGuiOpenCommand extends CommandBase {

    @Override
    public void processCommand(ICommandSender sender, String[] params) throws CommandException {
        ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER).Toggle();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "apectoggle";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/apectoggle";
    }

}
