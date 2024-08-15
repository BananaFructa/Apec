package Apec.Commands;

import Apec.ApecMain;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class ApecOneConfigCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "apec";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/apec";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        ApecMain.oneConfig.openGui();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
