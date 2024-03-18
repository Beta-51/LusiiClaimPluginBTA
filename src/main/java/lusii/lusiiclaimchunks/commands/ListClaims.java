package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.ArrayList;
import java.util.Objects;

import static lusii.lusiiclaimchunks.LusiiClaimChunks.deleteAllClaimedChunks;

public class ListClaims extends Command {
	public ListClaims() {
		super("listclaims","claims", "claimlist", "listclaim");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		String theResults = LusiiClaimChunks.listClaimedChunks(sender.getPlayer().username).toString();
		theResults = theResults.replace(" , ", ", ");
		theResults = theResults.replace("[", "");
		theResults = theResults.replace("]", "");
		sender.sendMessage("§3Your claims: §4"+theResults);


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
