package Apec.Commands;

import Apec.ApecMain;
import Apec.ComponentId;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class ApecGuiOpenCommand extends CommandBase {

    @Override
    public void processCommand(ICommandSender sender, String[] params) throws CommandException {
        ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER).Toggle();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "apecToggle";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

}
