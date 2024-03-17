package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.*;

public class TrustedCommand extends Command {
	public TrustedCommand() {
		super("trusted","claimtrusted","claimwho");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		if (LusiiClaimChunks.map.get(intPair) == null) {
			sender.sendMessage("§eNo one owns this chunk!");
			return true;
		}
		List<String> trusted = new ArrayList<>(Collections.singletonList(""));
		for (int i = 1; i < LusiiClaimChunks.map.get(intPair).size(); i++) {
			trusted.add(LusiiClaimChunks.map.get(intPair).get(i));
		}


		String theResults = trusted.toString().replace(" , ", ", ");
		theResults = theResults.replaceFirst(", ", "");
		theResults = theResults.replace("[", "");
		theResults = theResults.replace("]", "");

		sender.sendMessage("§3Chunk owner: §r"+ LusiiClaimChunks.map.get(intPair).get(0));
		sender.sendMessage("§3Trusted players: §r"+theResults);

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
