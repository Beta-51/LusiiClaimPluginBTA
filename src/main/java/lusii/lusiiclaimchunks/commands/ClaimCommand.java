package lusii.lusiiclaimchunks.commands;

import lusii.lusiiclaimchunks.LusiiClaimChunks;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ClaimCommand extends Command {
	public ClaimCommand() {
		super("claim");
	}
//
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (sender.getPlayer().score < LusiiClaimChunks.cost) {
			sender.sendMessage("§eInsufficient funds!");
			return true;
		}


		int cx = sender.getPlayer().chunkCoordX;
		int cz = sender.getPlayer().chunkCoordZ;
		LusiiClaimChunks.IntPair intPair = new LusiiClaimChunks.IntPair(cx,cz);
		String username = sender.getPlayer().username;
		if (LusiiClaimChunks.map.get(intPair) == null) {
			LusiiClaimChunks.map.put(intPair, Collections.singletonList(username));
			LusiiClaimChunks.saveHashMap();
			sender.sendMessage("§4Chunk claimed!");
			sender.getPlayer().score -= LusiiClaimChunks.cost;
			return true;
		} else {
			sender.sendMessage("§eThis chunk is already claimed by §r" + LusiiClaimChunks.map.get(intPair).get(0) +"!");
			return true;
		}
	}
//
	public boolean opRequired(String[] args) {
		return false;
	}
//
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender) {

	}
}
