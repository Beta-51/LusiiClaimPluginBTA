package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class UnTrustAllCommand extends Command {
	public UnTrustAllCommand() {
		super("untrustall","claimuntrustall");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		String username = sender.getPlayer().username;
		String player;
		player = args[0];
		LusiiClaimChunks.removedPlayerFromChunkAll(username,player);
		sender.sendMessage("§3Player §r" + player + " §3untrusted in all claims.");
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
