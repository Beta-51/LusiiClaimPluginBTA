package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
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
				int delCount = deleteAllClaimedChunks(sender.getPlayer().username);
				int fullRefund = LusiiClaimChunks.getFullRefund(delCount);
				sender.getPlayer().score += fullRefund;

//				sender.sendMessage("§3All of your claims have been erased.");
				sender.sendMessage("§eUnclaimed §4" + delCount + "§e chunks.");
				sender.sendMessage("§1Refunded §4" + fullRefund + "§1 points.");

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
