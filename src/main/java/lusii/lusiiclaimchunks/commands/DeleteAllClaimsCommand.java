package lusii.lusiiclaimchunks.commands;

import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Objects;

import static lusii.lusiiclaimchunks.LusiiClaimChunks.deleteAllClaimedChunks;

public class DeleteAllClaimsCommand extends Command {
	public DeleteAllClaimsCommand() {
		super("unclaimall","delclaimsall");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length > 0) {
			if (Objects.equals(args[0].toLowerCase(), "confirm")) {
				deleteAllClaimedChunks(sender.getPlayer().username);
				sender.sendMessage("§3All of your claims have been erased.");
				return true;
			}
		}
		sender.sendMessage("§e§lTHIS COMMAND IS DANGEROUS!");
		sender.sendMessage("§eRerun the command with 'confirm' if you're absolutely sure!");
		return true;
    }
//
	public boolean opRequired(String[] args) {
		return false;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
