package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Objects;

public class TrustedCommand extends Command {
	public TrustedCommand() {
		super("trusted","claimtrusted","claimwho");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		if (!LusiiClaimChunks.isChunkClaimed(intPair)) {
			sender.sendMessage("§eNo one owns this chunk!");
			return true;
		}
		String owner = Objects.requireNonNull(LusiiClaimChunks.getTrustedPlayersInChunk(intPair)).get(0);
		String theResults = Objects.requireNonNull(LusiiClaimChunks.getTrustedPlayersInChunk(intPair)).toString().replace(" , ", ", ");
		theResults = theResults.replaceFirst(", ", "");
		theResults = theResults.replace("[", "");
		theResults = theResults.replace("]", "");
		theResults = theResults.replaceFirst(owner, "");

		sender.sendMessage("§3Chunk owner: §r" + owner);
		sender.sendMessage("§3Trusted players: §r" + theResults);

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
