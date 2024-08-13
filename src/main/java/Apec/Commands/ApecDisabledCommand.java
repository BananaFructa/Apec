package Apec.Commands;

import cc.polyfrost.oneconfig.libs.universal.ChatColor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ApecDisabledCommand extends CommandBase {
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
        sender.addChatMessage(new ChatComponentText(ChatColor.RED + "/apec is disabled when OneConfig is loaded"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
